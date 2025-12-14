package utn.back.mordiscoapi.service.interf;

import utn.back.mordiscoapi.common.exception.InternalServerErrorException;

public interface IEmailService {
    void sendPasswordResetEmail(String to, String name, String resetLink) throws InternalServerErrorException;

    void sendPasswordChangeAlertEmail(String to, String name, String loginLink) throws InternalServerErrorException;

    void sendNuevoPedidoEmail(String to, String nombre, Long pedidoId, String restaurante, String link) throws InternalServerErrorException;

    void sendPedidoEnPreparacionEmail(String to, String nombre, Long pedidoId, String link) throws InternalServerErrorException;

    void sendPedidoListoParaRetirarEmail(String to, String nombre, Long pedidoId, String link) throws InternalServerErrorException;

    void sendPedidoEnCaminoEmail(String to, String nombre, Long pedidoId, String link) throws InternalServerErrorException;

    void sendPedidoCanceladoEmailCliente(String to, String nombre, Long pedidoId, String motivo, String link) throws InternalServerErrorException;

    void sendPedidoCanceladoEmailRestaurante(String to, String nombreRestaurante, Long pedidoId, String link) throws InternalServerErrorException;

    void sendPagoConfirmadoEmailCliente(String to, String nombre, Long pedidoId, String link) throws InternalServerErrorException;

    void sendPagoConfirmadoEmailRestaurante(String to, String nombreRestaurante, Long pedidoId, String link) throws InternalServerErrorException;

    void sendPagoRechazadoEmailCliente(String to, String nombre, Long pedidoId, String motivo, String link) throws InternalServerErrorException;

    void sendPagoRechazadoEmailRestaurante(String to, String nombreRestaurante, Long pedidoId, String link) throws InternalServerErrorException;

}