package utn.back.mordiscoapi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseEntity;
import utn.back.mordiscoapi.config.AppProperties;
import utn.back.mordiscoapi.service.MaintenanceService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class MaintenanceControllerTest {

    private static final String SECRET = "synthetic-maintenance-secret";

    private MaintenanceService maintenanceService;
    private MaintenanceController controller;

    @BeforeEach
    void setUp() {
        maintenanceService = mock(MaintenanceService.class);
        AppProperties appProperties = new AppProperties();
        appProperties.setMaintenanceSecret(SECRET);
        controller = new MaintenanceController(maintenanceService, appProperties);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"wrong-secret", "   "})
    void rejectsMissingWrongOrBlankSecretsWithoutInvokingMaintenance(String providedSecret) {
        ResponseEntity<MaintenanceService.MaintenanceResult> response = controller.runMaintenance(providedSecret);

        assertEquals(401, response.getStatusCode().value());
        assertNull(response.getBody());
        verifyNoInteractions(maintenanceService);
    }

    @Test
    void acceptsTheConfiguredSecretAndInvokesMaintenanceOnce() {
        MaintenanceService.MaintenanceResult result = new MaintenanceService.MaintenanceResult("completed");
        when(maintenanceService.runMaintenance()).thenReturn(result);

        ResponseEntity<MaintenanceService.MaintenanceResult> response = controller.runMaintenance(SECRET);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(result, response.getBody());
        verify(maintenanceService).runMaintenance();
    }

    @Test
    void rejectsBlankConfiguredSecrets() {
        AppProperties appProperties = new AppProperties();
        appProperties.setMaintenanceSecret(" ");
        MaintenanceController controllerWithBlankConfiguration =
                new MaintenanceController(maintenanceService, appProperties);

        ResponseEntity<MaintenanceService.MaintenanceResult> response =
                controllerWithBlankConfiguration.runMaintenance(SECRET);

        assertEquals(401, response.getStatusCode().value());
        verifyNoInteractions(maintenanceService);
    }
}
