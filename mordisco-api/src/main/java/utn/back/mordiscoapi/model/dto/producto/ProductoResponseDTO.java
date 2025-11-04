package utn.back.mordiscoapi.model.dto.producto;

import io.swagger.v3.oas.annotations.media.Schema;
import utn.back.mordiscoapi.model.dto.imagen.ImagenResponseDTO;

import java.math.BigDecimal;

public record ProductoResponseDTO(
        @Schema(description = "Id del producto", example = "5")
        Long id,
        @Schema(description = "Nombre del producto", example = "flan mixto")
        String nombre,
        @Schema(description = "Descripción del producto", example = "Flan casero con dulce de leche y crema")
        String descripcion,
        @Schema(description = "Precio del producto", example = "20000")
        BigDecimal precio,
        Boolean disponible,
        ImagenResponseDTO imagen
) {
}
