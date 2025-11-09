package utn.back.mordiscoapi.model.dto.pago;

import utn.back.mordiscoapi.enums.EstadoPago;
import utn.back.mordiscoapi.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponse(
        Long id,
        Long pedidoId,
        MetodoPago metodoPago,
        BigDecimal monto,
        EstadoPago estado,
        String mercadoPagoPaymentId,
        String mercadoPagoStatus,
        LocalDateTime fechaCreacion
) {
}
