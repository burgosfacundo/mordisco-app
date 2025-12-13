package utn.back.mordiscoapi.model.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import utn.back.mordiscoapi.common.validation.ValidationConstants;

public record ChangePasswordDTO(
        @NotBlank(message = "La contraseña actual no puede estar vacía")
        String currentPassword,

        @Pattern(
                regexp = ValidationConstants.PASSWORD_PATTERN,
                message = ValidationConstants.PASSWORD_MESSAGE
        )
        @Size(min = ValidationConstants.PASSWORD_MIN_LENGTH, max = ValidationConstants.PASSWORD_MAX_LENGTH, message = "La contraseña debe tener entre 8 y 72 caracteres")
        @NotNull(message = "La contraseña del usuario es obligatoria")
        String newPassword) {
}
