package utn.back.mordiscoapi.event.auth;

import lombok.Getter;
import utn.back.mordiscoapi.event.NotificationEvent;

@Getter
public class PasswordResetRequestedEvent extends NotificationEvent {
    private final String nombre;
    private final String resetLink;

    public PasswordResetRequestedEvent(Long userId, String userEmail, String nombre, String resetLink) {
        super(userId, userEmail);
        this.nombre = nombre;
        this.resetLink = resetLink;
    }

    @Override
    public boolean shouldSendWebSocket() {
        return false; // No notificar por WebSocket
    }

    @Override
    public boolean shouldSendEmail() {
        return true; // Enviar email con link de reset
    }

    @Override
    public String getEventType() {
        return "PASSWORD_RESET_REQUESTED";
    }
}
