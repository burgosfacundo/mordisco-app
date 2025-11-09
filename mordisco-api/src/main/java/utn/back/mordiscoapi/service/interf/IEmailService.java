package utn.back.mordiscoapi.service.interf;

import utn.back.mordiscoapi.common.exception.InternalServerErrorException;

public interface IEmailService {
    void sendPasswordResetEmail(String to, String name, String resetLink) throws InternalServerErrorException;

    void sendPasswordChangeAlertEmail(String to, String name,String loginLink) throws InternalServerErrorException;

    void sendNuevoPedidoEmail(String to, String nombre, Long pedidoId, String restaurante,String link) throws InternalServerErrorException;

    void sendCambioEstadoPedidoEmail(String to, String nombre, Long pedidoId, String nuevoEstado,String link) throws InternalServerErrorException;

    void sendPedidoEnCaminoEmail(String to, String nombre, Long pedidoId,String link) throws InternalServerErrorException;

}