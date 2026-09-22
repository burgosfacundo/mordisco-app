package utn.back.mordiscoapi.security.jwt.utils;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private static final String SECRET = "synthetic-test-jwt-secret-with-at-least-32-characters";

    @Test
    void combinedProductionProfilesKeepRefreshCookiesSecureAndStrict() {
        JwtUtil jwtUtil = jwtUtil("prod", "schema-bootstrap", "demo-seed");

        MockHttpServletResponse setResponse = new MockHttpServletResponse();
        jwtUtil.setRefreshTokenCookie(setResponse, "synthetic-refresh-token");
        assertProductionCookie(setResponse);

        MockHttpServletResponse clearResponse = new MockHttpServletResponse();
        jwtUtil.clearRefreshTokenCookie(clearResponse);
        assertProductionCookie(clearResponse);
    }

    @Test
    void nonProductionRefreshCookiesRemainLaxAndInsecure() {
        JwtUtil jwtUtil = jwtUtil("dev");
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtUtil.setRefreshTokenCookie(response, "synthetic-refresh-token");

        String header = response.getHeader(HttpHeaders.SET_COOKIE);
        assertNotNull(header);
        assertFalse(header.contains("; Secure"));
        assertTrue(header.contains("SameSite=Lax"));
    }

    private JwtUtil jwtUtil(String... activeProfiles) {
        StandardEnvironment environment = new StandardEnvironment();
        environment.setActiveProfiles(activeProfiles);

        JwtUtil jwtUtil = new JwtUtil(environment);
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        jwtUtil.init();
        return jwtUtil;
    }

    private void assertProductionCookie(HttpServletResponse response) {
        String header = response.getHeader(HttpHeaders.SET_COOKIE);
        assertNotNull(header);
        assertTrue(header.contains("; Secure"));
        assertTrue(header.contains("HttpOnly"));
        assertTrue(header.contains("SameSite=Strict"));
    }
}
