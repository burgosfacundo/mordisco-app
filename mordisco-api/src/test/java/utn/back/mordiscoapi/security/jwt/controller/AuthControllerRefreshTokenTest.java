package utn.back.mordiscoapi.security.jwt.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.security.jwt.model.entity.RefreshToken;
import utn.back.mordiscoapi.security.jwt.repository.RefreshTokenRepository;
import utn.back.mordiscoapi.security.jwt.service.RefreshTokenService;
import utn.back.mordiscoapi.security.jwt.utils.JwtUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class AuthControllerRefreshTokenTest {
    private static final String RAW_TOKEN = "A".repeat(43);

    @Mock private UserDetailsService userDetailsService;
    @Mock private AuthenticationManager authenticationManager;

    private StubRefreshTokenService refreshTokenService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenCookieSecure", false);
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenCookieSameSite", "lax");
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenExpiration", 2_592_000_000L);
        refreshTokenService = new StubRefreshTokenService();
        mockMvc = standaloneSetup(new AuthController(
                userDetailsService, authenticationManager, jwtUtil, refreshTokenService)).build();
    }

    @Test
    void accessTokenExpirationUsesApplicationJwtConfigurationProperty() throws NoSuchFieldException {
        Value value = AuthController.class.getDeclaredField("accessTokenExpiration").getAnnotation(Value.class);

        assertEquals("${app.jwt.access.expiration:900000}", value.value());
    }

    @Test
    void missingRefreshCookieReturnsUnauthorizedAndClearsCookie() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/refresh")).andReturn();

        assertUnauthorizedAndCleared(result);
        assertEquals(0, refreshTokenService.rotateCalls);
    }

    @Test
    void unknownExpiredRevokedAndDisabledRefreshFailuresShareSafeResponseAndCookieClearing() throws Exception {
        for (RefreshTokenService.RefreshTokenAuthenticationException failure : new RefreshTokenService.RefreshTokenAuthenticationException[]{
                new RefreshTokenService.RefreshTokenAuthenticationException(),
                new RefreshTokenService.RefreshTokenReuseException(),
                new RefreshTokenService.DisabledUserRefreshException()
        }) {
            refreshTokenService.rotateFailure = failure;

            MvcResult result = mockMvc.perform(post("/api/auth/refresh")
                            .cookie(new Cookie("refreshToken", RAW_TOKEN)))
                    .andReturn();

            assertUnauthorizedAndCleared(result);
        }
    }

    @Test
    void logoutClearsCookieEvenWhenNoRefreshCookieWasSent() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/logout")).andReturn();

        assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());
        assertEquals(0, refreshTokenService.revokeCalls);
        String cookie = result.getResponse().getHeader("Set-Cookie");
        assertTrue(cookie.startsWith("refreshToken="));
        assertTrue(cookie.contains("Path=/api/auth"));
        assertTrue(cookie.contains("Max-Age=0"));
    }

    private void assertUnauthorizedAndCleared(MvcResult result) {
        assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus());
        assertEquals(0, result.getResponse().getContentAsByteArray().length);
        String cookie = result.getResponse().getHeader("Set-Cookie");
        assertTrue(cookie.startsWith("refreshToken="));
        assertTrue(cookie.contains("Path=/api/auth"));
        assertTrue(cookie.contains("Max-Age=0"));
    }

    private static final class StubRefreshTokenService extends RefreshTokenService {
        private RuntimeException rotateFailure;
        private int rotateCalls;
        private int revokeCalls;

        private StubRefreshTokenService() {
            super(mock(RefreshTokenRepository.class), mock(UsuarioRepository.class));
        }

        @Override
        public RefreshTokenService.IssuedRefreshToken rotateRefreshToken(String rawToken, String userAgent, String ipAddress)
                throws NotFoundException {
            rotateCalls++;
            if (rotateFailure != null) {
                throw rotateFailure;
            }
            return null;
        }

        @Override
        public void revokeToken(String rawToken) {
            revokeCalls++;
        }

        @Override
        public void revokeAllUserSessions(Long userId) {
            revokeCalls++;
        }
    }
}
