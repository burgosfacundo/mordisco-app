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

    @Test
    void combinedProductionProfilesRequireExactWebSocketOrigins() {
        appPropertiesRunner("prod", "schema-bootstrap", "demo-seed")
                .withPropertyValues(
                        "app.maintenance-secret=synthetic-maintenance-secret",
                        "app.mercado-pago.environment=sandbox",
                        "app.mercado-pago.webhook-secret=synthetic-webhook-secret",
                        "app.mercado-pago.notification-url=https://payments.example/api/pagos/webhook")
                .run(context -> assertStartupFailureContains(
                        context.getStartupFailure(), "Exact WebSocket allowed origins are required"));
    }

    @Test
    void combinedProductionProfilesRequireMaintenanceSecret() {
        appPropertiesRunner("prod", "schema-bootstrap", "demo-seed")
                .withPropertyValues(
                        "app.websocket-allowed-origins=https://frontend.example",
                        "app.mercado-pago.environment=sandbox",
                        "app.mercado-pago.webhook-secret=synthetic-webhook-secret",
                        "app.mercado-pago.notification-url=https://payments.example/api/pagos/webhook")
                .run(context -> assertStartupFailureContains(
                        context.getStartupFailure(), "MAINTENANCE_SECRET must be provided for the prod profile"));
    }

    @Test
    void combinedProductionProfilesRequireMercadoPagoWebhookConfiguration() {
        appPropertiesRunner("prod", "schema-bootstrap", "demo-seed")
                .withPropertyValues(
                        "app.websocket-allowed-origins=https://frontend.example",
                        "app.maintenance-secret=synthetic-maintenance-secret",
                        "app.mercado-pago.environment=sandbox",
                        "app.mercado-pago.notification-url=https://payments.example/api/pagos/webhook")
                .run(context -> assertStartupFailureContains(
                        context.getStartupFailure(), "Mercado Pago webhook secret is required in production"));
    }

    @Test
    void developmentProfileDoesNotRequireProductionOnlyAppProperties() {
        appPropertiesRunner("dev")
                .run(context -> assertNull(context.getStartupFailure(),
                        () -> failureText(context.getStartupFailure())));
    }

    private void assertStartupFailureContains(Throwable failure, String expectedMessage) {
        assertNotNull(failure, "Expected startup failure containing: " + expectedMessage);
        String diagnostics = failureText(failure);
        assertTrue(diagnostics.contains(expectedMessage), diagnostics);
    }

    private ApplicationContextRunner appPropertiesRunner(String... activeProfiles) {
        return new ApplicationContextRunner()
                .withInitializer(context -> context.getEnvironment().setActiveProfiles(activeProfiles))
                .withUserConfiguration(AppPropertiesOnlyConfiguration.class);
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

    @TestConfiguration(proxyBeanMethods = false)
    @EnableConfigurationProperties(AppProperties.class)
    static class AppPropertiesOnlyConfiguration {
    }
}
