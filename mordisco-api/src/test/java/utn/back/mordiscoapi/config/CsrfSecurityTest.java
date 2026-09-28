package utn.back.mordiscoapi.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import utn.back.mordiscoapi.common.constraint.ConstraintViolationMessageResolver;
import utn.back.mordiscoapi.model.entity.Rol;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.security.jwt.controller.AuthController;
import utn.back.mordiscoapi.security.jwt.model.entity.RefreshToken;
import utn.back.mordiscoapi.security.jwt.service.RefreshTokenService;
import utn.back.mordiscoapi.security.jwt.utils.JwtUtil;
import utn.back.mordiscoapi.service.impl.UsuarioServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"prod", "ci"})
@WebMvcTest(
        controllers = AuthController.class,
        properties = {
                "app.http-allowed-origins=https://frontend.example",
                "server.port=8080",
                "server.servlet.session.cookie.secure=true",
                "server.servlet.session.cookie.same-site=strict"
        })
@Import(SecurityConfiguration.class)
class CsrfSecurityTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private ConstraintViolationMessageResolver constraintViolationMessageResolver;
    @MockBean private AuthenticationManager authenticationManager;
    @MockBean private JwtUtil jwtUtil;
    @MockBean private RefreshTokenService refreshTokenService;
    @MockBean private UsuarioServiceImpl usuarioService;

    @Test
    void csrfBootstrapReturnsTokenCookieFlagsWithoutCreatingASession() throws Exception {
        CsrfCredentials credentials = bootstrap();
        String setCookie = credentials.setCookie();

        assertTrue(setCookie.startsWith("XSRF-TOKEN=" + credentials.token() + ";"));
        assertTrue(setCookie.contains("Path=/api/auth"));
        assertTrue(setCookie.contains("Secure"));
        assertTrue(setCookie.contains("HttpOnly"));
        assertEquals("Strict", credentials.responseCookie().getAttribute("SameSite"));
        assertFalse(setCookie.contains("JSESSIONID"));
    }

    @Test
    void missingInvalidAndParameterTokensAreRejectedWithSafeJsonAndSecurityHeaders() throws Exception {
        CsrfCredentials credentials = bootstrap();

        MvcResult missingHeader = mockMvc.perform(post("/api/auth/login")
                        .cookie(credentials.cookie()))
                .andReturn();
        assertSafeCsrfFailure(missingHeader);

        MvcResult invalidHeader = mockMvc.perform(post("/api/auth/refresh")
                        .cookie(credentials.cookie())
                        .header("X-XSRF-TOKEN", "not-the-cookie-token"))
                .andReturn();
        assertSafeCsrfFailure(invalidHeader);

        MvcResult parameterToken = mockMvc.perform(post("/api/auth/logout")
                        .cookie(credentials.cookie())
                        .param("_csrf", credentials.token()))
                .andReturn();
        assertSafeCsrfFailure(parameterToken);
    }

    @Test
    void sockJsPostIsNotSubjectToTheAuthCsrfMatcher() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/ws/000/session/xhr").content("[]"))
                .andReturn();

        assertNotEquals(403, result.getResponse().getStatus());
        assertFalse(result.getResponse().getContentAsString().contains("Invalid CSRF token"));
    }

    @Test
    void validHeaderTokenReachesLoginRefreshAndLogoutHandlers() throws Exception {
        CsrfCredentials credentials = bootstrap();
        Usuario user = testUser();
        when(usuarioService.loadUserByUsername(user.getEmail())).thenReturn(user);
        when(jwtUtil.generateAccessToken(user)).thenReturn("access-token");
        when(jwtUtil.getClientIP(any(HttpServletRequest.class))).thenReturn("127.0.0.1");
        when(jwtUtil.extractRefreshTokenFromCookie(any(HttpServletRequest.class))).thenReturn(Optional.empty());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuario(user);
        when(refreshTokenService.issueRefreshToken(eq(user.getId()), any(), any()))
                .thenReturn(new RefreshTokenService.IssuedRefreshToken(refreshToken, "opaque-refresh-token"));

        mockMvc.perform(post("/api/auth/login")
                        .cookie(credentials.cookie())
                        .header("X-XSRF-TOKEN", credentials.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"person@example.com\",\"password\":\"correct-password\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(credentials.cookie())
                        .header("X-XSRF-TOKEN", credentials.token()))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/auth/logout")
                        .cookie(credentials.cookie())
                        .header("X-XSRF-TOKEN", credentials.token()))
                .andExpect(status().isNoContent());

        verify(authenticationManager).authenticate(any());
        verify(jwtUtil).generateAccessToken(user);
        verify(refreshTokenService).issueRefreshToken(eq(user.getId()), any(), any());
        verify(jwtUtil, times(2)).extractRefreshTokenFromCookie(any(HttpServletRequest.class));
    }

    @Test
    void bearerOnlyEndpointDoesNotRequireCsrfToken() throws Exception {
        Usuario user = testUser();
        String accessToken = "valid-access-token";
        when(jwtUtil.extractUserName(accessToken)).thenReturn(user.getEmail());
        when(usuarioService.loadUserByUsername(user.getEmail())).thenReturn(user);
        when(jwtUtil.isAccessTokenValid(accessToken, user)).thenReturn(true);

        mockMvc.perform(post("/api/auth/logout-all")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());

        verify(refreshTokenService).revokeAllUserSessions(user.getId());
    }

    private CsrfCredentials bootstrap() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        String token = body.path("token").asText();
        String setCookie = result.getResponse().getHeader("Set-Cookie");
        Cookie responseCookie = result.getResponse().getCookie("XSRF-TOKEN");

        assertFalse(token.isBlank());
        assertNotNull(setCookie);
        assertNotNull(responseCookie);
        return new CsrfCredentials(token, new Cookie("XSRF-TOKEN", token), responseCookie, setCookie);
    }

    private void assertSafeCsrfFailure(MvcResult result) throws Exception {
        assertEquals(403, result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        assertEquals("forbidden", body.path("error").asText());
        assertEquals("Invalid CSRF token", body.path("message").asText());
        assertEquals("nosniff", result.getResponse().getHeader("X-Content-Type-Options"));
        assertEquals("DENY", result.getResponse().getHeader("X-Frame-Options"));
        assertTrue(result.getResponse().getHeader("Cache-Control").contains("no-cache"));
    }

    private Usuario testUser() {
        return Usuario.builder()
                .id(17L)
                .nombre("Test User")
                .email("person@example.com")
                .bajaLogica(false)
                .rol(Rol.builder().nombre("ROLE_CLIENTE").build())
                .build();
    }

    private record CsrfCredentials(String token, Cookie cookie, Cookie responseCookie, String setCookie) {
    }
}
