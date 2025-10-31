package utn.back.mordiscoapi.model.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;

public record UsuarioCardDTO(
        @Schema(description = "ID del usuario", example = "1")
        Long id,
        @Schema(description = "Nombre del usuario", example = "Juan")
        String nombre,
        @Schema(description = "Apellido del usuario", example = "Pérez")
        String apellido,
        @Schema(description = "Email del usuario", example = "juan@gmail.com")
        String email,
        @Schema(description = "Rol del usuario")
        String rol
) {
}
