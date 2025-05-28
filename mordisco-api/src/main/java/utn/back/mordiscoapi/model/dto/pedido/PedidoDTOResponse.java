package utn.back.mordiscoapi.model.dto.pedido;

import io.swagger.v3.oas.annotations.media.Schema;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.enums.TipoEntrega;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoDTOResponse(
        @Schema(description = "ID del pedido", example = "1")
        Long id,
        @Schema(description = "ID del cliente", example = "2")
        Long idCliente,
        @Schema(description = "ID del restaurante", example = "3")
        Long idRestaurante,
        @Schema(description = "ID de la dirección de entrega", example = "4")
        Long idDireccion,
        @Schema(description = "Tipo de entrega", example = "DELIVERY")
        TipoEntrega tipoEntrega,
        @Schema(description = "Fecha y hora", example = "2025/02/12 21:30")
        LocalDateTime fechaHora,
        @Schema(description = "Estado del pedido", example = "PENDIENTE")
        EstadoPedido estado,
        @Schema(description = "Total", example = "12000")
        BigDecimal total,
        @Schema(description = "ID del producto Pedido de entrega", example = "4")
        Long idProductoPedido,
        @Schema(description = "Cantidad del producto", example = "5")
        Integer cantidad,
        @Schema(description = "Precio unitario del producto", example = "100.0")
        BigDecimal precioUnitario,
        @Schema(description = "ID del producto pedido", example = "6")
        Long idProducto
        ) {
        }
