package utn.back.mordiscoapi.model.projection;

import io.swagger.v3.oas.annotations.media.Schema;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.enums.TipoEntrega;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PedidoProjection {
    @Schema(description = "ID del pedido", example = "1")
    Long getId();
    @Schema(description = "ID del cliente", example = "2")
    Long getClienteId();
    @Schema(description = "ID del restaurante", example = "3")
    Long getRestauranteId();
    @Schema(description = "ID de la dirección de entrega", example = "4")
    Long getDireccionId();
    @Schema(description = "Tipo de entrega", example = "DELIVERY")
    TipoEntrega getTipoEntrega();
    @Schema(description = "Fecha y hora", example = "2025/02/12 21:30")
    LocalDateTime getFechaHora();
    @Schema(description = "Estado del pedido", example = "PENDIENTE")
    EstadoPedido getEstado();
    @Schema(description = "Total", example = "12000")
    BigDecimal getTotal();
    @Schema(description = "ID del producto Pedido de entrega", example = "4")
    Long getProductoPedidoId();
    @Schema(description = "Cantidad del producto", example = "5")
    Integer getCantidad();
    @Schema(description = "Precio unitario del producto", example = "100.0")
    BigDecimal getPrecioUnitario();
    @Schema(description = "ID del producto pedido", example = "6")
    Long getProductoId();
}
