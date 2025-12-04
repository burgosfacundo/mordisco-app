package utn.back.mordiscoapi.event.order;

import lombok.Getter;
import utn.back.mordiscoapi.event.NotificationEvent;
import utn.back.mordiscoapi.model.entity.Pedido;

@Getter
public class PedidoEnPreparacionEvent extends NotificationEvent {
    private final Pedido pedido;

    public PedidoEnPreparacionEvent(Pedido pedido) {
        super(pedido.getCliente().getId(), pedido.getCliente().getEmail());
        this.pedido = pedido;
    }

    @Override
    public boolean shouldSendWebSocket() {
        return true; // Notificar al cliente por WebSocket
    }

    @Override
    public boolean shouldSendEmail() {
        return true; // Enviar email al cliente
    }

    @Override
    public String getEventType() {
        return "PEDIDO_EN_PREPARACION";
    }
}
