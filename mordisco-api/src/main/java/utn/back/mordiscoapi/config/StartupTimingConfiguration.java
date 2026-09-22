package utn.back.mordiscoapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.LongSupplier;

@Configuration(proxyBeanMethods = false)
public class StartupTimingConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupTimingConfiguration.class);

    @Bean
    static EntityManagerFactoryStartupTimingBeanPostProcessor entityManagerFactoryStartupTimingBeanPostProcessor() {
        return new EntityManagerFactoryStartupTimingBeanPostProcessor(System::nanoTime);
    }

    public static final class ApplicationStartupTimingListener implements ApplicationListener<SpringApplicationEvent> {
        private final LongSupplier nanoTime;
        private final AtomicReference<Long> startedAtNanos = new AtomicReference<>();

        public ApplicationStartupTimingListener() {
            this(System::nanoTime);
        }

        ApplicationStartupTimingListener(LongSupplier nanoTime) {
            this.nanoTime = nanoTime;
        }

        @Override
        public void onApplicationEvent(SpringApplicationEvent event) {
            if (event instanceof ApplicationStartingEvent) {
                onStarting();
            } else if (event instanceof ApplicationReadyEvent) {
                onReady();
            }
        }

        void onStarting() {
            startedAtNanos.set(nanoTime.getAsLong());
        }

        void onReady() {
            Long startedAt = startedAtNanos.getAndSet(null);
            if (startedAt != null) {
                long elapsedNanos = nanoTime.getAsLong() - startedAt;
                LOGGER.info(
                        "Startup timing: application ready; totalElapsedMs={} boundary=ApplicationStartingEvent->ApplicationReadyEvent",
                        TimeUnit.NANOSECONDS.toMillis(elapsedNanos));
            }
        }
    }

    static final class EntityManagerFactoryStartupTimingBeanPostProcessor implements BeanPostProcessor {
        private static final String ENTITY_MANAGER_FACTORY_BEAN_NAME = "entityManagerFactory";

        private final LongSupplier nanoTime;
        private final AtomicReference<Long> startedAtNanos = new AtomicReference<>();

        EntityManagerFactoryStartupTimingBeanPostProcessor(LongSupplier nanoTime) {
            this.nanoTime = nanoTime;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) {
            if (isEntityManagerFactory(bean, beanName)
                    && startedAtNanos.compareAndSet(null, nanoTime.getAsLong())) {
                LOGGER.info("Startup timing: entityManagerFactory initialization started; boundary=BeanPostProcessor.beforeInitialization");
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (isEntityManagerFactory(bean, beanName)) {
                Long startedAt = startedAtNanos.getAndSet(null);
                if (startedAt != null) {
                    long elapsedNanos = nanoTime.getAsLong() - startedAt;
                    LOGGER.info(
                            "Startup timing: entityManagerFactory initialization complete; elapsedMs={} boundary=beforeInitialization->afterInitialization",
                            TimeUnit.NANOSECONDS.toMillis(elapsedNanos));
                }
            }
            return bean;
        }

        private boolean isEntityManagerFactory(Object bean, String beanName) {
            return ENTITY_MANAGER_FACTORY_BEAN_NAME.equals(beanName) && bean instanceof FactoryBean<?>;
        }
    }
}
