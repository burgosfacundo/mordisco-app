package utn.back.mordiscoapi.model.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;

public record RolDTO(
        @Schema(description = "ID del rol", example = "1")
        Long id,
        @Schema(description = "Nombre del rol", example = "ADMIN")
        String nombre
) {
}
