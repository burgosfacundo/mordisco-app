package utn.back.mordiscoapi.model.projection;

import io.swagger.v3.oas.annotations.media.Schema;


public interface ImagenProjection {
    @Schema (description = "Id de la imagen", example = "1")
    Long getID();
    @Schema (description = "Url de la imagen", example = "https://hoycocino.com.ar/wp-content/uploads/2023/08/milanesa-a-caballo.jpg")
    String getUrl();
    @Schema (description = "Nombre de la imagen", example = "milanesas a caballo")
    String getNombre();
}
