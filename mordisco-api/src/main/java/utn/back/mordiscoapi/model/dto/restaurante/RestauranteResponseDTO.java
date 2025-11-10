package utn.back.mordiscoapi.model.dto.restaurante;

import io.swagger.v3.oas.annotations.media.Schema;
import utn.back.mordiscoapi.model.dto.direccion.DireccionResponseDTO;
import utn.back.mordiscoapi.model.dto.imagen.ImagenResponseDTO;

public record RestauranteResponseDTO (
        @Schema(description = "Id del restaurante", example = "7")
        Long id,
        @Schema(description = "Razón social del restaurante", example = "Mc Donald's")
        String razonSocial,
        @Schema(description = "Si el restaurante esta activo", example = "true")
        Boolean activo,
        ImagenResponseDTO logo,
        @Schema(description = "Id del menu", example = "3")
        Long menuId,
        @Schema(description = "Estrellas del restaurante")
        Double estrellas,
        @Schema(description = "Direccion del restaurante", example = "Alvear 2328")
        DireccionResponseDTO direccion
    ){

}


