package utn.back.mordiscoapi.config;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
class PasswordRecoveryConfigurationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(AppPropertiesConfiguration.class);
    @Test
    void usesSafeIndependentDefaultsForPasswordRecoverySettings() {
        AppProperties properties = new AppProperties();
        assertEquals(3600, properties.getPasswordRecovery().getExpirationSeconds());
        assertEquals(300, properties.getPasswordRecovery().getCooldownSeconds());
        assertTrue(validator.validate(properties).isEmpty());
    }
    @Test
    void rejectsZeroNegativeAndOutOfRangeRecoverySettings() {
        AppProperties properties = new AppProperties();
        properties.getPasswordRecovery().setExpirationSeconds(0);
        properties.getPasswordRecovery().setCooldownSeconds(86401);
        assertFalse(validator.validate(properties).isEmpty());
        properties.getPasswordRecovery().setExpirationSeconds(-1);
        properties.getPasswordRecovery().setCooldownSeconds(-1);
        assertFalse(validator.validate(properties).isEmpty());
    }
    @Test
    void failsStartupForNonNumericAndInvalidBoundConfiguration() {
        contextRunner
                .withPropertyValues("app.password-recovery.expiration-seconds=not-a-number")
                .run(context -> assertTrue(context.getStartupFailure() != null));
        contextRunner
                .withPropertyValues("app.password-recovery.cooldown-seconds=0")
                .run(context -> assertTrue(context.getStartupFailure() != null));
    }
    @Test
    void exposesUtcClockAndTruncatesCapturedTimesToMySQLMicroseconds() {
        TimeConfiguration timeConfiguration = new TimeConfiguration();
        Instant instant = Instant.parse("2026-08-29T12:34:56.123456789Z");
        assertEquals(ZoneOffset.UTC, timeConfiguration.utcClock().getZone());
        assertEquals(
                LocalDateTime.of(2026, 8, 29, 12, 34, 56, 123456000),
                TimeConfiguration.toUtcMicroseconds(instant)
        );
    }
    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(AppProperties.class)
    static class AppPropertiesConfiguration {
    }
}
