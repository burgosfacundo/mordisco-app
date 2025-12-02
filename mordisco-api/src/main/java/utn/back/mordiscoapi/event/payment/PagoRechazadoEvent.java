package utn.back.mordiscoapi.event.payment;

import lombok.Getter;
import utn.back.mordiscoapi.event.NotificationEvent;
import utn.back.mordiscoapi.model.entity.Pedido;

@Getter
public class PagoRechazadoEvent extends NotificationEvent {
    private final Pedido pedido;
    private final String motivo;

    public PagoRechazadoEvent(Pedido pedido, String motivo) {
        super(pedido.getCliente().getId(), pedido.getCliente().getEmail());
        this.pedido = pedido;
        this.motivo = motivo;
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
        return "PAGO_RECHAZADO";
    }
}
