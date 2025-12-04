package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.common.email.EmailSender;
import utn.back.mordiscoapi.common.email.EmailTemplateBuilder;
import utn.back.mordiscoapi.common.exception.InternalServerErrorException;
import utn.back.mordiscoapi.config.AppProperties;
import utn.back.mordiscoapi.service.interf.IEmailService;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {
    private final EmailSender emailSender;
    private final EmailTemplateBuilder emailTemplateBuilder;
    private final AppProperties appProperties;

    /**
     * Sends a password reset email to the user.
     *
     * @param to        the recipient's email address.
     * @param name      the name of the user requesting password reset.
     * @param resetLink the link for resetting the user's password.
     */
    @Override
    public void sendPasswordResetEmail(String to, String name, String resetLink) throws InternalServerErrorException {
        String html = emailTemplateBuilder.buildPasswordResetEmail(name, resetLink);
        emailSender.sendHtmlEmail(to, "Restablecer tu contraseña", html);
    }

    /**
     * Sends an alert email to the user when their password has been changed.
     *
     * @param to   the recipient's email address.
     * @param name the name of the user whose password was changed.
     */
    @Override
    public void sendPasswordChangeAlertEmail(String to, String name, String loginLink) throws InternalServerErrorException {
        String html = emailTemplateBuilder.buildPasswordChangeAlert(name, loginLink);
        emailSender.sendHtmlEmail(to, "Aviso: tu contraseña ha sido modificada", html);
    }

    @Override
    public void sendNuevoPedidoEmail(String to, String nombre, Long pedidoId, String restaurante, String link)
            throws InternalServerErrorException {
        String html = emailTemplateBuilder.buildNuevoPedidoEmail(nombre, pedidoId, restaurante, link);
        emailSender.sendHtmlEmail(to, "Nuevo pedido recibido", html);
    }

    @Override
    public void sendPedidoEnPreparacionEmail(String to, String nombre, Long pedidoId, String link)
            throws InternalServerErrorException {
        String html = emailTemplateBuilder.buildPedidoEnPreparacionEmail(nombre, pedidoId, link);
        emailSender.sendHtmlEmail(to, "Tu pedido está en preparación", html);
    }

    @Override
    public void sendPedidoListoParaRetirarEmail(String to, String nombre, Long pedidoId, String link)
            throws InternalServerErrorException {
        String html = emailTemplateBuilder.buildPedidoListoParaRetirarEmail(nombre, pedidoId, link);
        emailSender.sendHtmlEmail(to, "¡Tu pedido está listo para retirar!", html);
    }

    @Override
    public void sendPedidoEnCaminoEmail(String to, String nombre, Long pedidoId, String link)
            throws InternalServerErrorException {
        String html = emailTemplateBuilder.buildPedidoEnCaminoEmail(nombre, pedidoId, link);
        emailSender.sendHtmlEmail(to, "¡Tu pedido está en camino!", html);
    }

    @Override
    public void sendPedidoCanceladoEmailCliente(String to, String nombre, Long pedidoId, String motivo, String link)
            throws InternalServerErrorException {
        String html = emailTemplateBuilder.buildPedidoCanceladoEmailCliente(nombre, pedidoId, motivo, link);
        emailSender.sendHtmlEmail(to, "Tu pedido ha sido cancelado", html);
    }

    @Override
    public void sendPedidoCanceladoEmailRestaurante(String to, String nombreRestaurante, Long pedidoId, String link)
            throws InternalServerErrorException {
        String html = emailTemplateBuilder.buildPedidoCanceladoEmailRestaurante(nombreRestaurante, pedidoId, link);
        emailSender.sendHtmlEmail(to, "Pedido cancelado", html);
    }

    @Override
    public void sendPagoConfirmadoEmailCliente(String to, String nombre, Long pedidoId, String link)
            throws InternalServerErrorException {
        String html = emailTemplateBuilder.buildPagoConfirmadoEmailCliente(nombre, pedidoId, link);
        emailSender.sendHtmlEmail(to, "Pago confirmado", html);
    }

    @Override
    public void sendPagoConfirmadoEmailRestaurante(String to, String nombreRestaurante, Long pedidoId, String link)
            throws InternalServerErrorException {
        String html = emailTemplateBuilder.buildPagoConfirmadoEmailRestaurante(nombreRestaurante, pedidoId, link);
        emailSender.sendHtmlEmail(to, "Pago recibido", html);
    }

    @Override
    public void sendPagoRechazadoEmailCliente(String to, String nombre, Long pedidoId, String motivo, String link)
            throws InternalServerErrorException {
        String html = emailTemplateBuilder.buildPagoRechazadoEmailCliente(nombre, pedidoId, motivo, link);
        emailSender.sendHtmlEmail(to, "Pago rechazado", html);
    }

    @Override
    public void sendPagoRechazadoEmailRestaurante(String to, String nombreRestaurante, Long pedidoId, String link)
            throws InternalServerErrorException {
        String html = emailTemplateBuilder.buildPagoRechazadoEmailRestaurante(nombreRestaurante, pedidoId, link);
        emailSender.sendHtmlEmail(to, "Pago rechazado", html);
    }
}
