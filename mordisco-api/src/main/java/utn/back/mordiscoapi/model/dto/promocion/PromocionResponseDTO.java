package utn.back.mordiscoapi.model.dto.promocion;

import io.swagger.v3.oas.annotations.media.Schema;


import java.time.LocalDate;

public record PromocionResponseDTO (
        @Schema(description = "Id de la promocion", example = "7")
        Long id,
        @Schema(description = "Descripción de la promoción", example = "Descuento del 20% en hamburguesas")
        String descripcion,
        @Schema(description = "Descuento de la promoción", example = "0.20")
        Double descuento,
        @Schema(description = "Fecha de inicio de la promoción", example = "2023-10-01")
        LocalDate fechaInicio,
        @Schema(description = "Fecha de fin de la promoción", example = "2023-10-31")
        LocalDate fechaFin
){
}
