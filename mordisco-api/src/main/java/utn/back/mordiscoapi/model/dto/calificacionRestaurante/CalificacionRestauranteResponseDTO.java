package utn.back.mordiscoapi.model.dto.calificacionRestaurante;

import io.swagger.v3.oas.annotations.media.Schema;


import java.time.LocalDateTime;

public record CalificacionRestauranteResponseDTO(
        @Schema(description = "ID de la calificación", example = "3")
        Long id,
        @Schema(description = "Fecha y hora de la calificación", example = "2023-10-01T12:00:00")
        LocalDateTime fechaHora,
        CalificacionDTO calificacion
) {
}
