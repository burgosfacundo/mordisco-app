package utn.back.mordiscoapi.model.dto.imagen;

import io.swagger.v3.oas.annotations.media.Schema;

public record ImagenResponseRestauranteDTO (
    @Schema(description = "Id de la imagen", example = "5")
    Long id
){

}
