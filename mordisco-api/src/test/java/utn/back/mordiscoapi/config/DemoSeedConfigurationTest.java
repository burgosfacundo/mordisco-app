package utn.back.mordiscoapi.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DemoSeedConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new ConfigDataApplicationContextInitializer())
            .withUserConfiguration(DemoSeedConfiguration.class)
            .withPropertyValues("spring.config.name=application");

    @Test
    void normalProductionIsAcceptedWithoutDemoConfigurationSideEffects() {
        runner("prod").run(context -> {
            assertNull(context.getStartupFailure(),
                    () -> "Unexpected startup failure: " + failureText(context.getStartupFailure()));
            assertFalse(context.containsBean("demoSeedConfiguration"));
            assertTrue(context.getBeansOfType(ApplicationRunner.class).isEmpty());
            assertTrue(context.getBeansOfType(CommandLineRunner.class).isEmpty());
            assertEquals("never", context.getEnvironment().getProperty("spring.sql.init.mode"));
        });
    }

    @Test
    void exactCombinedProfilesAreAcceptedWithoutInstantiatingASeedRunner() {
        runner("prod,schema-bootstrap,demo-seed").run(context -> {
            assertNull(context.getStartupFailure(),
                    () -> "Unexpected startup failure: " + failureText(context.getStartupFailure()));
            assertTrue(context.containsBean("demoSeedConfiguration"));
            assertTrue(context.getBeansOfType(ApplicationRunner.class).isEmpty());
            assertTrue(context.getBeansOfType(CommandLineRunner.class).isEmpty());
            assertEquals("never", context.getEnvironment().getProperty("spring.sql.init.mode"));
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "demo-seed",
            "dev,demo-seed",
            "prod,demo-seed",
            "schema-bootstrap,demo-seed",
            "prod,schema-bootstrap,dev,demo-seed"
    })
    void unsafeDemoSeedProfileCombinationsFailClosed(String profiles) {
        runner(profiles).run(context -> {
            Throwable failure = context.getStartupFailure();
            assertNotNull(failure);
            String diagnostics = failureText(failure);
            assertTrue(diagnostics.contains("prod"), diagnostics);
            assertTrue(diagnostics.contains("schema-bootstrap"), diagnostics);
        });
    }

    private ApplicationContextRunner runner(String profiles) {
        return contextRunner.withPropertyValues(
                "spring.profiles.active=" + profiles,
                "SPRING_PROFILES_ACTIVE=" + profiles
        );
    }

    private String failureText(Throwable failure) {
        StringBuilder text = new StringBuilder();
        while (failure != null) {
            text.append(failure).append('\n');
            failure = failure.getCause();
        }
        return text.toString();
    }
}
