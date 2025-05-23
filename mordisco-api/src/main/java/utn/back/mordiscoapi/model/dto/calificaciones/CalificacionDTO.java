package utn.back.mordiscoapi.model.dto.calificaciones;

import io.swagger.v3.oas.annotations.media.Schema;

public record CalificacionDTO(
        @Schema(description = "Id del usuario que califico", example = "7")
        Long idUsuario,
        @Schema(description = "Calificación del producto", example = "5")
        Integer calificacion,
        @Schema(description = "Comentario de la calificación", example = "Muy rico el doble cuarto de libra")
        String comentario) {
}
