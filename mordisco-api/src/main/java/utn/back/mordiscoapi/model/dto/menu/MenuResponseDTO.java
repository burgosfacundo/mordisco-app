package utn.back.mordiscoapi.model.dto.menu;

import io.swagger.v3.oas.annotations.media.Schema;

public record MenuResponseDTO(
        @Schema(description = "Id del menu", example = "5")
        Long id,
        @Schema(description = "Nombre del menu", example = "Ricoricor")
        String nombre
) {
}
