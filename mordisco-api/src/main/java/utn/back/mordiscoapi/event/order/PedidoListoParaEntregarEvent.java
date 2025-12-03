package utn.back.mordiscoapi.event.order;

import lombok.Getter;
import utn.back.mordiscoapi.event.NotificationEvent;
import utn.back.mordiscoapi.model.entity.Pedido;

@Getter
public class PedidoListoParaEntregarEvent extends NotificationEvent {
    private final Pedido pedido;

    public PedidoListoParaEntregarEvent(Pedido pedido) {
        super(null, null); // Broadcast a repartidores disponibles
        this.pedido = pedido;
    }

    @Override
    public boolean shouldSendWebSocket() {
        return true; // Notificar a repartidores disponibles
    }

    @Override
    public boolean shouldSendEmail() {
        return false; // No enviar email
    }

    @Override
    public String getEventType() {
        return "PEDIDO_LISTO_PARA_ENTREGAR";
    }
}
