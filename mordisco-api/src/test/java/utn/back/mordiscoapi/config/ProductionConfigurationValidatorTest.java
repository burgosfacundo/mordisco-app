package utn.back.mordiscoapi.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Profile;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductionConfigurationValidatorTest {

    @Test
    void productionRequiresMaintenanceSecret() {
        validationRunner("prod")
                .run(context -> {
                    Throwable failure = context.getStartupFailure();
                    assertNotNull(failure);
                    String diagnostics = failureText(failure);
                    assertTrue(diagnostics.contains("MAINTENANCE_SECRET"), diagnostics);
                    assertFalse(diagnostics.contains("synthetic-maintenance-secret"));
                });
    }

    @Test
    void productionAcceptsAConfiguredMaintenanceSecret() {
        validationRunner("prod")
                .withPropertyValues("app.maintenance-secret=synthetic-maintenance-secret")
                .run(context -> {
                    assertNull(context.getStartupFailure(),
                            () -> failureText(context.getStartupFailure()));
                    assertEqualsSecret(context.getBean(ProductionConfigurationValidator.class));
                });
    }

    @Test
    void localProfilesRemainUsableWithoutMaintenanceSecret() {
        validationRunner("dev")
                .run(context -> {
                    assertNull(context.getStartupFailure(),
                            () -> failureText(context.getStartupFailure()));
                    assertFalse(context.containsBean("productionConfigurationValidator"));
                });
    }

    private void assertEqualsSecret(ProductionConfigurationValidator validator) {
        assertTrue("synthetic-maintenance-secret".equals(validator.getMaintenanceSecret()));
    }

    private ApplicationContextRunner validationRunner(String profile) {
        return new ApplicationContextRunner()
                .withInitializer(new ConfigDataApplicationContextInitializer())
                .withUserConfiguration(ValidationOnlyConfiguration.class)
                .withPropertyValues(
                        "spring.config.name=application",
                        "spring.profiles.active=" + profile,
                        "SPRING_PROFILES_ACTIVE=" + profile);
    }

    private String failureText(Throwable failure) {
        StringBuilder text = new StringBuilder();
        while (failure != null) {
            text.append(failure).append('\n');
            failure = failure.getCause();
        }
        return text.toString();
    }

    @TestConfiguration(proxyBeanMethods = false)
    @Profile("prod")
    @EnableConfigurationProperties(ProductionConfigurationValidator.class)
    static class ValidationOnlyConfiguration {
    }
}
