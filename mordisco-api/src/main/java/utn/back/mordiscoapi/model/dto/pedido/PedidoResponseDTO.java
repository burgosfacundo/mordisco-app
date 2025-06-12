package utn.back.mordiscoapi.model.dto.pedido;

import io.swagger.v3.oas.annotations.media.Schema;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.enums.TipoEntrega;
import utn.back.mordiscoapi.model.dto.direccion.DireccionResponseDTO;
import utn.back.mordiscoapi.model.dto.productoPedido.ProductoPedidoResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponseDTO(
    @Schema(description = "ID del pedido", example = "1")
    Long id,
    @Schema(description = "Cliente del pedido")
    UsuarioPedidoDTO cliente,
    @Schema(description = "Restaurante del pedido")
    RestaurantePedidoDTO restaurante,
    @Schema(description = "Lista de productos del pedido")
    List<ProductoPedidoResponseDTO> productos,
    @Schema(description = "Tipo de entrega del pedido", example = "DELIVERY")
    TipoEntrega tipoEntrega,
    @Schema(description = "Estado del pedido", example = "EN_PROCESO")
    EstadoPedido estado,
    @Schema(description = "Fecha y hora del pedido", example = "2023-10-01T12:00:00")
    LocalDateTime fechaHora,
    @Schema(description = "Total del pedido", example = "150.00")
    BigDecimal total,
    @Schema(description = "Dirección de entrega del pedido")
    DireccionResponseDTO direccionEntrega
) {
}
