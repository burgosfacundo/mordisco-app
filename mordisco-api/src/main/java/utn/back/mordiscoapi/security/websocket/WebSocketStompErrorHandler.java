package utn.back.mordiscoapi.security.websocket;

import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

@Component
public class WebSocketStompErrorHandler extends StompSubProtocolErrorHandler {

    private static final String SAFE_MESSAGE = "Authentication failed";

    @Override
    @NonNull
    public Message<byte[]> handleClientMessageProcessingError(
            @Nullable Message<byte[]> clientMessage,
            @NonNull Throwable exception) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setMessage(SAFE_MESSAGE);
        accessor.setContentType(MediaType.TEXT_PLAIN);
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(
                SAFE_MESSAGE.getBytes(StandardCharsets.UTF_8),
                accessor.getMessageHeaders()
        );
    }
}
