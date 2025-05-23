package utn.back.mordiscoapi.model.dto.calificaciones;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ProductoCalificacionesResponseDTO(
        @Schema(description = "Id del producto", example = "7")
        Long idProducto,
        @Schema(description = "Nombre del producto", example = "Doble Cuarto de Libra")
        String nombreProducto,
        @Schema(description = "Lista de calificaciones del producto")
        List<CalificacionDTO> calificaciones) {
}
