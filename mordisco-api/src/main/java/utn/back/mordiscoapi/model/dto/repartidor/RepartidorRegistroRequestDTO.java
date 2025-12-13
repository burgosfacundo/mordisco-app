package utn.back.mordiscoapi.model.dto.repartidor;

import jakarta.validation.constraints.*;
import utn.back.mordiscoapi.common.validation.ValidationConstants;

public record RepartidorRegistroRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = ValidationConstants.NOMBRE_MIN_LENGTH, max = ValidationConstants.NOMBRE_MAX_LENGTH, message = "El nombre debe tener entre 2 y 50 caracteres")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = ValidationConstants.NOMBRE_MIN_LENGTH, max = ValidationConstants.NOMBRE_MAX_LENGTH, message = "El apellido debe tener entre 2 y 50 caracteres")
        String apellido,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = ValidationConstants.TELEFONO_PATTERN, message = ValidationConstants.TELEFONO_MESSAGE)
        @Size(max = ValidationConstants.TELEFONO_MAX_LENGTH, message = "El teléfono debe tener máximo " + ValidationConstants.TELEFONO_MAX_LENGTH + " caracteres")
        String telefono,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato de email inválido")
        @Size(max = ValidationConstants.EMAIL_MAX_LENGTH, message = "El email debe tener máximo 100 caracteres")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = ValidationConstants.PASSWORD_MIN_LENGTH, max = ValidationConstants.PASSWORD_MAX_LENGTH, message = "La contraseña debe tener entre 8 y 72 caracteres")
        @Pattern(regexp = ValidationConstants.PASSWORD_PATTERN, message = ValidationConstants.PASSWORD_MESSAGE)
        String password,

        @NotBlank(message = "El CUIL es obligatorio")
        @Pattern(regexp = "^\\d{11}$", message = "El CUIL debe tener 11 dígitos")
        String cuil,

        @NotBlank(message = "La foto del DNI es obligatoria")
        String fotoDni
) {}