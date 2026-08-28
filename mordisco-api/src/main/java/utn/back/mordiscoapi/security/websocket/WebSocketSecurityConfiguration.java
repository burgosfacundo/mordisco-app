package utn.back.mordiscoapi.security.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
public class WebSocketSecurityConfiguration {

    @Bean
    public AuthorizationManager<Message<?>> websocketMessageAuthorizationManager() {
        return messageAuthorizationManager();
    }

    static AuthorizationManager<Message<?>> messageAuthorizationManager() {
        MessageMatcherDelegatingAuthorizationManager.Builder messages =
                MessageMatcherDelegatingAuthorizationManager.builder();
        messages
                .simpSubscribeDestMatchers("/user/queue/notificaciones").authenticated()
                .simpSubscribeDestMatchers("/topic/repartidores").hasRole("REPARTIDOR")
                .simpTypeMatchers(SimpMessageType.MESSAGE).denyAll()
                .simpTypeMatchers(
                        SimpMessageType.CONNECT,
                        SimpMessageType.UNSUBSCRIBE,
                        SimpMessageType.DISCONNECT,
                        SimpMessageType.HEARTBEAT).authenticated()
                .simpTypeMatchers(SimpMessageType.SUBSCRIBE).denyAll()
                .anyMessage().denyAll();
        return messages.build();
    }
}
