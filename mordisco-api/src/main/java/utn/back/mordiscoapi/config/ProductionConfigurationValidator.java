package utn.back.mordiscoapi.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Profile("prod")
@Validated
@ConfigurationProperties(prefix = "app")
public class ProductionConfigurationValidator {

    @NotBlank(message = "MAINTENANCE_SECRET must be provided for the prod profile")
    private String maintenanceSecret;

    public String getMaintenanceSecret() {
        return maintenanceSecret;
    }

    public void setMaintenanceSecret(String maintenanceSecret) {
        this.maintenanceSecret = maintenanceSecret;
    }
}
