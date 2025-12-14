package utn.back.mordiscoapi.event.auth;

import lombok.Getter;
import utn.back.mordiscoapi.event.NotificationEvent;

@Getter
public class CuentaBloqueadaEvent extends NotificationEvent {
    private final String nombre;
    private final String motivo;

    public CuentaBloqueadaEvent(Long userId, String userEmail, String nombre, String motivo) {
        super(userId, userEmail);
        this.nombre = nombre;
        this.motivo = motivo;
    }

    @Override
    public boolean shouldSendWebSocket() {
        return false; // No notificar por WebSocket
    }

    @Override
    public boolean shouldSendEmail() {
        return true; // Enviar email de alerta
    }

    @Override
    public String getEventType() {
        return "CUENTA_BLOQUEADA";
    }
}
