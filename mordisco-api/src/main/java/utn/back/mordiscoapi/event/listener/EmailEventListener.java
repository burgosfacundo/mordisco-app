package utn.back.mordiscoapi.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import utn.back.mordiscoapi.common.AppProperties;
import utn.back.mordiscoapi.event.auth.CuentaBloqueadaEvent;
import utn.back.mordiscoapi.event.auth.PasswordChangedEvent;
import utn.back.mordiscoapi.event.auth.PasswordResetRequestedEvent;
import utn.back.mordiscoapi.event.order.PedidoCanceladoEvent;
import utn.back.mordiscoapi.event.order.PedidoCreatedEvent;
import utn.back.mordiscoapi.event.order.PedidoEnCaminoEvent;
import utn.back.mordiscoapi.event.order.PedidoEstadoChangedEvent;
import utn.back.mordiscoapi.model.entity.Pedido;
import utn.back.mordiscoapi.service.interf.IEmailService;

/**
 * Listener asíncrono para eventos de notificación vía Email
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailEventListener {

    private final IEmailService emailService;
    private final AppProperties appProperties;

    @Async
    @EventListener
    public void handlePedidoCreated(PedidoCreatedEvent event) {
        if (!event.shouldSendEmail()) return;
        
        log.info("Sending email for PedidoCreatedEvent: {}", event.getEventId());
        
        try {
            Pedido pedido = event.getPedido();
            String link = appProperties.getFrontendUrl() + "/restaurante/pedidos";
            
            emailService.sendNuevoPedidoEmail(
                    event.getUserEmail(),
                    pedido.getRestaurante().getRazonSocial(),
                    pedido.getId(),
                    pedido.getCliente().getNombre(),
                    link
            );
            
            log.info("Email sent successfully for new order #{}", pedido.getId());
        } catch (Exception e) {
            log.error("Error sending email for PedidoCreatedEvent: {}", e.getMessage(), e);
        }
    }

    @Async
    @EventListener
    public void handlePedidoEstadoChanged(PedidoEstadoChangedEvent event) {
        if (!event.shouldSendEmail()) return;
        
        log.info("Sending email for PedidoEstadoChangedEvent: {}", event.getEventId());
        
        try {
            Pedido pedido = event.getPedido();
            String link = appProperties.getFrontendUrl() + "/cliente/pedidos";
            
            emailService.sendCambioEstadoPedidoEmail(
                    event.getUserEmail(),
                    pedido.getCliente().getNombre(),
                    pedido.getId(),
                    formatearEstado(event.getEstadoNuevo()),
                    link
            );
            
            log.info("Email sent successfully for order #{} state change", pedido.getId());
        } catch (Exception e) {
            log.error("Error sending email for PedidoEstadoChangedEvent: {}", e.getMessage(), e);
        }
    }

    @Async
    @EventListener
    public void handlePedidoEnCamino(PedidoEnCaminoEvent event) {
        if (!event.shouldSendEmail()) return;
        
        log.info("Sending email for PedidoEnCaminoEvent: {}", event.getEventId());
        
        try {
            Pedido pedido = event.getPedido();
            String link = appProperties.getFrontendUrl() + "/cliente/pedidos";
            
            emailService.sendPedidoEnCaminoEmail(
                    event.getUserEmail(),
                    pedido.getCliente().getNombre(),
                    pedido.getId(),
                    link
            );
            
            log.info("Email sent successfully for order #{} en camino", pedido.getId());
        } catch (Exception e) {
            log.error("Error sending email for PedidoEnCaminoEvent: {}", e.getMessage(), e);
        }
    }

    @Async
    @EventListener
    public void handlePedidoCancelado(PedidoCanceladoEvent event) {
        if (!event.shouldSendEmail()) return;
        
        log.info("Sending email for PedidoCanceladoEvent: {}", event.getEventId());
        
        try {
            Pedido pedido = event.getPedido();
            String link = appProperties.getFrontendUrl() + "/cliente/pedidos";
            
            emailService.sendCambioEstadoPedidoEmail(
                    event.getUserEmail(),
                    pedido.getCliente().getNombre(),
                    pedido.getId(),
                    "CANCELADO - " + event.getMotivo(),
                    link
            );
            
            log.info("Email sent successfully for cancelled order #{}", pedido.getId());
        } catch (Exception e) {
            log.error("Error sending email for PedidoCanceladoEvent: {}", e.getMessage(), e);
        }
    }

    @Async
    @EventListener
    public void handlePasswordResetRequested(PasswordResetRequestedEvent event) {
        if (!event.shouldSendEmail()) return;
        
        log.info("Sending email for PasswordResetRequestedEvent: {}", event.getEventId());
        
        try {
            emailService.sendPasswordResetEmail(
                    event.getUserEmail(),
                    event.getNombre(),
                    event.getResetLink()
            );
            
            log.info("Password reset email sent successfully to {}", event.getUserEmail());
        } catch (Exception e) {
            log.error("Error sending password reset email: {}", e.getMessage(), e);
        }
    }

    @Async
    @EventListener
    public void handlePasswordChanged(PasswordChangedEvent event) {
        if (!event.shouldSendEmail()) return;
        
        log.info("Sending email for PasswordChangedEvent: {}", event.getEventId());
        
        try {
            emailService.sendPasswordChangeAlertEmail(
                    event.getUserEmail(),
                    event.getNombre(),
                    event.getLoginLink()
            );
            
            log.info("Password change alert email sent successfully to {}", event.getUserEmail());
        } catch (Exception e) {
            log.error("Error sending password change alert email: {}", e.getMessage(), e);
        }
    }

    @Async
    @EventListener
    public void handleCuentaBloqueada(CuentaBloqueadaEvent event) {
        if (!event.shouldSendEmail()) return;
        
        log.info("Sending email for CuentaBloqueadaEvent: {}", event.getEventId());
        
        try {
            // Aquí podrías crear un nuevo método en EmailService para cuenta bloqueada
            // Por ahora usamos el de cambio de contraseña como template
            emailService.sendPasswordChangeAlertEmail(
                    event.getUserEmail(),
                    event.getNombre(),
                    appProperties.getFrontendUrl() + "/login"
            );
            
            log.info("Account blocked email sent successfully to {}", event.getUserEmail());
        } catch (Exception e) {
            log.error("Error sending account blocked email: {}", e.getMessage(), e);
        }
    }

    private String formatearEstado(utn.back.mordiscoapi.enums.EstadoPedido estado) {
        return switch (estado) {
            case PENDIENTE -> "Pendiente de Confirmación";
            case EN_PROCESO -> "En Preparación";
            case LISTO_PARA_ENTREGAR -> "Listo para Entregar";
            case LISTO_PARA_RETIRAR -> "Listo para Retirar";
            case EN_CAMINO -> "En Camino";
            case COMPLETADO -> "Completado";
            case CANCELADO -> "Cancelado";
        };
    }
}
