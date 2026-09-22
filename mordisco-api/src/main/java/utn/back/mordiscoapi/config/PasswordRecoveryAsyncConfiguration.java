package utn.back.mordiscoapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class PasswordRecoveryAsyncConfiguration {
    /**
     * Retains the bean name for compatibility while ensuring a future qualified
     * listener cannot leave security mail queued after a Vercel response.
     */
    @Bean("passwordRecoveryEmailExecutor")
    public TaskExecutor passwordRecoveryEmailExecutor() {
        return new SyncTaskExecutor();
    }
}
