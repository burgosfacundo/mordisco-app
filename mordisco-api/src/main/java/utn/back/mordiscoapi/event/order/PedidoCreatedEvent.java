package utn.back.mordiscoapi.event.order;

import lombok.Getter;
import utn.back.mordiscoapi.event.NotificationEvent;
import utn.back.mordiscoapi.model.entity.Pedido;

@Getter
public class PedidoCreatedEvent extends NotificationEvent {
    private final Pedido pedido;

    public PedidoCreatedEvent(Pedido pedido) {
        super(pedido.getRestaurante().getUsuario().getId(), 
              pedido.getRestaurante().getUsuario().getEmail());
        this.pedido = pedido;
    }

    @Override
    public boolean shouldSendWebSocket() {
        return true; // Notificar al restaurante por WebSocket
    }

    @Override
    public boolean shouldSendEmail() {
        return true; // Enviar email al restaurante
    }

    @Override
    public String getEventType() {
        return "PEDIDO_CREATED";
    }
}
