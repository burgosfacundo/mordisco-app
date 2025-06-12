package utn.back.mordiscoapi.model.dto.productoPedido;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductoPedidoDTO(
                                 @NotNull(message = "La cantidad no puede ser nula.")
                                 @Positive(message = "La cantidad no puede ser menor a 1")
                                 @Schema(description = "Cantidad de productos", example = "5")
                                 Integer cantidad,
                                 @NotNull(message = "El producto no puede ser nulo.")
                                 @Positive(message = "El id del producto no puede ser menor a 1")
                                 @Schema(description = "El id del producto", example = "5")
                                 Long productoId) {
}
