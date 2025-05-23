package utn.back.mordiscoapi.model.dto.pedido;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoDTOResponse(
        @NotNull(message = "El cliente no puede ser nulo.")
        @Size(message = "El cliente no puede tener mas de 50 caracteres", max = 50)
        @Schema(description = "ID del cliente", example = "256")
        Long idCliente,
        @NotNull(message = "El restaurante no puede ser nulo.")
        @Size(message = "El restaurante no puede tener mas de 50 caracteres", max = 50)
        @Schema(description = "ID del restaurante", example = "256")
        Long idRestaurante,
        @NotNull(message = "La direccion de entrega no puede ser nulo.")
        @Size(message = "La direccion de entrega no puede tener mas de 100 caracteres", max = 100)
        @Schema(description = "Direccion de entrega", example = "San Juan 1050")
        String direccionEntrega,
        @NotNull(message = "El tipo de entrega no puede ser nulo.")
        @Size(message = "El tipo de entrega no puede tener mas de 50 caracteres", max = 50)
        @Schema(description = "Tipo de entrega", example = "Domicilio")
        String tipoEntrega,
        @NotNull(message = "El tipo de pago no puede ser nulo.")
        @Size(message = "El tipo de pago no puede tener mas de 100 caracteres", max = 100)
        @Schema(description = "Tipo de pago", example = "Efectivo")
        String tipoPago,
        @NotNull(message = "La fecha y hora no puede ser nulo.")
        @Size(message = "La fecha y hora no puede tener mas de 100 caracteres", max = 100)
        @Schema(description = "Fecha y hora", example = "2025/02/12 21:30")
        LocalDateTime fechaHora,
        @NotNull(message = "El estado no puede ser nulo.")
        @Size(message = "El estado no puede tener mas de 100 caracteres", max = 100)
        @Schema(description = "Estado del pedido", example = "En preparacion")
        String estado,
        @NotNull(message = "El pedido debe terer productos.")
        @Size(min = 1)
        @Schema(description = "El producto del pedido", example = " ")
        /*ProductoPedidoDTO*/String productoPedido,
        @NotNull(message = "El total no puede ser nulo.")
        @Size(message = "El total no puede tener mas de 50 caracteres", max = 50)
        @Schema(description = "Total", example = "12000")
        BigDecimal total
       /* @NotNull(message = "La calificacion no puede ser nulo.")
        @Size(message = "La calificacion no puede tener mas de 50 caracteres", max = 50)
        @Schema(description = "Calificacion", example = "3 estrellas")
        String calificacionRestaurante*/
) {
}
