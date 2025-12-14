package utn.back.mordiscoapi.event.order;

import lombok.Getter;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.event.NotificationEvent;
import utn.back.mordiscoapi.model.entity.Pedido;

@Getter
public class PedidoEstadoChangedEvent extends NotificationEvent {
    private final Pedido pedido;
    private final EstadoPedido estadoAnterior;
    private final EstadoPedido estadoNuevo;

    public PedidoEstadoChangedEvent(Pedido pedido, EstadoPedido estadoAnterior) {
        super(pedido.getCliente().getId(), pedido.getCliente().getEmail());
        this.pedido = pedido;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = pedido.getEstado();
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
        return "PEDIDO_ESTADO_CHANGED";
    }
}
