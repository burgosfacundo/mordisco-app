package utn.back.mordiscoapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.config.AppProperties;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.enums.TipoNotificacion;
import utn.back.mordiscoapi.model.dto.notificacion.NotificacionDTO;
import utn.back.mordiscoapi.model.entity.Pedido;
import utn.back.mordiscoapi.service.interf.IEmailService;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionService {
    private final AppProperties appProperties;
    private final IEmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;

    public void notificarNuevoPedidoARestaurante(Pedido pedido) {
        // WebSocket
        String topic = "/topic/restaurante/" + pedido.getRestaurante().getId();
        NotificacionDTO notif = new NotificacionDTO(
                TipoNotificacion.NUEVO_PEDIDO,
                "Nuevo pedido #" + pedido.getId(),
                pedido.getId(),
                pedido.getEstado().toString()
        );
        messagingTemplate.convertAndSend(topic, notif);

        // Email (asíncrono)
        try {
            // Construir URL de recuperación
            String link = appProperties.getFrontendUrl() + "/restaurante/pedidos/detalle/" + pedido.getId();
            emailService.sendNuevoPedidoEmail(
                    pedido.getRestaurante().getUsuario().getEmail(),
                    pedido.getRestaurante().getUsuario().getNombre(),
                    pedido.getId(),
                    pedido.getRestaurante().getRazonSocial(),
                    link
            );
        } catch (Exception e) {
            log.error("Error al enviar email de nuevo pedido", e);
        }

        log.info("📬 Notificación enviada al restaurante #{}: Nuevo pedido #{}",
                pedido.getRestaurante().getId(), pedido.getId());
    }

    public void notificarCambioEstadoACliente(Pedido pedido) {
        // WebSocket
        String topic = "/topic/cliente/" + pedido.getCliente().getId();
        NotificacionDTO notif = new NotificacionDTO(
                TipoNotificacion.CAMBIO_ESTADO_PEDIDO,
                "Tu pedido #" + pedido.getId() + " cambió a: " + formatearEstado(pedido.getEstado()),
                pedido.getId(),
                pedido.getEstado().toString()
        );
        messagingTemplate.convertAndSend(topic, notif);

        // Email (asíncrono)
        try {
            String link = appProperties.getFrontendUrl() + "/restaurante/pedidos/detalle/" + pedido.getId();
            if (pedido.getEstado() == EstadoPedido.EN_CAMINO) {
                emailService.sendPedidoEnCaminoEmail(
                        pedido.getCliente().getEmail(),
                        pedido.getCliente().getNombre(),
                        pedido.getId(),
                        link
                );
            } else {
                emailService.sendCambioEstadoPedidoEmail(
                        pedido.getCliente().getEmail(),
                        pedido.getCliente().getNombre(),
                        pedido.getId(),
                        formatearEstado(pedido.getEstado()),
                        link
                );
            }
        } catch (Exception e) {
            log.error("Error al enviar email de cambio de estado", e);
        }

        log.info("📬 Notificación enviada al cliente #{}: Cambio de estado en pedido #{}",
                pedido.getCliente().getId(), pedido.getId());
    }
    /**
     * Notifica al cliente que su pago fue aprobado
     */
    public void notificarPagoAprobado(Pedido pedido) {
        String topic = "/topic/cliente/" + pedido.getCliente().getId();

        NotificacionDTO notif = new NotificacionDTO(TipoNotificacion.PAGO_CONFIRMADO,
                "Tu pago fue confirmado. Pedido #" + pedido.getId() + " en preparación"
                ,pedido.getId(), pedido.getEstado().toString());

        messagingTemplate.convertAndSend(topic, notif);
        log.info("🔔 Notificación enviada al cliente #{}: Pago aprobado para pedido #{}",
                pedido.getCliente().getId(), pedido.getId());
    }

    /**
     * Notifica al cliente que su pago fue rechazado
     */
    public void notificarPagoRechazado(Pedido pedido) {
        String topic = "/topic/cliente/" + pedido.getCliente().getId();

        NotificacionDTO notif = new NotificacionDTO(TipoNotificacion.PAGO_RECHAZADO,
                "Tu pago fue rechazado. Pedido #" + pedido.getId() + " cancelado"
                ,pedido.getId(), pedido.getEstado().toString());

        messagingTemplate.convertAndSend(topic, notif);
        log.info("🔔 Notificación enviada al cliente #{}: Pago rechazado para pedido #{}",
                pedido.getCliente().getId(), pedido.getId());
    }

    /**
     * Notifica al cliente que su pedido está listo para entregar
     */
    public void notificarPedidoListoParaEntregar(Pedido pedido) {
        String topic = "/topic/cliente/" + pedido.getCliente().getId();

        NotificacionDTO notif = new NotificacionDTO(
                TipoNotificacion.CAMBIO_ESTADO_PEDIDO,
                "Tu pedido #" + pedido.getId() + " está listo para ser entregado",
                pedido.getId(),
                pedido.getEstado().toString()
        );

        messagingTemplate.convertAndSend(topic, notif);
        
        // Email
        try {
            String link = appProperties.getFrontendUrl() + "/cliente/pedidos/" + pedido.getId();
            emailService.sendCambioEstadoPedidoEmail(
                    pedido.getCliente().getEmail(),
                    pedido.getCliente().getNombre(),
                    pedido.getId(),
                    "Listo para entregar",
                    link
            );
        } catch (Exception e) {
            log.error("Error al enviar email de pedido listo para entregar", e);
        }

        log.info("🔔 Notificación enviada al cliente #{}: Pedido #{} listo para entregar",
                pedido.getCliente().getId(), pedido.getId());
    }

    /**
     * Notifica al cliente que su pedido está listo para retirar
     */
    public void notificarPedidoListoParaRetirar(Pedido pedido) {
        String topic = "/topic/cliente/" + pedido.getCliente().getId();

        NotificacionDTO notif = new NotificacionDTO(
                TipoNotificacion.CAMBIO_ESTADO_PEDIDO,
                "¡Tu pedido #" + pedido.getId() + " está listo para retirar!",
                pedido.getId(),
                pedido.getEstado().toString()
        );

        messagingTemplate.convertAndSend(topic, notif);
        
        // Email
        try {
            String link = appProperties.getFrontendUrl() + "/cliente/pedidos/" + pedido.getId();
            emailService.sendCambioEstadoPedidoEmail(
                    pedido.getCliente().getEmail(),
                    pedido.getCliente().getNombre(),
                    pedido.getId(),
                    "Listo para retirar",
                    link
            );
        } catch (Exception e) {
            log.error("Error al enviar email de pedido listo para retirar", e);
        }
    }


    private String formatearEstado(EstadoPedido estado) {
        return switch (estado) {
            case PENDIENTE -> "Pendiente";
            case EN_PREPARACION -> "En Preparación";
            case LISTO_PARA_ENTREGAR ->  "Listo para entregar";
            case LISTO_PARA_RETIRAR ->  "Listo para retirar";
            case EN_CAMINO -> "En Camino";
            case COMPLETADO -> "Entregado";
            case CANCELADO -> "Cancelado";
        };
    }

    public void notificarRepartidoresCercanos(Pedido pedido) {
        String topic = "/topic/repartidor/" + pedido.getRepartidor().getId();

        NotificacionDTO notif = new NotificacionDTO(
                TipoNotificacion.CAMBIO_ESTADO_PEDIDO,
                "¡Pedido #" + pedido.getId() + " está disponible para asignarse!",
                pedido.getId(),
                pedido.getEstado().toString()
        );

        messagingTemplate.convertAndSend(topic, notif);
    }

    public void notificarPedidoEnCamino(Pedido pedido) {
        String topic = "/topic/cliente/" + pedido.getCliente().getId();

        NotificacionDTO notif = new NotificacionDTO(
                TipoNotificacion.CAMBIO_ESTADO_PEDIDO,
                "¡Tu pedido #" + pedido.getId() + " está en camino!",
                pedido.getId(),
                pedido.getEstado().toString()
        );

        messagingTemplate.convertAndSend(topic, notif);

        // Email
        try {
            String link = appProperties.getFrontendUrl() + "/cliente/pedidos/" + pedido.getId();
            emailService.sendCambioEstadoPedidoEmail(
                    pedido.getCliente().getEmail(),
                    pedido.getCliente().getNombre(),
                    pedido.getId(),
                    "Pedido en camino",
                    link
            );
        } catch (Exception e) {
            log.error("Error al enviar email de pedido en camino", e);
        }
    }

    public void notificarPedidoCompletado(Pedido pedido) {
        String topic = "/topic/cliente/" + pedido.getCliente().getId();

        NotificacionDTO notif = new NotificacionDTO(
                TipoNotificacion.CAMBIO_ESTADO_PEDIDO,
                "¡Tu pedido #" + pedido.getId() + " se entrego correctamente!",
                pedido.getId(),
                pedido.getEstado().toString()
        );

        messagingTemplate.convertAndSend(topic, notif);

        // Email
        try {
            String link = appProperties.getFrontendUrl() + "/cliente/pedidos/" + pedido.getId();
            emailService.sendCambioEstadoPedidoEmail(
                    pedido.getCliente().getEmail(),
                    pedido.getCliente().getNombre(),
                    pedido.getId(),
                    "Pedido entregado",
                    link
            );
        } catch (Exception e) {
            log.error("Error al enviar email de pedido entregado", e);
        }
    }

    public void notificarPedidoCancelado(Pedido pedido) {
        String topic = "/topic/cliente/" + pedido.getCliente().getId();

        NotificacionDTO notif = new NotificacionDTO(
                TipoNotificacion.CAMBIO_ESTADO_PEDIDO,
                "¡Tu pedido #" + pedido.getId() + " fue cancelado!",
                pedido.getId(),
                pedido.getEstado().toString()
        );

        messagingTemplate.convertAndSend(topic, notif);

        // Email
        try {
            String link = appProperties.getFrontendUrl() + "/cliente/pedidos/" + pedido.getId();
            emailService.sendCambioEstadoPedidoEmail(
                    pedido.getCliente().getEmail(),
                    pedido.getCliente().getNombre(),
                    pedido.getId(),
                    "Pedido cancelado",
                    link
            );
        } catch (Exception e) {
            log.error("Error al enviar email de pedido cancelado", e);
        }
    }
}