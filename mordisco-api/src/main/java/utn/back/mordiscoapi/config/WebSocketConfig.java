package utn.back.mordiscoapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.*;
import utn.back.mordiscoapi.security.websocket.WebSocketAuthenticationChannelInterceptor;
import utn.back.mordiscoapi.security.websocket.WebSocketStompErrorHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthenticationChannelInterceptor authenticationInterceptor;
    private final WebSocketStompErrorHandler errorHandler;
    private final AuthorizationManager<Message<?>> authorizationManager;
    private final AppProperties appProperties;

    public WebSocketConfig(WebSocketAuthenticationChannelInterceptor authenticationInterceptor,
                           WebSocketStompErrorHandler errorHandler,
                           AuthorizationManager<Message<?>> authorizationManager,
                           AppProperties appProperties) {
        this.authenticationInterceptor = authenticationInterceptor;
        this.errorHandler = errorHandler;
        this.authorizationManager = authorizationManager;
        this.appProperties = appProperties;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
                authenticationInterceptor,
                new SecurityContextChannelInterceptor(),
                new AuthorizationChannelInterceptor(authorizationManager));
    }

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
        String[] allowedOrigins = appProperties.getWebsocketAllowedOrigins().toArray(String[]::new);
        registry.setErrorHandler(errorHandler);
        registry
                .addEndpoint("/api/ws")
                .setAllowedOrigins(allowedOrigins)
                .withSockJS()
                .setStreamBytesLimit(512 * 1024)
                .setHttpMessageCacheSize(1000)
                .setDisconnectDelay(30 * 1000);

        registry
                .addEndpoint("/api/ws")
                .setAllowedOrigins(allowedOrigins);
    }
}
