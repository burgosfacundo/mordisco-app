package utn.back.mordiscoapi.event.order;

import lombok.Getter;
import utn.back.mordiscoapi.event.NotificationEvent;
import utn.back.mordiscoapi.model.entity.Pedido;

@Getter
public class PedidoCanceladoEvent extends NotificationEvent {
    private final Pedido pedido;
    private final String motivo;

    public PedidoCanceladoEvent(Pedido pedido, String motivo) {
        super(pedido.getCliente().getId(), pedido.getCliente().getEmail());
        this.pedido = pedido;
        this.motivo = motivo;
    }

    @Override
    public boolean shouldSendWebSocket() {
        return true; // Notificar a todas las partes
    }

    @Override
    public boolean shouldSendEmail() {
        return true; // Enviar email al cliente
    }

    @Override
    public String getEventType() {
        return "PEDIDO_CANCELADO";
    }
}
