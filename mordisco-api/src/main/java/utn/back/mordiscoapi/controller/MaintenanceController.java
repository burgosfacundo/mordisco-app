package utn.back.mordiscoapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utn.back.mordiscoapi.config.AppProperties;
import utn.back.mordiscoapi.service.MaintenanceService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
public class MaintenanceController {

    private static final String MAINTENANCE_SECRET_HEADER = "X-Maintenance-Secret";

    private final MaintenanceService maintenanceService;
    private final AppProperties appProperties;

    @PostMapping("/maintenance")
    public ResponseEntity<MaintenanceService.MaintenanceResult> runMaintenance(
            @RequestHeader(value = MAINTENANCE_SECRET_HEADER, required = false) String providedSecret) {
        if (!matchesConfiguredSecret(providedSecret, appProperties.getMaintenanceSecret())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(maintenanceService.runMaintenance());
    }

    static boolean matchesConfiguredSecret(String providedSecret, String configuredSecret) {
        if (providedSecret == null
                || configuredSecret == null
                || providedSecret.isBlank()
                || configuredSecret.isBlank()) {
            return false;
        }

        return MessageDigest.isEqual(
                configuredSecret.getBytes(StandardCharsets.UTF_8),
                providedSecret.getBytes(StandardCharsets.UTF_8)
        );
    }
}
