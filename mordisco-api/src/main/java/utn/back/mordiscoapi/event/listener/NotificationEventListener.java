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

    /**
     * Notifica al restaurante cuando se crea un nuevo pedido
     */
    @Async
    @EventListener
    public void handlePedidoCreated(PedidoCreatedEvent event) {
        log.info("Processing PedidoCreatedEvent: {}", event.getEventId());
        
        Pedido pedido = event.getPedido();
        String topic = "/topic/restaurante/" + pedido.getRestaurante().getId();
        
        NotificacionDTO notif = new NotificacionDTO(
                TipoNotificacion.NUEVO_PEDIDO,
                "Nuevo pedido #" + pedido.getId() + " de " + pedido.getCliente().getNombre(),
                pedido.getId(),
                pedido.getEstado().toString()
        );
        
        messagingTemplate.convertAndSend(topic, notif);
        log.info("📬 WebSocket notification sent to restaurant #{}", pedido.getRestaurante().getId());
    }

    /**
     * Notifica al cliente cuando su pedido pasa a preparación
     */
    @Async
    @EventListener
    public void handlePedidoEnPreparacion(PedidoEnPreparacionEvent event) {
        log.info("Processing PedidoEnPreparacionEvent: {}", event.getEventId());
        
        Pedido pedido = event.getPedido();
        String topic = "/topic/cliente/" + pedido.getCliente().getId();
        
        NotificacionDTO notif = new NotificacionDTO(
                TipoNotificacion.PEDIDO_EN_PREPARACION,
                "Tu pedido #" + pedido.getId() + " está en preparación",
                pedido.getId(),
                pedido.getEstado().toString()
        );
        
        messagingTemplate.convertAndSend(topic, notif);
        log.info("📬 WebSocket notification sent to client #{}", pedido.getCliente().getId());
    }

    /**
     * Notifica a repartidores cuando un pedido está listo para entregar
     */
    @Async
    @EventListener
    public void handlePedidoListoParaEntregar(PedidoListoParaEntregarEvent event) {
        log.info("Processing PedidoListoParaEntregarEvent: {}", event.getEventId());
        
        Pedido pedido = event.getPedido();
        String topic = "/topic/repartidores";
        
        NotificacionDTO notif = new NotificacionDTO(
                TipoNotificacion.PEDIDO_LISTO_PARA_ENTREGAR,
                "Pedido #" + pedido.getId() + " listo para entregar desde " + 
                pedido.getRestaurante().getRazonSocial(),
                pedido.getId(),
                pedido.getEstado().toString()
        );
        
        messagingTemplate.convertAndSend(topic, notif);
        log.info("📬 WebSocket notification sent to delivery drivers");
    }

    /**
     * Notifica al cliente cuando su pedido está listo para retirar
     */
    @Async
    @EventListener
    public void handlePedidoListoParaRetirar(PedidoListoParaRetirarEvent event) {
        log.info("Processing PedidoListoParaRetirarEvent: {}", event.getEventId());
        
        Pedido pedido = event.getPedido();
        String topic = "/topic/cliente/" + pedido.getCliente().getId();
        
        NotificacionDTO notif = new NotificacionDTO(
                TipoNotificacion.PEDIDO_LISTO_PARA_RETIRAR,
                "¡Tu pedido #" + pedido.getId() + " está listo para retirar!",
                pedido.getId(),
                pedido.getEstado().toString()
        );
        
        messagingTemplate.convertAndSend(topic, notif);
        log.info("📬 WebSocket notification sent to client #{}", pedido.getCliente().getId());
    }

    /**
     * Notifica al cliente cuando su pedido está en camino
     */
    @Async
    @EventListener
    public void handlePedidoEnCamino(PedidoEnCaminoEvent event) {
        log.info("Processing PedidoEnCaminoEvent: {}", event.getEventId());
        
        Pedido pedido = event.getPedido();
        String topic = "/topic/cliente/" + pedido.getCliente().getId();
        
        NotificacionDTO notif = new NotificacionDTO(
                TipoNotificacion.PEDIDO_EN_CAMINO,
                "Tu pedido #" + pedido.getId() + " está en camino",
                pedido.getId(),
                pedido.getEstado().toString()
        );
        
        messagingTemplate.convertAndSend(topic, notif);
        log.info("📬 WebSocket notification sent to client #{}", pedido.getCliente().getId());
    }

    /**
     * Notifica al cliente Y al restaurante cuando un pedido es completado
     */
    @Async
    @EventListener
    public void handlePedidoCompletado(PedidoCompletadoEvent event) {
        log.info("Processing PedidoCompletadoEvent: {}", event.getEventId());
        
        Pedido pedido = event.getPedido();
        
        // Notificar al cliente
        String topicCliente = "/topic/cliente/" + pedido.getCliente().getId();
        NotificacionDTO notifCliente = new NotificacionDTO(
                TipoNotificacion.PEDIDO_COMPLETADO,
                "Tu pedido #" + pedido.getId() + " ha sido completado",
                pedido.getId(),
                pedido.getEstado().toString()
        );
        messagingTemplate.convertAndSend(topicCliente, notifCliente);
        
        // Notificar al restaurante
        String topicRestaurante = "/topic/restaurante/" + pedido.getRestaurante().getId();
        NotificacionDTO notifRestaurante = new NotificacionDTO(
                TipoNotificacion.PEDIDO_COMPLETADO,
                "Pedido #" + pedido.getId() + " completado exitosamente",
                pedido.getId(),
                pedido.getEstado().toString()
        );
        messagingTemplate.convertAndSend(topicRestaurante, notifRestaurante);
        
        log.info("📬 WebSocket notifications sent to client #{} and restaurant #{}", 
                pedido.getCliente().getId(), pedido.getRestaurante().getId());
    }

    /**
     * Notifica al cliente Y al restaurante cuando un pedido es cancelado
     */
    @Async
    @EventListener
    public void handlePedidoCancelado(PedidoCanceladoEvent event) {
        log.info("Processing PedidoCanceladoEvent: {}", event.getEventId());
        
        Pedido pedido = event.getPedido();
        
        // Notificar al cliente
        String topicCliente = "/topic/cliente/" + pedido.getCliente().getId();
        NotificacionDTO notifCliente = new NotificacionDTO(
                TipoNotificacion.PEDIDO_CANCELADO,
                "Tu pedido #" + pedido.getId() + " ha sido cancelado. Motivo: " + event.getMotivo(),
                pedido.getId(),
                pedido.getEstado().toString()
        );
        messagingTemplate.convertAndSend(topicCliente, notifCliente);
        
        // Notificar al restaurante
        String topicRestaurante = "/topic/restaurante/" + pedido.getRestaurante().getId();
        NotificacionDTO notifRestaurante = new NotificacionDTO(
                TipoNotificacion.PEDIDO_CANCELADO,
                "El pedido #" + pedido.getId() + " ha sido cancelado",
                pedido.getId(),
                pedido.getEstado().toString()
        );
        messagingTemplate.convertAndSend(topicRestaurante, notifRestaurante);

        log.info("📬 WebSocket notifications sent to client #{} and restaurant #{}", 
                pedido.getCliente().getId(), pedido.getRestaurante().getId());
    }

    /**
     * Notifica al cliente Y al restaurante cuando un pago es aprobado
     */
    @Async
    @EventListener
    public void handlePagoAprobado(PagoAprobadoEvent event) {
        log.info("Processing PagoAprobadoEvent: {}", event.getEventId());
        
        Pedido pedido = event.getPedido();
        
        // Notificar al cliente
        String topicCliente = "/topic/cliente/" + pedido.getCliente().getId();
        NotificacionDTO notifCliente = new NotificacionDTO(
                TipoNotificacion.PAGO_CONFIRMADO,
                "El pago de tu pedido #" + pedido.getId() + " ha sido aprobado",
                pedido.getId(),
                pedido.getEstado().toString()
        );
        messagingTemplate.convertAndSend(topicCliente, notifCliente);
        
        // Notificar al restaurante
        String topicRestaurante = "/topic/restaurante/" + pedido.getRestaurante().getId();
        NotificacionDTO notifRestaurante = new NotificacionDTO(
                TipoNotificacion.PAGO_CONFIRMADO,
                "Pago confirmado para pedido #" + pedido.getId(),
                pedido.getId(),
                pedido.getEstado().toString()
        );
        messagingTemplate.convertAndSend(topicRestaurante, notifRestaurante);
        
        log.info("📬 WebSocket notifications sent to client #{} and restaurant #{}", 
                pedido.getCliente().getId(), pedido.getRestaurante().getId());
    }

    /**
     * Notifica al cliente Y al restaurante cuando un pago es rechazado
     */
    @Async
    @EventListener
    public void handlePagoRechazado(PagoRechazadoEvent event) {
        log.info("Processing PagoRechazadoEvent: {}", event.getEventId());
        
        Pedido pedido = event.getPedido();
        
        // Notificar al cliente
        String topicCliente = "/topic/cliente/" + pedido.getCliente().getId();
        NotificacionDTO notifCliente = new NotificacionDTO(
                TipoNotificacion.PAGO_RECHAZADO,
                "El pago de tu pedido #" + pedido.getId() + " ha sido rechazado. " + event.getMotivo(),
                pedido.getId(),
                pedido.getEstado().toString()
        );
        messagingTemplate.convertAndSend(topicCliente, notifCliente);
        
        // Notificar al restaurante
        String topicRestaurante = "/topic/restaurante/" + pedido.getRestaurante().getId();
        NotificacionDTO notifRestaurante = new NotificacionDTO(
                TipoNotificacion.PAGO_RECHAZADO,
                "Pago rechazado para pedido #" + pedido.getId(),
                pedido.getId(),
                pedido.getEstado().toString()
        );
        messagingTemplate.convertAndSend(topicRestaurante, notifRestaurante);
        
        log.info("📬 WebSocket notifications sent to client #{} and restaurant #{}", 
                pedido.getCliente().getId(), pedido.getRestaurante().getId());
    }
}
