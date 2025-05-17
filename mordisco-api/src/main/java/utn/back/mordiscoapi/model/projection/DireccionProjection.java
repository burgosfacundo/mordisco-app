package utn.back.mordiscoapi.model.projection;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.EqualsAndHashCode;

public interface DireccionProjection {
    @Schema(description = "ID de la direccion", example = "1")
    Long getID();
    @Schema(description = "Nombre de la calle de la direccion", example = "Alvarado")
    String getCalle();
    @Schema(description = "Numero de la direccion",example = "2126")
    String getNumero();
    @Schema(description = "Piso de la direccion", example = "12")
    String getPiso();
    @Schema(description = "Depto de la direccion", example = "2")
    String getDepto();
    @Schema(description = "Codigo postal de la direccion",example = "7600")
    String getCodigoPostal();
    @Schema(description = "Referencia de la direccion",example = "Porton rojo")
    String getReferencias();
    @Schema(description = "Latitud de la direccion",example = "-38.00965070799693")
    Double getLatitud();
    @Schema(description = "Longitud de la direccion", example = "-57.55554272466376")
    Double getLongitud();
    @Schema(description = "ID de la ciudad", example = "Mar del Plata")
    String getCiudad();

}
