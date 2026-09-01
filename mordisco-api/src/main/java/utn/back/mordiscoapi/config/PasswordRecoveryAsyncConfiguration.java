package utn.back.mordiscoapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class PasswordRecoveryAsyncConfiguration {
    @Bean("passwordRecoveryEmailExecutor")
    public AsyncTaskExecutor passwordRecoveryEmailExecutor() {
        ThreadPoolTaskExecutor delegate = new ThreadPoolTaskExecutor();
        delegate.setCorePoolSize(1);
        delegate.setMaxPoolSize(1);
        delegate.setQueueCapacity(100);
        delegate.setThreadNamePrefix("password-recovery-email-");
        delegate.initialize();
        return new RecoveryEmailTaskExecutor(delegate);
    }
}
