package utn.back.mordiscoapi.config;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RecoveryEmailTaskExecutorTest {
    @Test
    void destroyDelegatesToThreadPoolExecutorExactlyOnce() throws Exception {
        TrackingThreadPoolTaskExecutor delegate = new TrackingThreadPoolTaskExecutor();
        RecoveryEmailTaskExecutor executor = new RecoveryEmailTaskExecutor(delegate);

        executor.destroy();

        assertEquals(1, delegate.destroyCalls);
    }

    @Test
    void rejectedRecoveryEmailSubmissionIsPrivateAndBestEffort() throws Exception {
        AsyncTaskExecutor delegate = mock(AsyncTaskExecutor.class);
        when(delegate.submit(any(Runnable.class))).thenThrow(
                new TaskRejectedException("secret@example.com reset-token Password1! digest=dddd userId=17"));
        RecoveryEmailTaskExecutor executor = new RecoveryEmailTaskExecutor(delegate);
        Logger logger = (Logger) LoggerFactory.getLogger(RecoveryEmailTaskExecutor.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            Future<?> result = assertDoesNotThrow(() -> executor.submit(() -> { }));
            assertNull(result.get());
        } finally {
            logger.detachAppender(appender);
        }

        assertEquals(1, appender.list.size());
        ILoggingEvent event = appender.list.getFirst();
        assertEquals("Password recovery email dispatch unavailable", event.getFormattedMessage());
        assertNull(event.getThrowableProxy());
    }

    private static final class TrackingThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
        private int destroyCalls;

        @Override
        public void destroy() {
            destroyCalls++;
        }
    }
}
