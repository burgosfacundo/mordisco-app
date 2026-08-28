package utn.back.mordiscoapi.security.websocket;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import utn.back.mordiscoapi.security.jwt.utils.JwtUtil;

import java.util.List;

@Component
public class WebSocketAuthenticationChannelInterceptor implements ChannelInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHENTICATION_FAILURE = "WebSocket authentication failed";

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public WebSocketAuthenticationChannelInterceptor(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(@NonNull Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() != StompCommand.CONNECT) {
            return message;
        }

        try {
            String token = extractAccessToken(accessor.getNativeHeader(AUTHORIZATION_HEADER));
            String email = jwtUtil.extractUserName(token);
            UserDetails user = userDetailsService.loadUserByUsername(email);
            if (!jwtUtil.isAccessTokenValid(token, user)) {
                throw authenticationFailure();
            }

            accessor.setUser(UsernamePasswordAuthenticationToken.authenticated(
                    user.getUsername(),
                    null,
                    user.getAuthorities()
            ));
            accessor.removeNativeHeader(AUTHORIZATION_HEADER);
            return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
        } catch (BadCredentialsException exception) {
            throw exception;
        } catch (Exception exception) {
            throw authenticationFailure();
        }
    }

    private String extractAccessToken(List<String> authorizationHeaders) {
        if (authorizationHeaders == null || authorizationHeaders.size() != 1) {
            throw authenticationFailure();
        }

        String authorization = authorizationHeaders.getFirst();
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            throw authenticationFailure();
        }

        String token = authorization.substring(BEARER_PREFIX.length());
        if (token.isBlank() || !token.equals(token.trim())) {
            throw authenticationFailure();
        }
        return token;
    }

    private BadCredentialsException authenticationFailure() {
        return new BadCredentialsException(AUTHENTICATION_FAILURE);
    }
}
