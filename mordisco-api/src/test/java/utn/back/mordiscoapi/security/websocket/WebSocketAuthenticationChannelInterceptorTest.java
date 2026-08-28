package utn.back.mordiscoapi.security.websocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import utn.back.mordiscoapi.model.entity.Rol;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.security.jwt.utils.JwtUtil;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WebSocketAuthenticationChannelInterceptorTest {

    private static final String EMAIL = "user@example.test";
    private static final String ACCESS_TOKEN = "access-token";

    private JwtUtil jwtUtil;
    private UserDetailsService userDetailsService;
    private WebSocketAuthenticationChannelInterceptor interceptor;

    @BeforeEach
    void setUp() {
        jwtUtil = mock(JwtUtil.class);
        userDetailsService = mock(UserDetailsService.class);
        interceptor = new WebSocketAuthenticationChannelInterceptor(jwtUtil, userDetailsService);
    }

    @Test
    void authenticatesConnectWithCanonicalStringPrincipalAndNoRetainedSensitiveState() {
        Usuario usuario = Usuario.builder()
                .email(EMAIL)
                .password("stored-password")
                .bajaLogica(false)
                .rol(Rol.builder().nombre("ROLE_CLIENTE").build())
                .build();
        when(jwtUtil.extractUserName(ACCESS_TOKEN)).thenReturn(EMAIL);
        when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(usuario);
        when(jwtUtil.isAccessTokenValid(ACCESS_TOKEN, usuario)).thenReturn(true);

        Message<?> result = interceptor.preSend(connect(List.of("Bearer " + ACCESS_TOKEN)), null);
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(result);

        UsernamePasswordAuthenticationToken authentication =
                assertInstanceOf(UsernamePasswordAuthenticationToken.class, headers.getUser());
        String canonicalName = usuario.getUsername();
        String principal = assertInstanceOf(String.class, authentication.getPrincipal());
        assertSame(canonicalName, principal);
        assertEquals(EMAIL, principal);
        assertEquals(EMAIL, authentication.getName());
        assertEquals(List.of("ROLE_CLIENTE"), authentication.getAuthorities().stream()
                .map(Object::toString)
                .toList());
        assertNull(authentication.getCredentials());
        assertNull(authentication.getDetails());
        assertTrue(authentication.isAuthenticated());
        assertFalse(authentication.getPrincipal() instanceof Usuario);
        assertFalse(authentication.toString().contains(usuario.getPassword()));
        assertFalse(authentication.toString().contains(ACCESS_TOKEN));
        assertFalse(authentication.toString().contains("Authorization"));
        assertNull(headers.getFirstNativeHeader("Authorization"));
        assertFalse(headers.toNativeHeaderMap().containsKey("Authorization"));
        assertEquals("safe-value", headers.getFirstNativeHeader("X-Safe-Header"));
        assertFalse(result.getHeaders().values().stream()
                .anyMatch(value -> value.toString().contains(ACCESS_TOKEN)));
        verify(userDetailsService).loadUserByUsername(EMAIL);
        verify(jwtUtil).isAccessTokenValid(ACCESS_TOKEN, usuario);
    }

    @ParameterizedTest(name = "rejects {0}")
    @MethodSource("invalidAuthorizationHeaders")
    void rejectsMissingDuplicateAndMalformedAuthorizationWithoutLoadingAUser(
            String ignored, List<String> authorizationHeaders) {
        assertThrows(BadCredentialsException.class,
                () -> interceptor.preSend(connect(authorizationHeaders), null));

        verify(userDetailsService, never()).loadUserByUsername(org.mockito.ArgumentMatchers.anyString());
    }

    @ParameterizedTest(name = "rejects {0} token")
    @MethodSource("invalidTokenClasses")
    void rejectsInvalidExpiredRefreshAndPasswordRecoveryTokens(String ignored, String token) {
        when(jwtUtil.extractUserName(token)).thenReturn(EMAIL);
        Usuario usuario = Usuario.builder().email(EMAIL).build();
        when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(usuario);
        when(jwtUtil.isAccessTokenValid(token, usuario)).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> interceptor.preSend(connect(List.of("Bearer " + token)), null));
    }

    @Test
    void rejectsTokenParsingFailuresWithSanitizedException() {
        String sensitiveToken = "secret.invalid.token";
        when(jwtUtil.extractUserName(sensitiveToken)).thenThrow(new IllegalArgumentException("parser: " + sensitiveToken));

        BadCredentialsException failure = assertThrows(BadCredentialsException.class,
                () -> interceptor.preSend(connect(List.of("Bearer " + sensitiveToken)), null));

        assertEquals("WebSocket authentication failed", failure.getMessage());
        assertNull(failure.getCause());
    }

    @Test
    void leavesNonConnectFramesForTheLaterAuthorizationInterceptor() {
        Message<byte[]> message = stompMessage(StompCommand.SUBSCRIBE);

        assertEquals(message, interceptor.preSend(message, null));
        verify(jwtUtil, never()).extractUserName(org.mockito.ArgumentMatchers.anyString());
    }

    private static Stream<Arguments> invalidAuthorizationHeaders() {
        return Stream.of(
                Arguments.of("missing", List.of()),
                Arguments.of("duplicate", List.of("Bearer one", "Bearer two")),
                Arguments.of("blank bearer", List.of("Bearer ")),
                Arguments.of("wrong scheme", List.of("Basic credentials")),
                Arguments.of("leading whitespace", List.of(" Bearer token"))
        );
    }

    private static Stream<Arguments> invalidTokenClasses() {
        return Stream.of(
                Arguments.of("invalid", "invalid-token"),
                Arguments.of("expired", "expired-token"),
                Arguments.of("refresh", "refresh-token"),
                Arguments.of("password recovery", "password-recovery-token")
        );
    }

    private static Message<byte[]> connect(List<String> authorizationHeaders) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        authorizationHeaders.forEach(value -> accessor.addNativeHeader("Authorization", value));
        accessor.addNativeHeader("X-Safe-Header", "safe-value");
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    private static Message<byte[]> stompMessage(StompCommand command) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(command);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }
}
