package utn.back.mordiscoapi.model.projection;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public interface ProductoPedidoProjection {
    @Schema(description = "ID del producto_pedido", example = "3")
    Long getId();
    @Schema(description = "Cantidad del producto deseada", example = "5")
    Integer getCantidad();
    @Schema(description = "Precio unitario del producto", example = "100.0")
    BigDecimal getPrecioUnitario();
    @Schema(description = "ID del producto", example = "1")
    Long getProductoId();
    @Schema(description = "ID del pedido", example = "2")
    Long getPedidoId();

}

