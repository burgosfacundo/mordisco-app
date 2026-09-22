package utn.back.mordiscoapi.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import utn.back.mordiscoapi.MordiscoApiApplication;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchedulingConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(SchedulingConfiguration.class);

    @Test
    void applicationDoesNotEnableSchedulingGlobally() {
        assertFalse(MordiscoApiApplication.class.isAnnotationPresent(EnableScheduling.class));
    }

    @Test
    void schedulingConfigurationIsEnabledOnlyOutsideProduction() {
        Profile profile = SchedulingConfiguration.class.getAnnotation(Profile.class);
        assertNotNull(profile);
        assertTrue(java.util.Arrays.asList(profile.value()).contains("!prod"));
        assertNotNull(SchedulingConfiguration.class.getAnnotation(EnableScheduling.class));
    }

    @Test
    void productionContextDoesNotCreateScheduledTaskProcessor() {
        contextRunner
                .withInitializer(context -> context.getEnvironment().setActiveProfiles("prod"))
                .run(context -> assertTrue(
                        context.getBeansOfType(ScheduledAnnotationBeanPostProcessor.class).isEmpty()));
    }

    @Test
    void nonProductionContextCreatesScheduledTaskProcessor() {
        contextRunner
                .withInitializer(context -> context.getEnvironment().setActiveProfiles("dev"))
                .run(context -> assertFalse(
                        context.getBeansOfType(ScheduledAnnotationBeanPostProcessor.class).isEmpty()));
    }
}
