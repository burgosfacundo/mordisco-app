package utn.back.mordiscoapi.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import utn.back.mordiscoapi.enums.TipoNotificacion;
import utn.back.mordiscoapi.event.order.*;
import utn.back.mordiscoapi.event.payment.PagoAprobadoEvent;
import utn.back.mordiscoapi.event.payment.PagoRechazadoEvent;
import utn.back.mordiscoapi.model.dto.notificacion.NotificacionDTO;
import utn.back.mordiscoapi.model.entity.Pedido;

/**
 * Listener asíncrono para eventos de notificación vía WebSocket
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @Async
    @EventListener
    public void handlePedidoCreated(PedidoCreatedEvent event) {
        log.info("Processing PedidoCreatedEvent: {}", event.getEventId());
        
        Pedido pedido = event.getPedido();
        String topic = "/topic/restaurante/" + pedido.getRestaurante().getId();
        
        NotificacionDTO notif = new NotificacionDTO(
                "Nuevo Pedido #" + pedido.getId(),
                "Has recibido un nuevo pedido de " + pedido.getCliente().getNombre(),
                TipoNotificacion.NUEVO_PEDIDO,
                pedido.getId()
        );
        
        messagingTemplate.convertAndSend(topic, notif);
        log.info("WebSocket notification sent to {}", topic);
    }

    @Async
    @EventListener
    public void handlePedidoEstadoChanged(PedidoEstadoChangedEvent event) {
        log.info("Processing PedidoEstadoChangedEvent: {}", event.getEventId());
        
        Pedido pedido = event.getPedido();
        String topic = "/topic/cliente/" + pedido.getCliente().getId();
        
        String estadoFormateado = formatearEstado(pedido.getEstado());
        
        NotificacionDTO notif = new NotificacionDTO(
                "Pedido #" + pedido.getId(),
                "Tu pedido está " + estadoFormateado,
                TipoNotificacion.CAMBIO_ESTADO,
                pedido.getId()
        );
        
        messagingTemplate.convertAndSend(topic, notif);
        log.info("WebSocket notification sent to {}", topic);
    }

    @Async
    @EventListener
    public void handlePedidoListoParaEntregar(PedidoListoParaEntregarEvent event) {
        log.info("Processing PedidoListoParaEntregarEvent: {}", event.getEventId());
        
        Pedido pedido = event.getPedido();
        String topic = "/topic/repartidores";
        
        NotificacionDTO notif = new NotificacionDTO(
                "Nuevo Pedido Disponible",
                "Pedido #" + pedido.getId() + " listo para entregar desde " + 
                pedido.getRestaurante().getRazonSocial(),
                TipoNotificacion.PEDIDO_DISPONIBLE,
                pedido.getId()
        );
        
        messagingTemplate.convertAndSend(topic, notif);
        log.info("WebSocket notification sent to {}", topic);
    }

    @Async
    @EventListener
    public void handlePedidoEnCamino(PedidoEnCaminoEvent event) {
        log.info("Processing PedidoEnCaminoEvent: {}", event.getEventId());
        
        Pedido pedido = event.getPedido();
        String topic = "/topic/cliente/" + pedido.getCliente().getId();
        
        NotificacionDTO notif = new NotificacionDTO(
                "Pedido en Camino",
                "Tu pedido #" + pedido.getId() + " está en camino",
                TipoNotificacion.PEDIDO_EN_CAMINO,
                pedido.getId()
        );
        
        messagingTemplate.convertAndSend(topic, notif);
        log.info("WebSocket notification sent to {}", topic);
    }

    @Async
    @EventListener
    public void handlePedidoCompletado(PedidoCompletadoEvent event) {
        log.info("Processing PedidoCompletadoEvent: {}", event.getEventId());
        
        Pedido pedido = event.getPedido();
        String topic = "/topic/cliente/" + pedido.getCliente().getId();
        
        NotificacionDTO notif = new NotificacionDTO(
                "Pedido Completado",
                "Tu pedido #" + pedido.getId() + " ha sido completado",
                TipoNotificacion.PEDIDO_COMPLETADO,
                pedido.getId()
        );
        
        messagingTemplate.convertAndSend(topic, notif);
        log.info("WebSocket notification sent to {}", topic);
    }

    @Async
    @EventListener
    public void handlePedidoCancelado(PedidoCanceladoEvent event) {
        log.info("Processing PedidoCanceladoEvent: {}", event.getEventId());
        
        Pedido pedido = event.getPedido();
        
        // Notificar al cliente
        String topicCliente = "/topic/cliente/" + pedido.getCliente().getId();
        NotificacionDTO notifCliente = new NotificacionDTO(
                "Pedido Cancelado",
                "Tu pedido #" + pedido.getId() + " ha sido cancelado. Motivo: " + event.getMotivo(),
                TipoNotificacion.PEDIDO_CANCELADO,
                pedido.getId()
        );
        messagingTemplate.convertAndSend(topicCliente, notifCliente);
        
        // Notificar al restaurante
        String topicRestaurante = "/topic/restaurante/" + pedido.getRestaurante().getId();
        NotificacionDTO notifRestaurante = new NotificacionDTO(
                "Pedido Cancelado",
                "El pedido #" + pedido.getId() + " ha sido cancelado",
                TipoNotificacion.PEDIDO_CANCELADO,
                pedido.getId()
        );
        messagingTemplate.convertAndSend(topicRestaurante, notifRestaurante);
        
        log.info("WebSocket notifications sent for cancelled order");
    }

    @Async
    @EventListener
    public void handlePagoAprobado(PagoAprobadoEvent event) {
        log.info("Processing PagoAprobadoEvent: {}", event.getEventId());
        
        Pedido pedido = event.getPedido();
        String topic = "/topic/cliente/" + pedido.getCliente().getId();
        
        NotificacionDTO notif = new NotificacionDTO(
                "Pago Aprobado",
                "El pago de tu pedido #" + pedido.getId() + " ha sido aprobado",
                TipoNotificacion.PAGO_APROBADO,
                pedido.getId()
        );
        
        messagingTemplate.convertAndSend(topic, notif);
        log.info("WebSocket notification sent to {}", topic);
    }

    @Async
    @EventListener
    public void handlePagoRechazado(PagoRechazadoEvent event) {
        log.info("Processing PagoRechazadoEvent: {}", event.getEventId());
        
        Pedido pedido = event.getPedido();
        String topic = "/topic/cliente/" + pedido.getCliente().getId();
        
        NotificacionDTO notif = new NotificacionDTO(
                "Pago Rechazado",
                "El pago de tu pedido #" + pedido.getId() + " ha sido rechazado. " + event.getMotivo(),
                TipoNotificacion.PAGO_RECHAZADO,
                pedido.getId()
        );
        
        messagingTemplate.convertAndSend(topic, notif);
        log.info("WebSocket notification sent to {}", topic);
    }

    private String formatearEstado(EstadoPedido estado) {
        return switch (estado) {
            case PENDIENTE -> "pendiente de confirmación";
            case EN_PROCESO -> "en preparación";
            case LISTO_PARA_ENTREGAR -> "listo para entregar";
            case LISTO_PARA_RETIRAR -> "listo para retirar";
            case EN_CAMINO -> "en camino";
            case COMPLETADO -> "completado";
            case CANCELADO -> "cancelado";
        };
    }
}
