package utn.back.mordiscoapi.model.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import utn.back.mordiscoapi.model.dto.direccion.DireccionUpdateDTO;

import java.util.List;

public record UsuarioUpdateDTO(
        @Size(message = "El nombre del usuario debe tener máximo 50 caracteres", max = 50)
        @NotNull(message = "El nombre del usuario es obligatorio")
        String nombre,

        @Size(message = "El apellido del usuario debe tener máximo 50 caracteres", max = 50)
        @NotNull(message = "El apellido del usuario es obligatorio")
        String apellido,

        @Size(message = "El teléfono del usuario debe tener máximo 50 caracteres", max = 50)
        @NotNull(message = "El teléfono del usuario es obligatorio")
        String telefono,
        @NotNull(message = "Las direcciones del usuario son obligatorias")
        @Schema(description = "Direcciones del usuario")
        @Valid
        List<DireccionUpdateDTO> direcciones

) {
}
