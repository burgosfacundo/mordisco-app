package utn.back.mordiscoapi.model.projection;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public interface CalificacionRestauranteProjection {
    @Schema(description = "ID de la calificación de restaurante", example = "1")
    Long getId();

    @Schema(description = "Puntaje de la calificación de restaurante", example = "5")
    Long getPuntaje();

    @Schema(description = "Comentario para el restaurante", example = "La comida estaba rica")
    String getComentario();

    @Schema(description = "Fecha y Hora de la calificación", example = " 2025/04/12T12:00")
    LocalDateTime getFechaHora();

    @Schema(description = "ID del restaurante", example = "2")
    Long getRestauranteId();

    @Schema(description = "ID del usuario", example = "3")
    Long getUsuarioId();
}