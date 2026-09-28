package utn.back.mordiscoapi.security.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import utn.back.mordiscoapi.security.jwt.utils.JwtUtil;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilRefreshCookieTest {
    @Test
    void creationAndDeletionShareCookieScopeAndConfiguredSecurityAttributes() {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenCookieSecure", true);
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenCookieSameSite", "strict");
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenExpiration", 86_400_000L);
        MockHttpServletResponse creation = new MockHttpServletResponse();
        MockHttpServletResponse deletion = new MockHttpServletResponse();

        jwtUtil.setRefreshTokenCookie(creation, "opaque-raw-token");
        jwtUtil.clearRefreshTokenCookie(deletion);

        String createdCookie = creation.getHeader("Set-Cookie");
        String deletedCookie = deletion.getHeader("Set-Cookie");
        assertTrue(createdCookie.startsWith("refreshToken=opaque-raw-token"));
        assertTrue(createdCookie.contains("Max-Age=86400"));
        assertTrue(deletedCookie.startsWith("refreshToken="));
        assertTrue(deletedCookie.contains("Max-Age=0"));

        for (String attribute : new String[]{"Path=/api/auth", "HttpOnly", "Secure", "SameSite=strict"}) {
            assertTrue(createdCookie.contains(attribute));
            assertTrue(deletedCookie.contains(attribute));
        }
    }

    @Test
    void laxCookieUsesConfiguredSecurityAttributes() {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenCookieSecure", false);
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenCookieSameSite", "lax");
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenExpiration", 60_000L);
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtUtil.setRefreshTokenCookie(response, "opaque-raw-token");

        String cookie = response.getHeader("Set-Cookie");
        assertTrue(cookie.contains("Path=/api/auth"));
        assertTrue(cookie.contains("SameSite=lax"));
        assertTrue(cookie.contains("Max-Age=60"));
        assertFalse(cookie.contains("; Secure"));
    }
}
