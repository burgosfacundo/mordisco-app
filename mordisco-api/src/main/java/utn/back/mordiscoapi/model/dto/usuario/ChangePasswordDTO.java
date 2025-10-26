package utn.back.mordiscoapi.model.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ChangePasswordDTO(
        @NotBlank(message = "La contraseña actual no puede estar vacía")
        String currentPassword,
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,72}$",
                message = "La contraseña debe tener 8-72 caracteres, con al menos una mayúscula, una minúscula, un número y un caracter especial"
        )
        @NotNull(message = "El contraseña del usuario es obligatoria")
        String newPassword) {
}
