package utn.back.mordiscoapi.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import utn.back.mordiscoapi.security.jwt.utils.JwtRequestFilter;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

class SecurityConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(SecurityTestConfiguration.class)
            .withPropertyValues("app.frontend-url=http://localhost:4200");

    @Test
    void permitsOnlyTheExactMaintenanceEndpoint() throws Exception {
        String[] whitelist = readStringArray("AUTH_WHITELIST");

        assertTrue(Arrays.asList(whitelist).contains(SecurityConfiguration.MAINTENANCE_ENDPOINT));
        assertFalse(Arrays.asList(whitelist).contains("/api/internal/**"));
    }

    @Test
    void ignoresCsrfForTheExactMaintenanceEndpoint() throws Exception {
        String[] csrfIgnoredEndpoints = readStringArray("CSRF_IGNORED_ENDPOINTS");

        assertTrue(Arrays.asList(csrfIgnoredEndpoints).contains(SecurityConfiguration.MAINTENANCE_ENDPOINT));
        assertFalse(Arrays.asList(csrfIgnoredEndpoints).contains("/api/internal/**"));
    }

    @Test
    void combinedProductionProfilesKeepCsrfEnabled() {
        contextRunner
                .withInitializer(context -> context.getEnvironment().setActiveProfiles(
                        "prod", "schema-bootstrap", "demo-seed"))
                .run(context -> {
                    assertNull(context.getStartupFailure(),
                            () -> "Unexpected startup failure: " + context.getStartupFailure());

                    DefaultSecurityFilterChain filterChain = assertInstanceOf(
                            DefaultSecurityFilterChain.class,
                            context.getBean(SecurityFilterChain.class)
                    );
                    assertTrue(filterChain.getFilters().stream().anyMatch(CsrfFilter.class::isInstance));
                    assertTrue(filterChain.getFilters().stream()
                            .anyMatch(SecurityConfiguration.CsrfCookieFilter.class::isInstance));
                });
    }

    @TestConfiguration(proxyBeanMethods = false)
    @Import(SecurityConfiguration.class)
    static class SecurityTestConfiguration {
        @Bean
        JwtRequestFilter jwtRequestFilter() {
            return mock(JwtRequestFilter.class);
        }
    }

    private String[] readStringArray(String fieldName) throws Exception {
        Field field = SecurityConfiguration.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (String[]) field.get(null);
    }
}
