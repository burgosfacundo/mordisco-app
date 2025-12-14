package utn.back.mordiscoapi.event;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Clase base abstracta para todos los eventos de notificación
 */
@Getter
public abstract class NotificationEvent {
    private final String eventId;
    private final LocalDateTime timestamp;
    private final Long userId;
    private final String userEmail;

    protected NotificationEvent(Long userId, String userEmail) {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.userId = userId;
        this.userEmail = userEmail;
    }

    /**
     * Indica si este evento debe enviar notificación por WebSocket
     */
    public abstract boolean shouldSendWebSocket();

    /**
     * Indica si este evento debe enviar notificación por Email
     */
    public abstract boolean shouldSendEmail();

    /**
     * Retorna el tipo de evento para logging
     */
    public abstract String getEventType();
}
