package utn.back.mordiscoapi.model.dto.usuario;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateDTO(
        @Size(message = "El nombre del usuario debe tener máximo 50 caracteres", max = 50)
        @NotNull(message = "El nombre del usuario es obligatorio")
        String nombre,

        @Size(message = "El apellido del usuario debe tener máximo 50 caracteres", max = 50)
        @NotNull(message = "El apellido del usuario es obligatorio")
        String apellido,

        @Size(message = "El telefono del usuario debe tener máximo 50 caracteres", max = 50)
        @NotNull(message = "El telefono del usuario es obligatorio")
        String telefono
) {
}
