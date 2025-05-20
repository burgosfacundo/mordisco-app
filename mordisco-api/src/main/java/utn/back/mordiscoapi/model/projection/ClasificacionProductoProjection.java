package utn.back.mordiscoapi.model.projection;

import io.swagger.v3.oas.annotations.media.Schema;
import utn.back.mordiscoapi.model.entity.Producto;
import utn.back.mordiscoapi.model.entity.Usuario;

import java.time.LocalDateTime;

public interface ClasificacionProductoProjection {

    @Schema (description = "Id de la calificacion", example = "2")
    Long getID();

    @Schema(description = "Producto", example = "Papas fritas")
    Producto getProducto();

    @Schema(description = "Usuario", example = "pepito1234")
    Usuario getUsuario();

    @Schema(description = "Puntaje", example = "4")
    Integer getPuntaje();

    @Schema (description = "Comentario", example = "Esxcelente servicio")
    String getComentario();

    @Schema (description = "Fecha", example = "2025-06-06 14:30")
    LocalDateTime getFecha();
}
