package utn.back.mordiscoapi.model.dto.productoPedido;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record ProductoPedidoDTOResponse(
        @Schema(description = "ID del producto_pedido", example = "3")
        Long id,
        @Schema(description = "Cantidad del producto deseada", example = "5")
        Integer cantidad,
        @Schema(description = "Precio unitario del producto", example = "100.0")
        BigDecimal precioUnitario,
        @Schema(description = "ID del producto", example = "1")
        Long idProducto,
        @Schema(description = "ID del pedido", example = "2")
        Long idPedido
        ) {
        }
