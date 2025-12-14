package utn.back.mordiscoapi.event.order;

import lombok.Getter;
import utn.back.mordiscoapi.event.NotificationEvent;
import utn.back.mordiscoapi.model.entity.Pedido;

@Getter
public class PedidoCompletadoEvent extends NotificationEvent {
    private final Pedido pedido;

    public PedidoCompletadoEvent(Pedido pedido) {
        super(pedido.getCliente().getId(), pedido.getCliente().getEmail());
        this.pedido = pedido;
    }

    @Override
    public boolean shouldSendWebSocket() {
        return true; // Notificar al cliente
    }

    @Override
    public boolean shouldSendEmail() {
        return false; // No enviar email (ya está completado)
    }

    @Override
    public String getEventType() {
        return "PEDIDO_COMPLETADO";
    }
}
