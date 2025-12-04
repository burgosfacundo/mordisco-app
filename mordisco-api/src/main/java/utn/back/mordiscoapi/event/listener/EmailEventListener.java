package utn.back.mordiscoapi.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import utn.back.mordiscoapi.config.AppProperties;
import utn.back.mordiscoapi.event.auth.CuentaBloqueadaEvent;
import utn.back.mordiscoapi.event.auth.PasswordChangedEvent;
import utn.back.mordiscoapi.event.auth.PasswordResetRequestedEvent;
import utn.back.mordiscoapi.event.order.*;
import utn.back.mordiscoapi.event.payment.PagoAprobadoEvent;
import utn.back.mordiscoapi.event.payment.PagoRechazadoEvent;
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

    /**
     * Envía email al restaurante cuando se crea un nuevo pedido
     */
    @Async
    @EventListener
    public void handlePedidoCreated(PedidoCreatedEvent event) {
        if (!event.shouldSendEmail()) return;
        
        log.info("Sending email for PedidoCreatedEvent: {}", event.getEventId());
        
        try {
            Pedido pedido = event.getPedido();
            String link = appProperties.getFrontendUrl() + "/restaurante/pedidos/" + pedido.getId();
            
            emailService.sendNuevoPedidoEmail(
                    pedido.getRestaurante().getUsuario().getEmail(),
                    pedido.getRestaurante().getUsuario().getNombre(),
                    pedido.getId(),
                    pedido.getRestaurante().getRazonSocial(),
                    link
            );
            
            log.info("✉️ Email sent successfully for new order #{}", pedido.getId());
        } catch (Exception e) {
            log.error("Error sending email for PedidoCreatedEvent: {}", e.getMessage(), e);
        }
    }

    /**
     * Envía email al cliente cuando su pedido pasa a preparación
     */
    @Async
    @EventListener
    public void handlePedidoEnPreparacion(PedidoEnPreparacionEvent event) {
        if (!event.shouldSendEmail()) return;
        
        log.info("Sending email for PedidoEnPreparacionEvent: {}", event.getEventId());
        
        try {
            Pedido pedido = event.getPedido();
            String link = appProperties.getFrontendUrl() + "/cliente/pedidos/" + pedido.getId();
            
            emailService.sendPedidoEnPreparacionEmail(
                    pedido.getCliente().getEmail(),
                    pedido.getCliente().getNombre(),
                    pedido.getId(),
                    link
            );
            
            log.info("✉️ Email sent successfully for order #{} en preparación", pedido.getId());
        } catch (Exception e) {
            log.error("Error sending email for PedidoEnPreparacionEvent: {}", e.getMessage(), e);
        }
    }

    /**
     * Envía email al cliente cuando su pedido está listo para retirar
     */
    @Async
    @EventListener
    public void handlePedidoListoParaRetirar(PedidoListoParaRetirarEvent event) {
        if (!event.shouldSendEmail()) return;
        
        log.info("Sending email for PedidoListoParaRetirarEvent: {}", event.getEventId());
        
        try {
            Pedido pedido = event.getPedido();
            String link = appProperties.getFrontendUrl() + "/cliente/pedidos/" + pedido.getId();
            
            emailService.sendPedidoListoParaRetirarEmail(
                    pedido.getCliente().getEmail(),
                    pedido.getCliente().getNombre(),
                    pedido.getId(),
                    link
            );
            
            log.info("✉️ Email sent successfully for order #{} listo para retirar", pedido.getId());
        } catch (Exception e) {
            log.error("Error sending email for PedidoListoParaRetirarEvent: {}", e.getMessage(), e);
        }
    }

    /**
     * Envía email al cliente cuando su pedido está en camino
     */
    @Async
    @EventListener
    public void handlePedidoEnCamino(PedidoEnCaminoEvent event) {
        if (!event.shouldSendEmail()) return;
        
        log.info("Sending email for PedidoEnCaminoEvent: {}", event.getEventId());
        
        try {
            Pedido pedido = event.getPedido();
            String link = appProperties.getFrontendUrl() + "/cliente/pedidos/" + pedido.getId();
            
            emailService.sendPedidoEnCaminoEmail(
                    pedido.getCliente().getEmail(),
                    pedido.getCliente().getNombre(),
                    pedido.getId(),
                    link
            );
            
            log.info("✉️ Email sent successfully for order #{} en camino", pedido.getId());
        } catch (Exception e) {
            log.error("Error sending email for PedidoEnCaminoEvent: {}", e.getMessage(), e);
        }
    }

    /**
     * Envía emails al cliente Y al restaurante cuando un pedido es cancelado
     */
    @Async
    @EventListener
    public void handlePedidoCancelado(PedidoCanceladoEvent event) {
        if (!event.shouldSendEmail()) return;
        
        log.info("Sending emails for PedidoCanceladoEvent: {}", event.getEventId());
        
        try {
            Pedido pedido = event.getPedido();
            
            // Email al cliente
            String linkCliente = appProperties.getFrontendUrl() + "/cliente/pedidos/" + pedido.getId();
            emailService.sendPedidoCanceladoEmailCliente(
                    pedido.getCliente().getEmail(),
                    pedido.getCliente().getNombre(),
                    pedido.getId(),
                    event.getMotivo(),
                    linkCliente
            );
            
            // Email al restaurante
            String linkRestaurante = appProperties.getFrontendUrl() + "/restaurante/pedidos/" + pedido.getId();
            emailService.sendPedidoCanceladoEmailRestaurante(
                    pedido.getRestaurante().getUsuario().getEmail(),
                    pedido.getRestaurante().getRazonSocial(),
                    pedido.getId(),
                    linkRestaurante
            );
            
            log.info("✉️ Emails sent successfully for cancelled order #{}", pedido.getId());
        } catch (Exception e) {
            log.error("Error sending emails for PedidoCanceladoEvent: {}", e.getMessage(), e);
        }
    }

    /**
     * Envía emails al cliente Y al restaurante cuando un pago es aprobado
     */
    @Async
    @EventListener
    public void handlePagoAprobado(PagoAprobadoEvent event) {
        if (!event.shouldSendEmail()) return;
        
        log.info("Sending emails for PagoAprobadoEvent: {}", event.getEventId());
        
        try {
            Pedido pedido = event.getPedido();
            
            // Email al cliente
            String linkCliente = appProperties.getFrontendUrl() + "/cliente/pedidos/" + pedido.getId();
            emailService.sendPagoConfirmadoEmailCliente(
                    pedido.getCliente().getEmail(),
                    pedido.getCliente().getNombre(),
                    pedido.getId(),
                    linkCliente
            );
            
            // Email al restaurante
            String linkRestaurante = appProperties.getFrontendUrl() + "/restaurante/pedidos/" + pedido.getId();
            emailService.sendPagoConfirmadoEmailRestaurante(
                    pedido.getRestaurante().getUsuario().getEmail(),
                    pedido.getRestaurante().getRazonSocial(),
                    pedido.getId(),
                    linkRestaurante
            );
            
            log.info("✉️ Emails sent successfully for approved payment of order #{}", pedido.getId());
        } catch (Exception e) {
            log.error("Error sending emails for PagoAprobadoEvent: {}", e.getMessage(), e);
        }
    }

    /**
     * Envía emails al cliente Y al restaurante cuando un pago es rechazado
     */
    @Async
    @EventListener
    public void handlePagoRechazado(PagoRechazadoEvent event) {
        if (!event.shouldSendEmail()) return;
        
        log.info("Sending emails for PagoRechazadoEvent: {}", event.getEventId());
        
        try {
            Pedido pedido = event.getPedido();
            
            // Email al cliente
            String linkCliente = appProperties.getFrontendUrl() + "/cliente/pedidos/" + pedido.getId();
            emailService.sendPagoRechazadoEmailCliente(
                    pedido.getCliente().getEmail(),
                    pedido.getCliente().getNombre(),
                    pedido.getId(),
                    event.getMotivo(),
                    linkCliente
            );
            
            // Email al restaurante
            String linkRestaurante = appProperties.getFrontendUrl() + "/restaurante/pedidos/" + pedido.getId();
            emailService.sendPagoRechazadoEmailRestaurante(
                    pedido.getRestaurante().getUsuario().getEmail(),
                    pedido.getRestaurante().getRazonSocial(),
                    pedido.getId(),
                    linkRestaurante
            );
            
            log.info("✉️ Emails sent successfully for rejected payment of order #{}", pedido.getId());
        } catch (Exception e) {
            log.error("Error sending emails for PagoRechazadoEvent: {}", e.getMessage(), e);
        }
    }

    // ========== Auth Events (sin cambios) ==========

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
}
