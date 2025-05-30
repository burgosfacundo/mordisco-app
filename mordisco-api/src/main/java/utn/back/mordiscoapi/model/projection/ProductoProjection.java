package utn.back.mordiscoapi.model.projection;

import io.swagger.v3.oas.annotations.media.Schema;
import utn.back.mordiscoapi.model.entity.Imagen;
import utn.back.mordiscoapi.model.entity.Menu;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ProductoProjection {

    /**
     * Proyección de la entidad Producto.
     * Esta interfaz define los métodos para acceder a los atributos de la entidad Producto.
     */

    @Schema(description = "ID del Producto", example = "1")
    Long getId();

    @Schema(description = "Nombre del Producto", example = "Hamburguesa")
    String getNombre();

    @Schema(description = "Descripcion del Producto", example = "Hamburguesa simple con queso")
    String getDescripcion();

    @Schema(description = "Precio del Producto", example = "10000.00")
    BigDecimal getPrecio();

    @Schema(description = "Disponibilidad del Producto", example = "True")
    Boolean getDisponible();

    @Schema(description = "Imagen del Producto", example = "{url:https://hamburguesa-con-queso.com," +
                                                                "Nombre: Cheeseburger}")
    Imagen getImagen();
}
