package utn.back.mordiscoapi.model.dto.productoPedido;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductoPedidoDTO( @Positive
                                 @Schema(description = "Cantidad de productos", example = "5")
                                 Integer cantidad,
                                 @Positive
                                 @Schema(description = "Precio unitario", example = "500")
                                 BigDecimal precioUnitario,
                                 @NotNull(message = "El producto no puede ser nulo.")
                                 @Schema(description = "El id del producto", example = "5")
                                 Long producto_id,
                                 @NotNull(message = "El pedido no puede ser nulo.")
                                 @Schema(description = "El id del pedido", example = "5")
                                 Long pedido_id) {
}
