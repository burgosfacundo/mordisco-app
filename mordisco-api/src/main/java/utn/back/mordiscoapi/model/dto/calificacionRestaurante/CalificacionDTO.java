package utn.back.mordiscoapi.model.dto.calificacionRestaurante;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CalificacionDTO(
        @NotNull(message = "El id del usuario es obligatorio")
        @Positive(message = "El id del usuario debe ser positivo")
        @Schema(description = "Id del usuario que califico", example = "7")
        Long idUsuario,
        @NotNull(message = "La calificación es obligatoria")
        @Max(value = 5, message = "La calificación no puede ser mayor a 5")
        @Min(value = 0, message = "La calificación no puede ser menor a 0")
        @Schema(description = "Calificación", example = "5")
        Integer puntaje,
        @Schema(description = "Comentario de la calificación", example = "Muy rico el doble cuarto de libra")
        String comentario) {
}
