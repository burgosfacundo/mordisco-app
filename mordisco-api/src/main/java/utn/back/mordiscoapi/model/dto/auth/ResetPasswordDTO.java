package utn.back.mordiscoapi.model.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import utn.back.mordiscoapi.common.validation.ValidationConstants;

public record ResetPasswordDTO(
        @NotBlank(message = "El token es obligatorio")
        String token,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = ValidationConstants.PASSWORD_MIN_LENGTH, max = ValidationConstants.PASSWORD_MAX_LENGTH, message = "La contraseña debe tener entre 8 y 72 caracteres")
        @Pattern(
                regexp = ValidationConstants.PASSWORD_PATTERN,
                message = ValidationConstants.PASSWORD_MESSAGE
        )
        String newPassword
) {}