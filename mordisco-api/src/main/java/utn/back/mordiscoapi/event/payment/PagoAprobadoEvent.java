package utn.back.mordiscoapi.event.payment;

import lombok.Getter;
import utn.back.mordiscoapi.event.NotificationEvent;
import utn.back.mordiscoapi.model.entity.Pedido;

@Getter
public class PagoAprobadoEvent extends NotificationEvent {
    private final Pedido pedido;

    public PagoAprobadoEvent(Pedido pedido) {
        super(pedido.getCliente().getId(), pedido.getCliente().getEmail());
        this.pedido = pedido;
    }

    @Override
    public boolean shouldSendWebSocket() {
        return true; // Notificar al cliente
    }

    @Override
    public boolean shouldSendEmail() {
        return false; // No enviar email (solo WebSocket)
    }

    @Override
    public String getEventType() {
        return "PAGO_APROBADO";
    }
}
