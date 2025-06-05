package utn.back.mordiscoapi.model.dto.direccion;

import io.swagger.v3.oas.annotations.media.Schema;


public record DireccionResponseDTO (
        @Schema(description = "ID de la dirección", example = "3")
        Long id,
        @Schema(description = "Calle de la dirección", example = "Alvarado")
        String calle,
        @Schema(description = "Número de la dirección", example = "2621")
        String numero,
        @Schema(description = "Piso de la dirección", example = "12")
        String piso,
        @Schema(description = "Departamento de la dirección", example = "C")
        String depto,
        @Schema(description = "Código postal de la dirección", example = "7600")
        String codigoPostal,
        @Schema(description = "Referencia de la dirección", example = "Edificio al lado de porton rojo")
        String referencias,
        @Schema(description = "Latitud de la dirección", example = "-38.00965070799693")
        Double latitud,
        @Schema(description = "Longitud de la dirección", example = "-57.55554272466376")
        Double longitud,
        @Schema(description = "Ciudad de la dirección", example = "Mar del Plata")
        String ciudad
) {

}
