package utn.back.mordiscoapi.event.auth;

import lombok.Getter;
import utn.back.mordiscoapi.event.NotificationEvent;

@Getter
public class PasswordChangedEvent extends NotificationEvent {
    private final String nombre;
    private final String loginLink;

    public PasswordChangedEvent(Long userId, String userEmail, String nombre, String loginLink) {
        super(userId, userEmail);
        this.nombre = nombre;
        this.loginLink = loginLink;
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
        return "PASSWORD_CHANGED";
    }
}
