package utn.back.mordiscoapi.model.dto.calificacionRestaurante;

import io.swagger.v3.oas.annotations.media.Schema;


import java.time.LocalDateTime;

public record CalificacionRestauranteResponseDTO(
        @Schema(description = "ID de la calificacion", example = "3")
        Long id,
        @Schema(description = "Puntaje del restaurante", example = "9")
        int puntaje,
        @Schema(description = "Comentario al restaurante", example = "Muy buena atencion")
        String comentario,

        LocalDateTime fechaHora,

        Long usuarioId
) {
}
