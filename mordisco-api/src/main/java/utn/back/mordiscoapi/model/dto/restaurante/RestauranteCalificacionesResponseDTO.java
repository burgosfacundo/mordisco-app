package utn.back.mordiscoapi.model.dto.restaurante;

import io.swagger.v3.oas.annotations.media.Schema;
import utn.back.mordiscoapi.model.dto.calificacion.CalificacionDTO;

import java.util.List;

public record RestauranteCalificacionesResponseDTO(
        @Schema(description = "Id del restaurante", example = "7")
        Long idRestaurante,
        @Schema(description = "Lista de calificaciones del restaurante")
        List<CalificacionDTO> calificaciones) {
}