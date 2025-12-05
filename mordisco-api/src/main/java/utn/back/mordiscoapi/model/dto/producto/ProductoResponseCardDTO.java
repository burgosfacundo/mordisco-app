package utn.back.mordiscoapi.model.dto.producto;

import io.swagger.v3.oas.annotations.media.Schema;
import utn.back.mordiscoapi.model.dto.imagen.ImagenResponseDTO;

import java.math.BigDecimal;

public record ProductoResponseCardDTO(
        @Schema(description = "Id del producto", example = "5")
        Long id,
        @Schema(description = "Nombre del producto", example = "flan mixto")
        String nombre,
        @Schema(description = "Descripción del producto")
        String descripcion,
        @Schema(description = "Precio del producto", example = "20000")
        BigDecimal precio,
        @Schema(description = "Precio con descuento aplicado")
        BigDecimal precioConDescuento,
        @Schema(description = "Porcentaje de descuento")
        Double porcentajeDescuento,
        @Schema(description = "Indica si tiene promoción activa")
        Boolean tienePromocion,
        @Schema(description = "Descripción de la promoción")
        String descripcionPromocion,
        Boolean disponible,
        ImagenResponseDTO imagen
) {
}
