package utn.back.mordiscoapi.model.dto.calificacionRestaurante;

import io.swagger.v3.oas.annotations.media.Schema;

public record CalificacionResponseDTO(
        @Schema(description = "Nombre del usuario que califico", example = "Juan Perez")
        String usuario,
        @Schema(description = "Calificación", example = "5")
        Integer puntaje,
        @Schema(description = "Comentario de la calificación", example = "Muy rico el doble cuarto de libra")
        String comentario) {
}
