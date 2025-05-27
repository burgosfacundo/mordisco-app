package utn.back.mordiscoapi.model.dto.direccion;

import io.swagger.v3.oas.annotations.media.Schema;


public record DireccionResponseDTO (
        @Schema(description = "ID de la direccion", example = "3")
        Long id,

        String calle,

        String numero,

        String piso,

        String depto,

        String codigoPostal,

        String referencias,

        Double latitud,

        Double longitud,

        String ciudad
) {

}
