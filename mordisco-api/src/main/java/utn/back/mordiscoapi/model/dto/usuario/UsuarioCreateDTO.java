package utn.back.mordiscoapi.model.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import utn.back.mordiscoapi.model.dto.direccion.DireccionCreateDTO;

import java.util.List;

public record UsuarioCreateDTO(
        @Size(message = "El nombre del usuario debe tener máximo 50 caracteres", max = 50)
        @NotNull(message = "El nombre del usuario es obligatorio")
        String nombre,

        @Size(message = "El apellido del usuario debe tener máximo 50 caracteres", max = 50)
        @NotNull(message = "El apellido del usuario es obligatorio")
        String apellido,

        @Size(message = "El teléfono del usuario debe tener máximo 50 caracteres", max = 50)
        @NotNull(message = "El teléfono del usuario es obligatorio")
        String telefono,

        @Size(message = "El email del usuario debe tener máximo 100 caracteres", max = 100)
        @NotNull(message = "El email del usuario es obligatorio")
        @Email(message = "El email del usuario debe ser válido")
        String email,

        @Size(message = "La contraseña del usuario debe tener mínimo 8 caracteres", min = 8)
        @NotNull(message = "El contraseña del usuario es obligatorio")
        String password,

        @NotNull(message = "El rol del usuario es obligatorio")
        Long rolId,

        @Schema(description = "Direcciones del usuario")
        @Valid
        List<DireccionCreateDTO> direcciones
) {
}
