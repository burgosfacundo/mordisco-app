package utn.back.mordiscoapi.config;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityConfigurationTest {

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

    private String[] readStringArray(String fieldName) throws Exception {
        Field field = SecurityConfiguration.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (String[]) field.get(null);
    }
}
