package utn.back.mordiscoapi.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MenuResponseDTO(
        @Schema(description = "Id del producto", example = "7")
        Long id,
        @Schema(description = "Nombre del menu", example = "Ricoricor")
        String nombre,
        List<ProductoDTO> productos
){
}
