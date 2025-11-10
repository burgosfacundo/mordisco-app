package utn.back.mordiscoapi.model.dto.pedido;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;
import utn.back.mordiscoapi.enums.MetodoPago;
import utn.back.mordiscoapi.enums.TipoEntrega;
import utn.back.mordiscoapi.model.dto.productoPedido.ProductoPedidoDTO;

import java.util.List;

public record PedidoRequestDTO(
    @NotNull(message = "El cliente no puede ser nulo.")
    @Positive(message = "El id del cliente no puede ser menor a 1")
    @Schema(description = "ID del cliente", example = "256")
    Long idCliente,

    @NotNull(message = "El restaurante no puede ser nulo.")
    @Positive(message = "El id del restaurante no puede ser menor a 1")
    @Schema(description = "ID del restaurante", example = "256")
    Long idRestaurante,

    @Positive(message = "El id de la dirección no puede ser menor a 1")
    @Schema(description = "ID de la dirección de entrega", example = "256")
    Long idDireccion,

    @NotNull(message = "El tipo de entrega no puede ser nulo.")
    @Schema(description = "Tipo de entrega", example = "DELIVERY")
    TipoEntrega tipoEntrega,
    @NotNull(message = "El método de pago es obligatorio")
    MetodoPago metodoPago,

    @NotEmpty(message = "Debe incluir al menos un producto")
    @Schema(description = "Lista de productos del pedido")
    @Valid
    List<ProductoPedidoDTO> productos,

    @Length(max = 500, message = "Los comentarios no pueden exceder los 500 caracteres.")
    @Schema(description = "Comentarios adicionales para el pedido", example = "Sin cebolla en las hamburguesas")
    String comentarios
) {
}
