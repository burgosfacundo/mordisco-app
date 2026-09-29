package utn.back.mordiscoapi.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.DefaultCorsProcessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityConfigurationTest {

    private static final List<String> ALLOWED_METHODS =
            List.of("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    private static final List<String> ALLOWED_HEADERS =
            List.of("Authorization", "Content-Type", "Accept", "X-XSRF-TOKEN");

    @Test
    void developmentDefaultsToTheExactLocalFrontendOrigin() {
        CorsConfiguration cors = corsConfiguration("dev", "");

        assertEquals(List.of("http://localhost:4200"), cors.getAllowedOrigins());
        assertEquals("http://localhost:4200", cors.checkOrigin("http://localhost:4200"));
        assertNull(cors.checkOrigin("http://localhost:4201"));
        assertNull(cors.getAllowedOriginPatterns());
    }

    @Test
    void permitsCredentialedPreflightOnlyForConfiguredOriginsMethodsAndHeaders() throws IOException {
        CorsConfiguration cors = corsConfiguration("prod", "https://frontend.example,http://localhost:4200");

        assertEquals(List.of("https://frontend.example", "http://localhost:4200"), cors.getAllowedOrigins());
        assertEquals(ALLOWED_METHODS, cors.getAllowedMethods());
        assertEquals(ALLOWED_HEADERS, cors.getAllowedHeaders());
        assertTrue(cors.getAllowCredentials());
        assertTrue(cors.getExposedHeaders().isEmpty());

        MockHttpServletResponse response = processPreflight(cors,
                preflight("https://frontend.example", "PATCH", String.join(", ", ALLOWED_HEADERS)));

        assertEquals("https://frontend.example", response.getHeader("Access-Control-Allow-Origin"));
        assertEquals("true", response.getHeader("Access-Control-Allow-Credentials"));
        assertTrue(response.getHeader("Access-Control-Allow-Methods").contains("PATCH"));
        String allowedHeaders = response.getHeader("Access-Control-Allow-Headers").toLowerCase(Locale.ROOT);
        ALLOWED_HEADERS.forEach(header -> assertTrue(allowedHeaders.contains(header.toLowerCase(Locale.ROOT))));
        assertNull(response.getHeader("Access-Control-Expose-Headers"));
    }

    @Test
    void rejectsSubdomainsUnlistedMethodsAndUnlistedHeaders() throws IOException {
        CorsConfiguration cors = corsConfiguration("prod", "https://frontend.example");
        assertRejectedPreflight(cors, preflight("https://sub.frontend.example", "PATCH", "Authorization"));
        assertRejectedPreflight(cors, preflight("https://frontend.example", "TRACE", "Authorization"));
        assertRejectedPreflight(cors, preflight("https://frontend.example", "PATCH", "X-Requested-With"));
    }

    @Test
    void productionFailsFastForMissingEmptyWildcardAndMalformedOrigins() {
        List<String> invalidConfigurations = Arrays.asList(
                null,
                "",
                "   ",
                "*",
                "https://*.frontend.example",
                "https://frontend.example/path",
                "https://frontend.example?query=value",
                "https://frontend.example:65536",
                "not-an-origin",
                "https://frontend.example,"
        );

        for (String invalidConfiguration : invalidConfigurations) {
            assertThrows(IllegalStateException.class,
                    () -> corsConfiguration("prod", invalidConfiguration),
                    () -> "invalid origin configuration: " + invalidConfiguration);
        }
    }

    @Test
    void productionProfileAmongMultipleActiveProfilesStillFailsClosed() {
        assertThrows(IllegalStateException.class, () -> corsConfiguration("test, prod", ""));
    }

    @Test
    void rejectsWildcardAndMalformedOriginsOutsideProductionToo() {
        assertThrows(IllegalStateException.class, () -> corsConfiguration("dev", "https://*.example"));
        assertThrows(IllegalStateException.class, () -> corsConfiguration("dev", "https://example/path"));
    }

    private CorsConfiguration corsConfiguration(String profile, String origins) {
        CorsConfigurationSource source = SecurityConfiguration.createCorsConfigurationSource(profile, origins);
        return source.getCorsConfiguration(new MockHttpServletRequest("GET", "/api/example"));
    }

    private MockHttpServletResponse processPreflight(CorsConfiguration cors, MockHttpServletRequest request)
            throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertTrue(new DefaultCorsProcessor().processRequest(cors, request, response));
        return response;
    }

    private void assertRejectedPreflight(CorsConfiguration cors, MockHttpServletRequest request)
            throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertFalse(new DefaultCorsProcessor().processRequest(cors, request, response));
        assertEquals(403, response.getStatus());
        assertNull(response.getHeader("Access-Control-Allow-Origin"));
    }

    private MockHttpServletRequest preflight(String origin, String requestedMethod, String requestedHeaders) {
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/api/example");
        request.addHeader("Origin", origin);
        request.addHeader("Access-Control-Request-Method", requestedMethod);
        request.addHeader("Access-Control-Request-Headers", requestedHeaders);
        return request;
    }
}
