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
        Pedido pedido = event.getPedido();

        Long usuarioRestauranteId = pedido.getRestaurante().getUsuario().getId();
        String topic = "/topic/usuario/" + usuarioRestauranteId;

        NotificacionDTO notif = new NotificacionDTO(
                TipoNotificacion.NUEVO_PEDIDO,
                "Nuevo pedido #" + pedido.getId() + " de " + pedido.getCliente().getNombre(),
                pedido.getId(),
                pedido.getEstado().toString()
        );

        messagingTemplate.convertAndSend(topic, notif);
    }

    /**
     * Notifica al cliente cuando su pedido pasa a preparación
     */
    @Async
    @EventListener
    public void handlePedidoEnPreparacion(PedidoEnPreparacionEvent event) {
        Pedido pedido = event.getPedido();

        String topic = "/topic/usuario/" + pedido.getCliente().getId();

        NotificacionDTO notif = new NotificacionDTO(
                TipoNotificacion.PEDIDO_EN_PREPARACION,
                "Tu pedido #" + pedido.getId() + " está en preparación",
                pedido.getId(),
                pedido.getEstado().toString()
        );

        messagingTemplate.convertAndSend(topic, notif);
    }

    /**
     * Notifica a repartidores cuando un pedido está listo para entregar
     */
    @Async
    @EventListener
    public void handlePedidoListoParaEntregar(PedidoListoParaEntregarEvent event) {
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
    }

    /**
     * Notifica al cliente cuando su pedido está listo para retirar
     */
    @Async
    @EventListener
    public void handlePedidoListoParaRetirar(PedidoListoParaRetirarEvent event) {
        Pedido pedido = event.getPedido();
        String topic = "/topic/usuario/" + pedido.getCliente().getId();

        NotificacionDTO notif = new NotificacionDTO(
                TipoNotificacion.PEDIDO_LISTO_PARA_RETIRAR,
                "¡Tu pedido #" + pedido.getId() + " está listo para retirar!",
                pedido.getId(),
                pedido.getEstado().toString()
        );

        messagingTemplate.convertAndSend(topic, notif);
    }

    /**
     * Notifica al cliente cuando su pedido está en camino
     */
    @Async
    @EventListener
    public void handlePedidoEnCamino(PedidoEnCaminoEvent event) {
        Pedido pedido = event.getPedido();
        String topic = "/topic/usuario/" + pedido.getCliente().getId();

        NotificacionDTO notif = new NotificacionDTO(
                TipoNotificacion.PEDIDO_EN_CAMINO,
                "Tu pedido #" + pedido.getId() + " está en camino",
                pedido.getId(),
                pedido.getEstado().toString()
        );

        messagingTemplate.convertAndSend(topic, notif);
    }

    /**
     * Notifica al cliente Y al restaurante cuando un pedido es completado
     */
    @Async
    @EventListener
    public void handlePedidoCompletado(PedidoCompletadoEvent event) {
        Pedido pedido = event.getPedido();

        // Notificar al cliente
        String topicCliente = "/topic/usuario/" + pedido.getCliente().getId();
        NotificacionDTO notifCliente = new NotificacionDTO(
                TipoNotificacion.PEDIDO_COMPLETADO,
                "Tu pedido #" + pedido.getId() + " ha sido completado",
                pedido.getId(),
                pedido.getEstado().toString()
        );
        messagingTemplate.convertAndSend(topicCliente, notifCliente);

        // Notificar al restaurante
        Long usuarioRestauranteId = pedido.getRestaurante().getUsuario().getId();
        String topicRestaurante = "/topic/usuario/" + usuarioRestauranteId;
        NotificacionDTO notifRestaurante = new NotificacionDTO(
                TipoNotificacion.PEDIDO_COMPLETADO,
                "Pedido #" + pedido.getId() + " completado exitosamente",
                pedido.getId(),
                pedido.getEstado().toString()
        );
        messagingTemplate.convertAndSend(topicRestaurante, notifRestaurante);

    }

    /**
     * Notifica al cliente Y al restaurante cuando un pedido es cancelado
     */
    @Async
    @EventListener
    public void handlePedidoCancelado(PedidoCanceladoEvent event) {
        Pedido pedido = event.getPedido();

        // Notificar al cliente
        String topicCliente = "/topic/usuario/" + pedido.getCliente().getId();
        NotificacionDTO notifCliente = new NotificacionDTO(
                TipoNotificacion.PEDIDO_CANCELADO,
                "Tu pedido #" + pedido.getId() + " ha sido cancelado. Motivo: " + event.getMotivo(),
                pedido.getId(),
                pedido.getEstado().toString()
        );
        messagingTemplate.convertAndSend(topicCliente, notifCliente);

        // Notificar al restaurante
        Long usuarioRestauranteId = pedido.getRestaurante().getUsuario().getId();
        String topicRestaurante = "/topic/usuario/" + usuarioRestauranteId;
        NotificacionDTO notifRestaurante = new NotificacionDTO(
                TipoNotificacion.PEDIDO_CANCELADO,
                "El pedido #" + pedido.getId() + " ha sido cancelado",
                pedido.getId(),
                pedido.getEstado().toString()
        );
        messagingTemplate.convertAndSend(topicRestaurante, notifRestaurante);
    }

    /**
     * Notifica al cliente Y al restaurante cuando un pago es aprobado
     */
    @Async
    @EventListener
    public void handlePagoAprobado(PagoAprobadoEvent event) {
        Pedido pedido = event.getPedido();

        // Notificar al cliente
        String topicCliente = "/topic/usuario/" + pedido.getCliente().getId();
        NotificacionDTO notifCliente = new NotificacionDTO(
                TipoNotificacion.PAGO_CONFIRMADO,
                "El pago de tu pedido #" + pedido.getId() + " ha sido aprobado",
                pedido.getId(),
                pedido.getEstado().toString()
        );
        messagingTemplate.convertAndSend(topicCliente, notifCliente);

        // Notificar al restaurante
        Long usuarioRestauranteId = pedido.getRestaurante().getUsuario().getId();
        String topicRestaurante = "/topic/usuario/" + usuarioRestauranteId;
        NotificacionDTO notifRestaurante = new NotificacionDTO(
                TipoNotificacion.PAGO_CONFIRMADO,
                "Pago confirmado para pedido #" + pedido.getId(),
                pedido.getId(),
                pedido.getEstado().toString()
        );
        messagingTemplate.convertAndSend(topicRestaurante, notifRestaurante);

    }

    /**
     * Notifica al cliente Y al restaurante cuando un pago es rechazado
     */
    @Async
    @EventListener
    public void handlePagoRechazado(PagoRechazadoEvent event) {
        Pedido pedido = event.getPedido();

        // Notificar al cliente
        String topicCliente = "/topic/usuario/" + pedido.getCliente().getId();
        NotificacionDTO notifCliente = new NotificacionDTO(
                TipoNotificacion.PAGO_RECHAZADO,
                "El pago de tu pedido #" + pedido.getId() + " ha sido rechazado. " + event.getMotivo(),
                pedido.getId(),
                pedido.getEstado().toString()
        );
        messagingTemplate.convertAndSend(topicCliente, notifCliente);

        // Notificar al restaurante
        Long usuarioRestauranteId = pedido.getRestaurante().getUsuario().getId();
        String topicRestaurante = "/topic/usuario/" + usuarioRestauranteId;
        NotificacionDTO notifRestaurante = new NotificacionDTO(
                TipoNotificacion.PAGO_RECHAZADO,
                "Pago rechazado para pedido #" + pedido.getId(),
                pedido.getId(),
                pedido.getEstado().toString()
        );
        messagingTemplate.convertAndSend(topicRestaurante, notifRestaurante);
    }
}