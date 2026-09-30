package utn.back.mordiscoapi.common.exception;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import utn.back.mordiscoapi.common.constraint.ConstraintViolationMessageResolver;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class GlobalExceptionHandlerTest {
    private static final String DATABASE_DETAILS = "Duplicate entry 'db-secret@example.test' for key 'UK_usuario_email'; "
            + "INSERT INTO usuarios(password) VALUES ('sql-secret'); jdbc:mysql://db.internal/mordisco";
    private static final String SENSITIVE_EXCEPTION = "com.example.SecretDriver: SELECT * FROM users at "
            + "/private/credentials.json; Bearer SECRET_CREDENTIAL";

    private final ConstraintViolationMessageResolver resolver = new ConstraintViolationMessageResolver();
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler(resolver);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new ExceptionProbeController())
                .setControllerAdvice(handler)
                .build();
    }

    @Test
    void dataIntegrityResponseContainsOnlyResolvedSafeMessage() throws Exception {
        MvcResult result = mockMvc.perform(get("/probe/database")).andReturn();
        String body = result.getResponse().getContentAsString();

        assertEquals(400, result.getResponse().getStatus());
        assertEquals("Violación de integridad de datos", json(result).get("error").asText());
        assertEquals("El email ya está registrado.", json(result).get("message").asText());
        assertFalse(json(result).has("debug"));
        assertDoesNotContain(body, "db-secret@example.test", "INSERT INTO", "sql-secret", "jdbc:mysql://", "UK_usuario_email");
    }

    @Test
    void unavailablePaymentReturnsSafeServiceUnavailableResponse() throws Exception {
        assertSafePaymentUnavailable(mockMvc.perform(get("/probe/payment-unavailable")).andReturn());
        assertSafePaymentUnavailable(mockMvc.perform(get("/probe/payment-unavailable-wrapped")).andReturn());
    }

    @Test
    void unexpectedAndExplicitInternalErrorsReturnGenericFiveHundredResponses() throws Exception {
        assertGenericServerError(mockMvc.perform(get("/probe/unexpected")).andReturn());
        assertGenericServerError(mockMvc.perform(get("/probe/internal")).andReturn());
    }

    @Test
    void frameworkErrorsDoNotExposeRequestValuesClassNamesOrPaths() throws Exception {
        MvcResult malformed = mockMvc.perform(post("/probe/malformed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"credential\":\"MALFORMED_JSON_SECRET\","))
                .andReturn();
        assertError(malformed, 400, "JSON malformado", "No se pudo leer el cuerpo de la solicitud.",
                "MALFORMED_JSON_SECRET", "HttpMessageNotReadableException", "com.fasterxml.jackson", "/probe/malformed");

        MvcResult conversion = mockMvc.perform(get("/probe/conversion")
                        .queryParam("privateState", "REQUEST_VALUE_SECRET"))
                .andReturn();
        assertError(conversion, 400, "Valor de enumeración no válido",
                "El valor de enumeración proporcionado no es válido.",
                "REQUEST_VALUE_SECRET", "privateState", "GlobalExceptionHandlerTest", "java.lang");

        MvcResult missingParameter = mockMvc.perform(get("/probe/missing")).andReturn();
        assertError(missingParameter, 400, "Parámetro faltante", "Falta un parámetro requerido.", "privateToken");

        MvcResult unsupportedMethod = mockMvc.perform(get("/probe/method/PRIVATE_PATH_SECRET")).andReturn();
        assertError(unsupportedMethod, 400, "Método no soportado",
                "El método HTTP solicitado no está permitido.", "PRIVATE_PATH_SECRET", "/probe/method");

        MvcResult missingResource = mockMvc.perform(get("/probe/no-resource")).andReturn();
        assertError(missingResource, 400, "No existe el recurso", "No existe el recurso solicitado.",
                "PRIVATE_RESOURCE_PATH", "NoResourceFoundException", "/private/");
    }

    @Test
    void securityFailuresUseSafeMessagesAndKeepTheirStatuses() throws Exception {
        MvcResult security = mockMvc.perform(get("/probe/security")).andReturn();
        assertEquals(401, security.getResponse().getStatus());
        assertEquals("Acceso no autorizado", json(security).get("message").asText());
        assertDoesNotContain(security.getResponse().getContentAsString(), "Bearer SECRET_CREDENTIAL", "SecretDriver");

        MvcResult accessDenied = mockMvc.perform(get("/probe/access-denied")).andReturn();
        assertEquals(403, accessDenied.getResponse().getStatus());
        assertEquals("Acceso denegado", json(accessDenied).get("message").asText());
        assertDoesNotContain(accessDenied.getResponse().getContentAsString(), "Bearer SECRET_CREDENTIAL", "SecretDriver");
    }

    @Test
    void userSafeDomainAndValidationMessagesKeepTheirStatuses() throws Exception {
        MvcResult domainError = mockMvc.perform(post("/probe/domain-error")).andReturn();
        assertEquals(400, domainError.getResponse().getStatus());
        assertEquals(400, json(domainError).get("status").asInt());
        assertEquals("El pedido ya está cerrado.", json(domainError).get("message").asText());

        MvcResult validationError = mockMvc.perform(post("/probe/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"displayName\":\"\"}"))
                .andReturn();
        assertEquals(400, validationError.getResponse().getStatus());
        assertEquals("El nombre es obligatorio.", json(validationError).get("displayName").asText());
    }

    @Test
    void exceptionLogsNeverIncludeSensitiveMessagesOrThrowableDetails() {
        Logger logger = (Logger) LoggerFactory.getLogger(GlobalExceptionHandler.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        try {
            handler.handleGeneralException(new IllegalStateException(SENSITIVE_EXCEPTION));
            handler.handleDataIntegrityViolation(new DataIntegrityViolationException("database failure", new SQLException(DATABASE_DETAILS)));
            handler.handleSecurity(new SecurityException("Bearer SECRET_CREDENTIAL"));
            handler.handleAccessDenied(new AuthorizationDeniedException(SENSITIVE_EXCEPTION));
            handler.handleAccountDeactivated(new AccountDeactivatedException("Bearer SECRET_CREDENTIAL"));

            assertFalse(appender.list.isEmpty());
            assertTrue(appender.list.stream().allMatch(event -> event.getThrowableProxy() == null));
            String loggedMessages = appender.list.stream()
                    .map(ILoggingEvent::getFormattedMessage)
                    .collect(Collectors.joining("\n"));
            assertDoesNotContain(loggedMessages, "SECRET_CREDENTIAL", "SecretDriver", "SELECT * FROM", "INSERT INTO",
                    "jdbc:mysql://", "/private/credentials.json", "db-secret@example.test");
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    private void assertSafePaymentUnavailable(MvcResult result) throws Exception {
        assertEquals(503, result.getResponse().getStatus());
        assertEquals(503, json(result).get("status").asInt());
        assertEquals("Mercado Pago no está configurado para esta demo. Elegí efectivo o configurá tus credenciales.",
                json(result).get("message").asText());
        assertDoesNotContain(result.getResponse().getContentAsString(),
                "TOKEN_SECRET", "access_token", "PaymentUnavailableException");
    }

    private void assertGenericServerError(MvcResult result) throws Exception {
        assertEquals(500, result.getResponse().getStatus());
        assertEquals("Ocurrió un error inesperado", json(result).get("message").asText());
        assertDoesNotContain(result.getResponse().getContentAsString(), "SecretDriver", "SELECT * FROM",
                "/private/credentials.json", "SECRET_CREDENTIAL");
    }

    private void assertError(MvcResult result, int status, String error, String message, String... forbidden) throws Exception {
        assertEquals(status, result.getResponse().getStatus());
        assertEquals(error, json(result).get("error").asText());
        assertEquals(message, json(result).get("message").asText());
        assertDoesNotContain(result.getResponse().getContentAsString(), forbidden);
    }

    private static com.fasterxml.jackson.databind.JsonNode json(MvcResult result) throws Exception {
        return new com.fasterxml.jackson.databind.ObjectMapper().readTree(result.getResponse().getContentAsByteArray());
    }

    private static void assertDoesNotContain(String value, String... forbidden) {
        Arrays.stream(forbidden).forEach(fragment -> {
            assertNotNull(fragment);
            assertFalse(value.contains(fragment), "Unexpected sensitive detail in response/log: " + fragment);
        });
    }

    enum ProbeState {ACTIVE, DISABLED}

    record ValidationPayload(@NotBlank(message = "El nombre es obligatorio.") String displayName) {}

    @RestController
    static class ExceptionProbeController {
        @GetMapping("/probe/database")
        void database() {
            throw new DataIntegrityViolationException("database failure", new SQLException(DATABASE_DETAILS));
        }

        @GetMapping("/probe/unexpected")
        void unexpected() {
            throw new IllegalStateException(SENSITIVE_EXCEPTION);
        }

        @GetMapping("/probe/payment-unavailable")
        void paymentUnavailable() {
            throw new PaymentUnavailableException("TOKEN_SECRET access_token");
        }

        @GetMapping("/probe/payment-unavailable-wrapped")
        void paymentUnavailableWrapped() {
            throw new RuntimeException("webhook wrapper", new PaymentUnavailableException("TOKEN_SECRET access_token"));
        }

        @GetMapping("/probe/internal")
        void internal() throws InternalServerErrorException {
            throw new InternalServerErrorException(SENSITIVE_EXCEPTION, null);
        }

        @PostMapping("/probe/malformed")
        void malformed(@RequestBody java.util.Map<String, String> body) {}

        @GetMapping("/probe/conversion")
        void conversion(@RequestParam("privateState") ProbeState state) {}

        @GetMapping("/probe/missing")
        void missing(@RequestParam("privateToken") String token) {}

        @PostMapping("/probe/method/{path}")
        void method() {}

        @GetMapping("/probe/no-resource")
        void noResource() throws NoResourceFoundException {
            throw new NoResourceFoundException(HttpMethod.GET, "/private/PRIVATE_RESOURCE_PATH");
        }

        @GetMapping("/probe/security")
        void security() {
            throw new SecurityException("Bearer SECRET_CREDENTIAL");
        }

        @GetMapping("/probe/access-denied")
        void accessDenied() {
            throw new AuthorizationDeniedException(SENSITIVE_EXCEPTION);
        }

        @PostMapping("/probe/domain-error")
        void domainError() throws BadRequestException {
            throw new BadRequestException("El pedido ya está cerrado.");
        }

        @PostMapping("/probe/validation")
        void validation(@Valid @RequestBody ValidationPayload payload) {}
    }
}
