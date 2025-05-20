package utn.back.mordiscoapi.model.projection;

import io.swagger.v3.oas.annotations.media.Schema;

public interface ProductoProjection {
    @Schema(description = "Id del producto", example = "7")
    Long getID();
    @Schema(description = "Nombre del producto", example = "flan mixto")
    String getnombre();
    @Schema(description = "Descripción del producto", example = "Flan casero con dulce de leche y crema")
    String getDescripcion();
    @Schema(description = "Precio del producto", example = "20000")
    Double getPrecio();
    @Schema(description = "El producto esta disponible", example = "true")
    Boolean getDisponible();
}
