package utn.back.mordiscoapi.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.event.auth.PasswordChangedEvent;
import utn.back.mordiscoapi.event.auth.PasswordResetRequestedEvent;
import utn.back.mordiscoapi.model.dto.auth.RecoverPasswordDTO;
import utn.back.mordiscoapi.model.dto.auth.ResetPasswordDTO;
import utn.back.mordiscoapi.security.jwt.model.entity.RefreshToken;
import utn.back.mordiscoapi.model.entity.Rol;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.repository.PasswordRecoveryCredentialRepository;
import utn.back.mordiscoapi.repository.RolRepository;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.security.jwt.repository.RefreshTokenRepository;
import utn.back.mordiscoapi.security.recovery.PasswordRecoveryTokenCodec;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.profiles.active=test",
        "server.port=0",
        "app.frontend-url=https://frontend.test",
        "app.jwt.secret=test-secret-test-secret-test-secret-test-secret",
        "app.websocket-allowed-origins=https://frontend.test",
        "spring.mail.host=localhost",
        "spring.mail.port=2525",
        "spring.mail.username=test",
        "spring.mail.password=test",
        "app.mercadopago.access-token=test",
        "app.mercadopago.public-key=test",
        "app.mercadopago.notification-url=https://payments.test/webhook"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
@Import(PasswordRecoveryMySqlConcurrencyIntegrationTest.EventCaptureConfiguration.class)
class PasswordRecoveryMySqlConcurrencyIntegrationTest {
    private static final String GENERIC_ERROR = "El token es inválido o ha expirado";
    private static final String ORIGINAL_PASSWORD = "Original1!";
    private static final String RESET_PASSWORD_ONE = "Concurrent1!";
    private static final String RESET_PASSWORD_TWO = "Concurrent2!";

    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0");

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
    }

    @Autowired private PasswordRecoveryService passwordRecoveryService;
    @Autowired private PasswordRecoveryCredentialRepository credentialRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RolRepository rolRepository;
    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private PasswordRecoveryTokenCodec tokenCodec;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private EventCapture eventCapture;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.update("delete from password_recovery_credentials");
        jdbcTemplate.update("delete from refresh_tokens");
        jdbcTemplate.update("delete from usuarios");
        jdbcTemplate.update("delete from roles");
        eventCapture.clear();
    }

    @AfterEach
    void clearEvents() {
        eventCapture.clear();
    }

    @Test
    void concurrentResetsUseSeparateInnoDbTransactionsAndCommitExactlyOneSecurityMutation() throws Exception {
        Usuario affected = createUser("affected@example.test");
        Usuario unaffected = createUser("unaffected@example.test");
        createRefreshToken(affected, "affected-a");
        createRefreshToken(affected, "affected-b");
        createRefreshToken(unaffected, "unaffected-a");
        String token = issueToken(affected);
        eventCapture.clear();

        List<ResetResult> results = runConcurrently(
                () -> reset(token, RESET_PASSWORD_ONE),
                () -> reset(token, RESET_PASSWORD_TWO));

        assertEquals(1, results.stream().filter(ResetResult::success).count());
        assertEquals(1, results.stream().filter(result -> !result.success()).count());
        results.stream().filter(result -> !result.success()).forEach(result -> assertEquals(GENERIC_ERROR, result.message()));
        eventCapture.awaitPasswordChanged(1);

        assertNotNull(consumedAt(affected.getId()));
        String storedPassword = jdbcTemplate.queryForObject("select password from usuarios where id = ?", String.class, affected.getId());
        assertTrue(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().matches(RESET_PASSWORD_ONE, storedPassword)
                || new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().matches(RESET_PASSWORD_TWO, storedPassword));
        assertEquals(0, activeSessionCount(affected.getId()));
        assertEquals(1, activeSessionCount(unaffected.getId()));
        assertEquals(1, eventCapture.passwordChanged().size());
        assertEquals(affected.getId(), eventCapture.passwordChanged().peek().getUserId());
        assertEquals(ORIGINAL_PASSWORD, jdbcTemplate.queryForObject("select password from usuarios where id = ?", String.class, unaffected.getId()));
    }

    @Test
    void concurrentIssuanceLeavesOneCurrentDigestAndOnlyItsCapturedRawTokenCanReset() throws Exception {
        Usuario user = createUser("issuance@example.test");
        String supersededToken = issueToken(user);
        expireCooldown(user.getId());
        eventCapture.clear();

        runConcurrently(
                () -> { passwordRecoveryService.requestPasswordRecovery(new RecoverPasswordDTO(user.getEmail())); return ResetResult.succeeded(); },
                () -> { passwordRecoveryService.requestPasswordRecovery(new RecoverPasswordDTO(user.getEmail())); return ResetResult.succeeded(); });

        eventCapture.awaitPasswordReset(1);
        assertEquals(1, credentialCount(user.getId()));
        assertEquals(1, eventCapture.passwordResets().size());
        String currentToken = tokenFrom(eventCapture.passwordResets().peek().getResetLink());
        assertEquals(tokenCodec.digest(currentToken), currentDigest(user.getId()));
        assertNotEquals(tokenCodec.digest(supersededToken), currentDigest(user.getId()));

        assertGenericFailure(supersededToken);
        passwordRecoveryService.resetPassword(new ResetPasswordDTO(currentToken, RESET_PASSWORD_ONE));
        eventCapture.awaitPasswordChanged(1);
        assertNotNull(consumedAt(user.getId()));
    }

    @Test
    void issuanceAndResetRaceSerializeWithoutDeadlockOrMixedCredentialState() throws Exception {
        Usuario user = createUser("race@example.test");
        createRefreshToken(user, "race-session");
        String oldToken = issueToken(user);
        expireCooldown(user.getId());
        eventCapture.clear();

        List<ResetResult> results = runConcurrently(
                () -> reset(oldToken, RESET_PASSWORD_ONE),
                () -> { passwordRecoveryService.requestPasswordRecovery(new RecoverPasswordDTO(user.getEmail())); return ResetResult.succeeded(); });

        assertEquals(1, credentialCount(user.getId()));
        eventCapture.awaitPasswordReset(1);
        String issuedToken = tokenFrom(eventCapture.passwordResets().peek().getResetLink());
        assertEquals(tokenCodec.digest(issuedToken), currentDigest(user.getId()));
        assertNotEquals(tokenCodec.digest(oldToken), currentDigest(user.getId()));
        assertNull(consumedAt(user.getId()));
        assertEquals(1, eventCapture.passwordResets().size());
        ResetResult resetResult = results.getFirst();
        assertTrue(resetResult.success() || GENERIC_ERROR.equals(resetResult.message()));
        assertEquals(resetResult.success() ? 0 : 1, activeSessionCount(user.getId()));
        assertEquals(resetResult.success() ? 1 : 0, eventCapture.passwordChanged().size());
    }

    private Usuario createUser(String email) {
        Rol role = rolRepository.save(Rol.builder().nombre("ROLE_CLIENTE").build());
        return usuarioRepository.save(Usuario.builder()
                .nombre("Test").apellido("User").telefono("+549110" + Math.abs(email.hashCode()))
                .email(email).password(ORIGINAL_PASSWORD).bajaLogica(false).rol(role).build());
    }

    private void createRefreshToken(Usuario user, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuario(user);
        refreshToken.setToken(token);
        refreshToken.setCreatedAt(LocalDateTime.now().minusMinutes(1));
        refreshToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        refreshToken.setUserAgent("test");
        refreshToken.setIpAddress("127.0.0.1");
        refreshTokenRepository.save(refreshToken);
    }

    private String issueToken(Usuario user) throws InterruptedException {
        passwordRecoveryService.requestPasswordRecovery(new RecoverPasswordDTO(user.getEmail()));
        eventCapture.awaitPasswordReset(1);
        String token = tokenFrom(eventCapture.passwordResets().peek().getResetLink());
        eventCapture.clear();
        return token;
    }

    private List<ResetResult> runConcurrently(ThrowingSupplier<ResetResult> first, ThrowingSupplier<ResetResult> second) throws Exception {
        CyclicBarrier startBarrier = new CyclicBarrier(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<ResetResult> firstFuture = executor.submit(() -> runAfterBarrier(startBarrier, first));
            Future<ResetResult> secondFuture = executor.submit(() -> runAfterBarrier(startBarrier, second));
            return List.of(firstFuture.get(15, TimeUnit.SECONDS), secondFuture.get(15, TimeUnit.SECONDS));
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }
    }

    private ResetResult runAfterBarrier(CyclicBarrier startBarrier, ThrowingSupplier<ResetResult> action) throws Exception {
        startBarrier.await(10, TimeUnit.SECONDS);
        return action.get();
    }

    private ResetResult reset(String token, String password) {
        try {
            passwordRecoveryService.resetPassword(new ResetPasswordDTO(token, password));
            return ResetResult.succeeded();
        } catch (BadRequestException exception) {
            return ResetResult.failure(exception.getMessage());
        }
    }

    private void assertGenericFailure(String token) {
        ResetResult result = reset(token, RESET_PASSWORD_TWO);
        assertFalse(result.success());
        assertEquals(GENERIC_ERROR, result.message());
    }

    private void expireCooldown(Long userId) {
        jdbcTemplate.update("update password_recovery_credentials set cooldown_until = timestampadd(second, -1, utc_timestamp(6)) where usuario_id = ?", userId);
    }

    private int credentialCount(Long userId) {
        return jdbcTemplate.queryForObject("select count(*) from password_recovery_credentials where usuario_id = ?", Integer.class, userId);
    }

    private String currentDigest(Long userId) {
        return jdbcTemplate.queryForObject("select token_digest from password_recovery_credentials where usuario_id = ?", String.class, userId);
    }

    private LocalDateTime consumedAt(Long userId) {
        return jdbcTemplate.queryForObject("select consumed_at from password_recovery_credentials where usuario_id = ?", LocalDateTime.class, userId);
    }

    private int activeSessionCount(Long userId) {
        return jdbcTemplate.queryForObject("select count(*) from refresh_tokens where usuario_id = ? and revoked_at is null", Integer.class, userId);
    }

    private String tokenFrom(String resetLink) {
        return resetLink.substring(resetLink.indexOf("token=") + "token=".length());
    }

    record ResetResult(boolean success, String message) {
        static ResetResult succeeded() { return new ResetResult(true, null); }
        static ResetResult failure(String message) { return new ResetResult(false, message); }
    }

    @FunctionalInterface
    interface ThrowingSupplier<T> { T get() throws Exception; }

    @TestConfiguration
    static class EventCaptureConfiguration {
        @Bean EventCapture eventCapture() { return new EventCapture(); }
    }

    static class EventCapture {
        private final ConcurrentLinkedQueue<PasswordChangedEvent> passwordChanged = new ConcurrentLinkedQueue<>();
        private final ConcurrentLinkedQueue<PasswordResetRequestedEvent> passwordResets = new ConcurrentLinkedQueue<>();
        private final Semaphore changedEvents = new Semaphore(0);
        private final Semaphore resetEvents = new Semaphore(0);

        @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
        public void capture(PasswordChangedEvent event) { passwordChanged.add(event); changedEvents.release(); }
        @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
        public void capture(PasswordResetRequestedEvent event) { passwordResets.add(event); resetEvents.release(); }
        void awaitPasswordChanged(int count) throws InterruptedException { await(changedEvents, count, "password changed"); }
        void awaitPasswordReset(int count) throws InterruptedException { await(resetEvents, count, "password reset"); }
        ConcurrentLinkedQueue<PasswordChangedEvent> passwordChanged() { return passwordChanged; }
        ConcurrentLinkedQueue<PasswordResetRequestedEvent> passwordResets() { return passwordResets; }
        void clear() { passwordChanged.clear(); passwordResets.clear(); changedEvents.drainPermits(); resetEvents.drainPermits(); }
        private void await(Semaphore semaphore, int count, String event) throws InterruptedException {
            if (!semaphore.tryAcquire(count, 15, TimeUnit.SECONDS)) fail("Timed out waiting for " + event + " event");
        }
    }
}
