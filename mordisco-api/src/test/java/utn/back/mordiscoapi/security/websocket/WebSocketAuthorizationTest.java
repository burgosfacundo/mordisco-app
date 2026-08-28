package utn.back.mordiscoapi.security.websocket;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebSocketAuthorizationTest {

    private final AuthorizationManager<Message<?>> authorization =
            WebSocketSecurityConfiguration.messageAuthorizationManager();

    @Test
    void permitsOnlyTheExactAuthenticatedUserQueueSubscription() {
        assertTrue(granted(StompCommand.SUBSCRIBE, "/user/queue/notificaciones", "ROLE_CLIENTE"));
        assertFalse(granted(StompCommand.SUBSCRIBE, "/user/queue/otra", "ROLE_CLIENTE"));
        assertFalse(granted(StompCommand.SUBSCRIBE, "/queue/notificaciones", "ROLE_CLIENTE"));
        assertFalse(granted(StompCommand.SUBSCRIBE, "/user/queue/notificaciones", null));
    }

    @Test
    void permitsCourierTopicOnlyToRepartidor() {
        assertTrue(granted(StompCommand.SUBSCRIBE, "/topic/repartidores", "ROLE_REPARTIDOR"));
        assertFalse(granted(StompCommand.SUBSCRIBE, "/topic/repartidores", "ROLE_CLIENTE"));
        assertFalse(granted(StompCommand.SUBSCRIBE, "/topic/repartidores", "ROLE_RESTAURANTE"));
        assertFalse(granted(StompCommand.SUBSCRIBE, "/topic/repartidores", "ROLE_ADMIN"));
    }

    @Test
    void deniesEveryClientMessageAndUnsupportedFrame() {
        assertFalse(granted(StompCommand.SEND, "/app/pedidos", "ROLE_REPARTIDOR"));
        assertFalse(granted(StompCommand.SEND, "/topic/repartidores", "ROLE_REPARTIDOR"));
        assertFalse(granted(StompCommand.ACK, null, "ROLE_REPARTIDOR"));
        assertFalse(granted(StompCommand.NACK, null, "ROLE_REPARTIDOR"));
    }

    @Test
    void permitsOnlyRequiredAuthenticatedLifecycleFrames() {
        assertTrue(granted(StompCommand.UNSUBSCRIBE, null, "ROLE_CLIENTE"));
        assertTrue(granted(StompCommand.DISCONNECT, null, "ROLE_CLIENTE"));
        assertTrue(granted(null, null, "ROLE_CLIENTE"));
        assertFalse(granted(StompCommand.UNSUBSCRIBE, null, null));
        assertFalse(granted(StompCommand.DISCONNECT, null, null));
    }

    @Test
    void producesAConstantSanitizedStompErrorWithoutCredentialOrPrivatePayload() {
        String credential = "secret.jwt.value";
        String privatePayload = "private-order-data";
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        Message<byte[]> clientMessage = MessageBuilder.createMessage(
                privatePayload.getBytes(), accessor.getMessageHeaders());

        Message<byte[]> error = new WebSocketStompErrorHandler().handleClientMessageProcessingError(
                clientMessage,
                new BadCredentialsException("invalid " + credential));
        StompHeaderAccessor errorHeaders = StompHeaderAccessor.wrap(error);
        String errorBody = new String(error.getPayload());

        assertEquals("Authentication failed", errorHeaders.getMessage());
        assertEquals("Authentication failed", errorBody);
        assertFalse(errorBody.contains(credential));
        assertFalse(errorBody.contains(privatePayload));
    }

    private boolean granted(StompCommand command, String destination, String authority) {
        MessageHeaderAccessor accessor = command == null
                ? SimpMessageHeaderAccessor.create(SimpMessageType.HEARTBEAT)
                : StompHeaderAccessor.create(command);
        if (destination != null) {
            ((SimpMessageHeaderAccessor) accessor).setDestination(destination);
        }
        var authentication = authority == null
                ? new UsernamePasswordAuthenticationToken("anonymous", null)
                : new UsernamePasswordAuthenticationToken(
                        "user@example.com", null, List.of(new SimpleGrantedAuthority(authority)));
        AuthorizationResult decision = authorization.authorize(
                () -> authentication,
                MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders()));
        return decision != null && decision.isGranted();
    }
}
