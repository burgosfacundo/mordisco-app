package utn.back.mordiscoapi.model.dto.pedido;

import io.swagger.v3.oas.annotations.media.Schema;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.enums.TipoEntrega;
import utn.back.mordiscoapi.model.dto.productoPedido.ProductoPedidoDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoDTOResponse(
        @Schema(description = "ID del cliente", example = "256")
        Long idCliente,
        @Schema(description = "ID del restaurante", example = "256")
        Long idRestaurante,
        @Schema(description = "ID de la dirección de entrega", example = "256")
        Long idDireccion,
        @Schema(description = "Tipo de entrega", example = "DELIVERY")
        TipoEntrega tipoEntrega,
        @Schema(description = "Fecha y hora", example = "2025/02/12 21:30")
        LocalDateTime fechaHora,
        @Schema(description = "Estado del pedido", example = "PENDIENTE")
        EstadoPedido estado,
        @Schema(description = "Lista de productos del pedido")
        List<ProductoPedidoDTO> productos,
        @Schema(description = "Total", example = "12000")
        BigDecimal total
) {
}
