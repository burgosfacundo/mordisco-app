package utn.back.mordiscoapi.model.dto.usuario;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioDTO(
        @Size(message = "El nombre del usuario debe tener máximo 50 caracteres", max = 50)
        @NotNull(message = "El nombre del usuario es obligatorio")
        String nombre,

        @Size(message = "El apellido del usuario debe tener máximo 50 caracteres", max = 50)
        @NotNull(message = "El apellido del usuario es obligatorio")
        String apellido,

        @Size(message = "El telefono del usuario debe tener máximo 50 caracteres", max = 50)
        @NotNull(message = "El telefono del usuario es obligatorio")
        String telefono,

        @Size(message = "El email del usuario debe tener máximo 100 caracteres", max = 100)
        @NotNull(message = "El email del usuario es obligatorio")
        String email,

        @Size(message = "La contraseña del usuario debe tener minimo 8 caracteres", min = 8)
        @NotNull(message = "El contraseña del usuario es obligatorio")
        String password,

        @NotNull(message = "El rol del usuario es obligatorio")
        Long rolId
) {
}
