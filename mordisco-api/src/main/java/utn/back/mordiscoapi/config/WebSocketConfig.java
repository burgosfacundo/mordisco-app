package utn.back.mordiscoapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilitar broker simple para /topic y /queue
        config.enableSimpleBroker("/topic", "/queue");

        // Prefijo para mensajes desde el cliente hacia el servidor
        config.setApplicationDestinationPrefixes("/app");

        // (Opcional) Prefijo para mensajes de usuario
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/api/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setStreamBytesLimit(512 * 1024)
                .setHttpMessageCacheSize(1000)
                .setDisconnectDelay(30 * 1000);

        registry
                .addEndpoint("/api/ws")
                .setAllowedOriginPatterns("*");
    }
}