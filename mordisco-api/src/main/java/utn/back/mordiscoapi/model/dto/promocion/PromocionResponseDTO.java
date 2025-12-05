package utn.back.mordiscoapi.model.dto.promocion;

import io.swagger.v3.oas.annotations.media.Schema;
import utn.back.mordiscoapi.model.enums.AlcancePromocion;
import utn.back.mordiscoapi.model.enums.TipoDescuento;

import java.time.LocalDate;
import java.util.List;

public record PromocionResponseDTO (
        @Schema(description = "Id de la promocion", example = "7")
        Long id,
        @Schema(description = "Descripción de la promoción", example = "Descuento del 20% en hamburguesas")
        String descripcion,
        @Schema(description = "Descuento de la promoción", example = "20")
        Double descuento,
        @Schema(description = "Tipo de descuento", example = "PORCENTAJE")
        TipoDescuento tipoDescuento,
        @Schema(description = "Alcance de la promoción", example = "TODO_MENU")
        AlcancePromocion alcance,
        @Schema(description = "Fecha de inicio de la promoción", example = "2023-10-01")
        LocalDate fechaInicio,
        @Schema(description = "Fecha de fin de la promoción", example = "2023-10-31")
        LocalDate fechaFin,
        @Schema(description = "Indica si la promoción está activa", example = "true")
        Boolean activa,
        @Schema(description = "IDs de productos con esta promoción")
        List<Long> productosIds
){
}
