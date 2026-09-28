package utn.back.mordiscoapi.security.jwt.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import utn.back.mordiscoapi.model.entity.Rol;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.repository.RolRepository;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.security.jwt.model.entity.RefreshToken;
import utn.back.mordiscoapi.security.jwt.repository.RefreshTokenRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.jwt.refresh.expiration=600000",
        "app.jwt.max-sessions=5"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
@Import(RefreshTokenService.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class RefreshTokenRotationMySqlConcurrencyTest {
    private static final String RAW_TOKEN = "A".repeat(43);

    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0");

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
    }

    @Autowired private RefreshTokenService refreshTokenService;
    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RolRepository rolRepository;
    @Autowired private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clearDatabase() {
        jdbcTemplate.update("delete from refresh_tokens");
        jdbcTemplate.update("delete from usuarios");
        jdbcTemplate.update("delete from roles");
    }

    @Test
    void concurrentUseOfOneTokenSerializesAndCommitsReuseRevocation() throws Exception {
        Usuario user = createUser();
        persistRefreshToken(user, sha256(RAW_TOKEN));

        List<RotationOutcome> outcomes = rotateConcurrently();

        assertEquals(1, outcomes.stream().filter(RotationOutcome::succeeded).count());
        assertEquals(1, outcomes.stream().filter(outcome -> !outcome.succeeded()).count());
        assertEquals(0, activeSessionCount(user.getId()));

        List<String> storedCredentials = jdbcTemplate.queryForList(
                "select token from refresh_tokens where usuario_id = ?", String.class, user.getId());
        assertEquals(2, storedCredentials.size());
        assertTrue(storedCredentials.stream().allMatch(value -> value.matches("[0-9a-f]{64}")));
        RotationOutcome successfulRotation = outcomes.stream()
                .filter(RotationOutcome::succeeded)
                .findFirst()
                .orElseThrow();
        assertTrue(storedCredentials.contains(successfulRotation.successorDigest()));
        assertFalse(storedCredentials.contains(RAW_TOKEN));
    }

    @Test
    void concurrentLogoutAllAndRotationSerializeOnTheUserLock() throws Exception {
        Usuario user = createUser();
        persistRefreshToken(user, sha256(RAW_TOKEN));

        LogoutRotationOutcome outcome = revokeAllAndRotateConcurrently(user.getId());

        assertEquals(0, activeSessionCount(user.getId()));
        int storedSessionCount = jdbcTemplate.queryForObject(
                "select count(*) from refresh_tokens where usuario_id = ?",
                Integer.class, user.getId());
        assertEquals(outcome.rotationSucceeded() ? 2 : 1, storedSessionCount);
        assertEquals(storedSessionCount, jdbcTemplate.queryForObject(
                "select count(*) from refresh_tokens where usuario_id = ? and revoked_at is not null",
                Integer.class, user.getId()));
        if (outcome.rotationSucceeded()) {
            assertEquals(1, jdbcTemplate.queryForObject(
                    "select count(*) from refresh_tokens where usuario_id = ? and token = ? and revoked_at is not null",
                    Integer.class, user.getId(), outcome.successorDigest()));
        }
    }

    @Test
    void deterministicSecondUseCommitsRevocationOfThePersistedSuccessor() throws Exception {
        Usuario user = createUser();
        persistRefreshToken(user, sha256(RAW_TOKEN));

        RefreshTokenService.IssuedRefreshToken firstRotation = refreshTokenService.rotateRefreshToken(
                RAW_TOKEN, "first-agent", "192.0.2.1");
        String successorDigest = sha256(firstRotation.opaqueToken());
        assertEquals(1, jdbcTemplate.queryForObject(
                "select count(*) from refresh_tokens where usuario_id = ? and token = ? and revoked_at is null",
                Integer.class, user.getId(), successorDigest));

        assertThrows(
                RefreshTokenService.RefreshTokenReuseException.class,
                () -> refreshTokenService.rotateRefreshToken(RAW_TOKEN, "second-agent", "192.0.2.2"));

        assertEquals(0, activeSessionCount(user.getId()));
        assertEquals(1, jdbcTemplate.queryForObject(
                "select count(*) from refresh_tokens where usuario_id = ? and token = ? and revoked_at is not null",
                Integer.class, user.getId(), successorDigest));
    }

    private Usuario createUser() {
        Rol role = rolRepository.save(Rol.builder().nombre("ROLE_CLIENTE").build());
        return usuarioRepository.save(Usuario.builder()
                .nombre("Refresh")
                .apellido("Test")
                .telefono("+549110" + System.nanoTime())
                .email("refresh-" + System.nanoTime() + "@example.test")
                .password("not-used")
                .bajaLogica(false)
                .rol(role)
                .build());
    }

    private void persistRefreshToken(Usuario user, String digest) {
        RefreshToken token = new RefreshToken();
        token.setUsuario(user);
        token.setTokenDigest(digest);
        token.setCreatedAt(LocalDateTime.now().minusMinutes(1));
        token.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        token.setUserAgent("test-agent");
        token.setIpAddress("127.0.0.1");
        refreshTokenRepository.saveAndFlush(token);
    }

    private List<RotationOutcome> rotateConcurrently() throws Exception {
        CyclicBarrier startBarrier = new CyclicBarrier(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<RotationOutcome> first = executor.submit(() -> rotateAfterBarrier(startBarrier));
            Future<RotationOutcome> second = executor.submit(() -> rotateAfterBarrier(startBarrier));
            return List.of(first.get(20, TimeUnit.SECONDS), second.get(20, TimeUnit.SECONDS));
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }
    }

    private LogoutRotationOutcome revokeAllAndRotateConcurrently(Long userId) throws Exception {
        CyclicBarrier startBarrier = new CyclicBarrier(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<LogoutRotationOutcome> rotation = executor.submit(
                    () -> rotateAlongsideLogoutAfterBarrier(startBarrier));
            Future<?> logoutAll = executor.submit(() -> {
                startBarrier.await(10, TimeUnit.SECONDS);
                refreshTokenService.revokeAllUserSessions(userId);
                return null;
            });
            LogoutRotationOutcome outcome = rotation.get(20, TimeUnit.SECONDS);
            logoutAll.get(20, TimeUnit.SECONDS);
            return outcome;
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }
    }

    private LogoutRotationOutcome rotateAlongsideLogoutAfterBarrier(CyclicBarrier startBarrier) throws Exception {
        startBarrier.await(10, TimeUnit.SECONDS);
        try {
            RefreshTokenService.IssuedRefreshToken issued = refreshTokenService.rotateRefreshToken(
                    RAW_TOKEN, "rotation-agent", "192.0.2.2");
            return new LogoutRotationOutcome(true, sha256(issued.opaqueToken()));
        } catch (RefreshTokenService.RefreshTokenAuthenticationException exception) {
            return new LogoutRotationOutcome(false, null);
        }
    }

    private RotationOutcome rotateAfterBarrier(CyclicBarrier startBarrier) throws Exception {
        startBarrier.await(10, TimeUnit.SECONDS);
        try {
            RefreshTokenService.IssuedRefreshToken issued = refreshTokenService.rotateRefreshToken(
                    RAW_TOKEN, "new-agent", "192.0.2.1");
            return new RotationOutcome(true, sha256(issued.opaqueToken()));
        } catch (RefreshTokenService.RefreshTokenAuthenticationException exception) {
            return new RotationOutcome(false, null);
        }
    }

    private int activeSessionCount(Long userId) {
        return jdbcTemplate.queryForObject(
                "select count(*) from refresh_tokens where usuario_id = ? and revoked_at is null",
                Integer.class, userId);
    }

    private static String sha256(String token) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(token.getBytes(StandardCharsets.US_ASCII)));
    }

    private record RotationOutcome(boolean succeeded, String successorDigest) {
    }

    private record LogoutRotationOutcome(boolean rotationSucceeded, String successorDigest) {
    }
}
