package utn.back.mordiscoapi.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Manejador global de excepciones para la aplicación.
 * Proporciona respuestas consistentes para diferentes tipos de errores.
 */
@RestControllerAdvice // Anotación que indica que esta clase es un manejador de excepciones global
@Slf4j // Anotación de Lombok para el registro de logs
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ConstraintViolationMessageResolver messageResolver;

    /**
     * Maneja excepciones generales no capturadas.
     *
     * @param ex la excepción capturada
     * @return una respuesta con un mensaje de error genérico y estado HTTP 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Ocurrió un error inesperado",
                        LocalDateTime.now()
                ));
    }

    /**
     * Maneja excepciones de credenciales incorrectas.
     *
     * @return una respuesta con un mensaje de error y estado HTTP 401
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Login fallido: credenciales inválidas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Credenciales inválidas",
                        LocalDateTime.now()
                ));
    }

    /**
     * Maneja excepciones de acceso denegado.
     *
     * @return una respuesta con un mensaje de error y estado HTTP 403
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Acceso denegado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(
                        HttpStatus.FORBIDDEN.value(),
                        "Acceso denegado",
                        LocalDateTime.now()
                ));
    }

    /**
     * Maneja excepciones de seguridad
     *
     * @return una respuesta con un mensaje de error y estado HTTP 401
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleSecurity(SecurityException ex) {
        log.error("Error de seguridad: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        HttpStatus.UNAUTHORIZED.value(),
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }

    /**
     * Maneja violaciones de integridad de datos, como claves únicas duplicadas.
     *
     * @param ex la excepción de violación de integridad de datos
     * @return una respuesta con un mensaje de error y estado HTTP 400
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String rootMessage = Optional.ofNullable(ex.getRootCause())
                .map(Throwable::getMessage)
                .orElse("");

        String userMessage = messageResolver.resolveMessage(rootMessage);

        return Map.of(
                "error", "Violación de integridad de datos",
                "message", userMessage
        );
    }

    /**
     * Maneja errores de validación en los parámetros en funciones de los controladores.
     *
     * @param ex la excepción de validación de la funciones del manejador
     * @return una respuesta con los errores de validación y estado HTTP 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public Map<String, String> handleHandlerMethodValidation(HandlerMethodValidationException ex) {
        Map<String, String> errors = new HashMap<>();
        String field = "error";
        for (var error : ex.getAllErrors()) {
            String message = error.getDefaultMessage();
            errors.put(field, message);
        }
        return errors;
    }

    /**
     * Maneja validaciones fallidas en los DTOs.
     *
     * @param ex la excepción de validación de argumentos de la función
     * @return una respuesta con los errores de validación y estado HTTP 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    /**
     * Maneja errores de conversión de tipos de argumentos, especialmente para enumeraciones.
     *
     * @param ex la excepción de tipo de argumento no coincidente
     * @return una respuesta con un mensaje de error y estado HTTP 400
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleEnumConversionError(MethodArgumentTypeMismatchException ex) {
        Map<String, String> errors = new HashMap<>();
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            Object[] enumConstants = ex.getRequiredType().getEnumConstants();
            StringBuilder valores = new StringBuilder();
            for (int i = 0; i < enumConstants.length; i++) {
                valores.append(enumConstants[i]);
                if (i < enumConstants.length - 1) valores.append(", ");
            }
            errors.put("error", "Valor de enumeración no válido");
            errors.put("message", "El valor '" + ex.getValue() + "' no es válido para el parámetro '" + ex.getName() + "'. Valores permitidos: " + valores);
            return errors;
        }
        errors.put("error", "Tipo de argumento no válido");
        errors.put("message", ex.getMessage());
        return errors;
    }

    /**
     * Maneja solicitudes JSON malformadas.
     *
     * @param ex la excepción de mensaje HTTP no legible
     * @return una respuesta con un mensaje de error y estado HTTP 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Map<String, String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "JSON malformado");
        errors.put("message", ex.getMessage());
        return errors;
    }

    /**
     * Maneja excepciones de recursos no encontrados.
     *
     * @param ex la excepción de recurso no encontrado
     * @return una respuesta con un mensaje de error y estado HTTP 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoResourceFoundException.class)
    public Map<String, String> handleNoResourceFoundException(NoResourceFoundException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "No existe el recurso");
        errors.put("message", ex.getMessage());
        return errors;
    }

    /**
     * Maneja métodos HTTP no soportados.
     *
     * @param ex la excepción de función no soportado
     * @return una respuesta con un mensaje de error y estado HTTP 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Map<String, String> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Método no soportado");
        errors.put("message", ex.getMessage());
        return errors;
    }



    /**
     * Maneja parámetros faltantes en la solicitud.
     *
     * @param ex la excepción de parámetro de solicitud faltante
     * @return una respuesta con un mensaje de error y estado HTTP 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Map<String, String> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Parámetro faltante");
        errors.put("message", ex.getMessage());
        return errors;
    }

    /**
     * Maneja la excepción personalizada BadRequestException.
     *
     * @param e la excepción capturada
     * @return una respuesta con el mensaje de error y estado HTTP 400
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> badRequest(BadRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        e.getMessage(),
                        LocalDateTime.now()
                ));
    }

    /**
     * Maneja la excepción personalizada InternalServerErrorException.
     *
     * @param e la excepción capturada
     * @return una respuesta con el mensaje de error y estado HTTP 500
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorResponse> internalServerError(InternalServerErrorException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        e.getMessage(),
                        LocalDateTime.now()
                ));
    }

    /**
     * Maneja la excepción personalizada NotFoundException.
     *
     * @return una respuesta con el mensaje de error y estado HTTP 404
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }



    public record ErrorResponse(int status, String message, LocalDateTime timestamp) {}
}