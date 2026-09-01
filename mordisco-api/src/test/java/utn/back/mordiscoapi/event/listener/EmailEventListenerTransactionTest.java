package utn.back.mordiscoapi.event.listener;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.slf4j.LoggerFactory;
import utn.back.mordiscoapi.common.email.EmailSender;
import utn.back.mordiscoapi.common.exception.InternalServerErrorException;
import utn.back.mordiscoapi.config.AppProperties;
import utn.back.mordiscoapi.event.auth.PasswordChangedEvent;
import utn.back.mordiscoapi.event.auth.PasswordResetRequestedEvent;
import utn.back.mordiscoapi.service.interf.IEmailService;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class EmailEventListenerTransactionTest {
    private static final PasswordResetRequestedEvent RESET_EVENT =
            new PasswordResetRequestedEvent(17L, "secret@example.com", "Secret", "https://frontend/reset-password?token=secret-token");
    private static final PasswordChangedEvent CHANGED_EVENT =
            new PasswordChangedEvent(17L, "secret@example.com", "Secret", "https://frontend/login?token=secret-token");

    @Test
    void recoveryAndPasswordChangedMailAreDeliveredOnlyOnceAfterCommitAndNeverAfterRollback() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(EventConfiguration.class)) {
            ResettableEmailService emailService = context.getBean(ResettableEmailService.class);
            TransactionTemplate transaction = new TransactionTemplate(context.getBean(PlatformTransactionManager.class));
            ApplicationEventPublisher publisher = context;

            transaction.executeWithoutResult(status -> {
                publisher.publishEvent(RESET_EVENT);
                publisher.publishEvent(CHANGED_EVENT);
                emailService.assertNoCalls();
            });

            assertEquals(1, emailService.passwordResetCalls);
            assertEquals(1, emailService.passwordChangedCalls);

            emailService.reset();
            transaction.executeWithoutResult(status -> {
                publisher.publishEvent(RESET_EVENT);
                publisher.publishEvent(CHANGED_EVENT);
                status.setRollbackOnly();
            });
            emailService.assertNoCalls();
        }
    }

    @Test
    void recoveryMailFailuresAreContainedInFixedNonSensitiveLogs() throws Exception {
        IEmailService emailService = mock(IEmailService.class);
        doThrow(new InternalServerErrorException("secret@example.com https://frontend/reset-password?token=secret-token Password1! digest=dddd userId=17", null))
                .when(emailService).sendPasswordResetEmail(anyString(), anyString(), anyString());
        doThrow(new InternalServerErrorException("secret@example.com https://frontend/login?token=secret-token Password1! digest=dddd userId=17", null))
                .when(emailService).sendPasswordChangeAlertEmail(anyString(), anyString(), anyString());
        EmailEventListener listener = new EmailEventListener(emailService, new AppProperties());
        Logger logger = (Logger) LoggerFactory.getLogger(EmailEventListener.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            listener.handlePasswordResetRequested(RESET_EVENT);
            listener.handlePasswordChanged(CHANGED_EVENT);
        } finally {
            logger.detachAppender(appender);
        }

        List<ILoggingEvent> logs = appender.list;
        assertEquals(2, logs.size());
        for (ILoggingEvent event : logs) {
            assertEquals("Password recovery email delivery failed", event.getFormattedMessage());
            assertNull(event.getThrowableProxy());
            assertFalse(event.getFormattedMessage().contains("secret@example.com"));
            assertFalse(event.getFormattedMessage().contains("secret-token"));
            assertFalse(event.getFormattedMessage().contains("https://frontend"));
            assertFalse(event.getFormattedMessage().contains("Password1!"));
            assertFalse(event.getFormattedMessage().contains("digest=dddd"));
            assertFalse(event.getFormattedMessage().contains("userId=17"));
        }
    }

    @Test
    void recoveryHandlersUseAfterCommitAsyncDeliveryAndEmailSenderIsSynchronous() throws Exception {
        for (String name : List.of("handlePasswordResetRequested", "handlePasswordChanged")) {
            Method method = EmailEventListener.class.getDeclaredMethod(name,
                    name.equals("handlePasswordResetRequested") ? PasswordResetRequestedEvent.class : PasswordChangedEvent.class);
            TransactionalEventListener transactional = method.getAnnotation(TransactionalEventListener.class);
            assertNotNull(transactional);
            assertEquals(TransactionPhase.AFTER_COMMIT, transactional.phase());
            assertNotNull(method.getAnnotation(Async.class));
        }
        assertNull(EmailSender.class.getDeclaredMethod("sendHtmlEmail", String.class, String.class, String.class)
                .getAnnotation(Async.class));
    }

    @Configuration
    @EnableAsync
    @EnableTransactionManagement
    static class EventConfiguration {
        @Bean
        IEmailService emailService() {
            return new ResettableEmailService();
        }

        @Bean
        AppProperties appProperties() {
            return new AppProperties();
        }

        @Bean
        EmailEventListener emailEventListener(IEmailService emailService, AppProperties appProperties) {
            return new EmailEventListener(emailService, appProperties);
        }

        @Bean("passwordRecoveryEmailExecutor")
        Executor taskExecutor() {
            return new SyncTaskExecutor();
        }

        @Bean
        PlatformTransactionManager transactionManager() {
            return new TestTransactionManager();
        }
    }

    static class ResettableEmailService implements IEmailService {
        private int passwordResetCalls;
        private int passwordChangedCalls;

        void reset() {
            passwordResetCalls = 0;
            passwordChangedCalls = 0;
        }

        void assertNoCalls() {
            assertEquals(0, passwordResetCalls);
            assertEquals(0, passwordChangedCalls);
        }

        @Override
        public void sendPasswordResetEmail(String to, String name, String resetLink) {
            passwordResetCalls++;
        }

        @Override
        public void sendPasswordChangeAlertEmail(String to, String name, String loginLink) {
            passwordChangedCalls++;
        }

        @Override public void sendNuevoPedidoEmail(String to, String nombre, Long pedidoId, String restaurante, String link) { }
        @Override public void sendPedidoEnPreparacionEmail(String to, String nombre, Long pedidoId, String link) { }
        @Override public void sendPedidoListoParaRetirarEmail(String to, String nombre, Long pedidoId, String link) { }
        @Override public void sendPedidoEnCaminoEmail(String to, String nombre, Long pedidoId, String link) { }
        @Override public void sendPedidoCanceladoEmailCliente(String to, String nombre, Long pedidoId, String motivo, String link) { }
        @Override public void sendPedidoCanceladoEmailRestaurante(String to, String nombreRestaurante, Long pedidoId, String link) { }
        @Override public void sendPagoConfirmadoEmailCliente(String to, String nombre, Long pedidoId, String link) { }
        @Override public void sendPagoConfirmadoEmailRestaurante(String to, String nombreRestaurante, Long pedidoId, String link) { }
        @Override public void sendPagoRechazadoEmailCliente(String to, String nombre, Long pedidoId, String motivo, String link) { }
        @Override public void sendPagoRechazadoEmailRestaurante(String to, String nombre, Long pedidoId, String link) { }
    }

    static class TestTransactionManager extends AbstractPlatformTransactionManager {
        @Override
        protected Object doGetTransaction() {
            return new Object();
        }

        @Override
        protected void doBegin(Object transaction, org.springframework.transaction.TransactionDefinition definition) { }

        @Override
        protected void doCommit(DefaultTransactionStatus status) { }

        @Override
        protected void doRollback(DefaultTransactionStatus status) { }
    }
}
