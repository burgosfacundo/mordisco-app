package utn.back.mordiscoapi.config;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.LongSupplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StartupTimingConfigurationTest {

    @Test
    void logsJpaInitializationBeforeApplicationReadyWithoutSensitiveValues() throws Exception {
        Logger logger = (Logger) LoggerFactory.getLogger(StartupTimingConfiguration.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            StartupTimingConfiguration.EntityManagerFactoryStartupTimingBeanPostProcessor processor =
                    new StartupTimingConfiguration.EntityManagerFactoryStartupTimingBeanPostProcessor(
                            tickSequence(100_000_000L, 165_000_000L));
            SyntheticEntityManagerFactoryBean factory = new SyntheticEntityManagerFactoryBean();
            processor.postProcessBeforeInitialization(factory, "entityManagerFactory");
            factory.afterPropertiesSet();
            processor.postProcessAfterInitialization(factory, "entityManagerFactory");

            StartupTimingConfiguration.ApplicationStartupTimingListener listener =
                    new StartupTimingConfiguration.ApplicationStartupTimingListener(
                            tickSequence(200_000_000L, 500_000_000L));
            listener.onStarting();
            listener.onReady();

            List<String> messages = appender.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
            assertEquals(3, messages.size());
            assertTrue(messages.get(0).contains("entityManagerFactory initialization started"));
            assertTrue(messages.get(0).contains("boundary=BeanPostProcessor.beforeInitialization"));
            assertTrue(factory.initialized);
            assertTrue(messages.get(1).contains("entityManagerFactory initialization complete; elapsedMs=65"));
            assertTrue(messages.get(1).contains("boundary=beforeInitialization->afterInitialization"));
            assertTrue(messages.get(2).contains("application ready; totalElapsedMs=300"));
            assertTrue(messages.get(2).contains("boundary=ApplicationStartingEvent->ApplicationReadyEvent"));

            String output = String.join("\n", messages);
            assertTrue(output.indexOf("entityManagerFactory initialization started")
                    < output.indexOf("entityManagerFactory initialization complete"));
            assertTrue(output.indexOf("entityManagerFactory initialization complete")
                    < output.indexOf("application ready"));
            assertFalse(output.contains("jdbc:mysql://sensitive-host"));
            assertFalse(output.contains("jdbc-user-sentinel"));
            assertFalse(output.contains("jdbc-password-sentinel"));
            assertFalse(output.contains("app-secret-sentinel"));
            assertFalse(output.contains("env-value-sentinel"));
        } finally {
            logger.detachAppender(appender);
        }
    }

    @Test
    void registersTimingForTheProductionProfileWithoutOpeningPersistenceConnections() {
        new ApplicationContextRunner()
                .withInitializer(context -> context.getEnvironment().setActiveProfiles("prod"))
                .withUserConfiguration(StartupTimingConfiguration.class, SyntheticPersistenceConfiguration.class)
                .run(context -> {
                    assertNull(context.getStartupFailure(), () -> failureText(context.getStartupFailure()));
                    assertTrue(context.containsBean("entityManagerFactoryStartupTimingBeanPostProcessor"));
                    SyntheticEntityManagerFactoryBean factory =
                            (SyntheticEntityManagerFactoryBean) context.getBean("&entityManagerFactory");
                    assertTrue(factory.initialized);
                });
    }

    @Test
    void preservesEntityManagerFactoryInitializationFailures() {
        Logger logger = (Logger) LoggerFactory.getLogger(StartupTimingConfiguration.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            new ApplicationContextRunner()
                    .withInitializer(context -> context.getEnvironment().setActiveProfiles("prod"))
                    .withUserConfiguration(StartupTimingConfiguration.class, FailingPersistenceConfiguration.class)
                    .run(context -> {
                        Throwable failure = context.getStartupFailure();
                        assertNotNull(failure);
                        assertTrue(failureText(failure).contains("synthetic-emf-initialization-failure"));
                    });

            List<String> messages = appender.list.stream()
                    .map(ILoggingEvent::getFormattedMessage)
                    .filter(message -> message.startsWith("Startup timing:"))
                    .toList();
            assertEquals(1, messages.size());
            assertTrue(messages.get(0).contains("entityManagerFactory initialization started"));
            assertFalse(messages.get(0).contains("initialization complete"));
        } finally {
            logger.detachAppender(appender);
        }
    }

    private LongSupplier tickSequence(long... ticks) {
        AtomicInteger index = new AtomicInteger();
        return () -> ticks[index.getAndIncrement()];
    }

    private String failureText(Throwable failure) {
        StringBuilder text = new StringBuilder();
        while (failure != null) {
            text.append(failure).append('\n');
            failure = failure.getCause();
        }
        return text.toString();
    }

    @Configuration(proxyBeanMethods = false)
    static class SyntheticPersistenceConfiguration {
        @Bean("entityManagerFactory")
        SyntheticEntityManagerFactoryBean entityManagerFactory() {
            return new SyntheticEntityManagerFactoryBean();
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class FailingPersistenceConfiguration {
        @Bean("entityManagerFactory")
        FailingEntityManagerFactoryBean entityManagerFactory() {
            return new FailingEntityManagerFactoryBean();
        }
    }

    static class SyntheticEntityManagerFactoryBean implements FactoryBean<Object>, InitializingBean {
        private boolean initialized;

        @Override
        public void afterPropertiesSet() {
            initialized = true;
        }

        @Override
        public Object getObject() {
            return new Object();
        }

        @Override
        public Class<?> getObjectType() {
            return Object.class;
        }

        @Override
        public String toString() {
            return "jdbc:mysql://sensitive-host/catalog?user=jdbc-user-sentinel&password=jdbc-password-sentinel "
                    + "secret=app-secret-sentinel env=env-value-sentinel";
        }
    }

    static class FailingEntityManagerFactoryBean extends SyntheticEntityManagerFactoryBean {
        @Override
        public void afterPropertiesSet() {
            throw new IllegalStateException("synthetic-emf-initialization-failure");
        }
    }
}
