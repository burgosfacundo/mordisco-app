package utn.back.mordiscoapi.security.jwt.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.security.jwt.model.entity.RefreshToken;
import utn.back.mordiscoapi.security.jwt.repository.RefreshTokenRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {
    private static final Long USER_ID = 17L;
    private static final String RAW_TOKEN = "A".repeat(43);

    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private UsuarioRepository userRepository;

    private RefreshTokenService service;

    @BeforeEach
    void setUp() {
        service = new RefreshTokenService(refreshTokenRepository, userRepository);
        ReflectionTestUtils.setField(service, "refreshTokenDuration", 120_000L);
        ReflectionTestUtils.setField(service, "maxActiveSessions", 5);
    }

    @Test
    void issuanceReturnsA256BitUrlSafeTokenAndPersistsOnlyItsSha256Digest() throws Exception {
        Usuario user = activeUser();
        when(refreshTokenRepository.countByUsuarioIdAndRevokedAtIsNullAndExpiryDateAfter(eq(USER_ID), any()))
                .thenReturn(0L);
        when(userRepository.findByIdForUpdate(USER_ID)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LocalDateTime beforeIssue = LocalDateTime.now();
        RefreshTokenService.IssuedRefreshToken issuance = service.issueRefreshToken(USER_ID, "test-agent", "127.0.0.1");
        RefreshToken issued = issuance.refreshToken();
        LocalDateTime afterIssue = LocalDateTime.now();

        String rawToken = issuance.opaqueToken();
        assertFalse(issuance.toString().contains(rawToken));
        assertTrue(rawToken.matches("[A-Za-z0-9_-]{43}"));
        assertEquals(32, Base64.getUrlDecoder().decode(rawToken).length);
        assertEquals(sha256(rawToken), issued.getTokenDigest());
        assertNotEquals(rawToken, issued.getTokenDigest());
        assertTrue(issued.getCreatedAt().isAfter(beforeIssue.minusSeconds(1)));
        assertTrue(issued.getCreatedAt().isBefore(afterIssue.plusSeconds(1)));
        assertEquals(issued.getCreatedAt().plusSeconds(120), issued.getExpiryDate());
        assertFalse(issued.toString().contains(rawToken));
        assertFalse(issued.toString().contains(issued.getTokenDigest()));

        ArgumentCaptor<RefreshToken> persisted = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(persisted.capture());
        assertEquals(issued.getTokenDigest(), persisted.getValue().getTokenDigest());
        var issueOrder = inOrder(userRepository, refreshTokenRepository);
        issueOrder.verify(userRepository).findByIdForUpdate(USER_ID);
        issueOrder.verify(refreshTokenRepository)
                .countByUsuarioIdAndRevokedAtIsNullAndExpiryDateAfter(eq(USER_ID), any());
        assertFalse(java.util.Arrays.stream(RefreshToken.class.getDeclaredFields())
                .anyMatch(field -> "rawToken".equals(field.getName())));
        assertFalse(java.util.Arrays.stream(RefreshToken.class.getMethods())
                .anyMatch(method -> "getRawToken".equals(method.getName())));
    }

    @Test
    void rotationLocksAndRevokesTheOldDigestThenIssuesASeparateRawToken() throws Exception {
        Usuario user = activeUser();
        RefreshToken oldToken = storedToken(user, sha256(RAW_TOKEN), LocalDateTime.now().plusMinutes(5));
        when(refreshTokenRepository.findUsuarioIdByTokenDigest(sha256(RAW_TOKEN))).thenReturn(Optional.of(USER_ID));
        when(userRepository.findByIdForUpdate(USER_ID)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.findByTokenDigestForUpdate(sha256(RAW_TOKEN))).thenReturn(Optional.of(oldToken));
        when(refreshTokenRepository.countByUsuarioIdAndRevokedAtIsNullAndExpiryDateAfter(eq(USER_ID), any()))
                .thenReturn(0L);
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshTokenService.IssuedRefreshToken issuance =
                service.rotateRefreshToken(RAW_TOKEN, "new-agent", "192.0.2.1");
        RefreshToken rotated = issuance.refreshToken();

        assertNotNull(oldToken.getRevokedAt());
        assertEquals(sha256(RAW_TOKEN), oldToken.getTokenDigest());
        assertTrue(issuance.opaqueToken().matches("[A-Za-z0-9_-]{43}"));
        assertNotEquals(RAW_TOKEN, issuance.opaqueToken());
        assertEquals(sha256(issuance.opaqueToken()), rotated.getTokenDigest());
        assertEquals(user, rotated.getUsuario());
        var rotationLockOrder = inOrder(refreshTokenRepository, userRepository);
        rotationLockOrder.verify(refreshTokenRepository).findUsuarioIdByTokenDigest(sha256(RAW_TOKEN));
        rotationLockOrder.verify(userRepository).findByIdForUpdate(USER_ID);
        rotationLockOrder.verify(refreshTokenRepository).findByTokenDigestForUpdate(sha256(RAW_TOKEN));
    }

    @Test
    void revokeAllUserSessionsLocksTheUserBeforeBulkRevocation() {
        LocalDateTime now = LocalDateTime.now();

        service.revokeAllUserSessions(USER_ID, now);

        var revocationLockOrder = inOrder(userRepository, refreshTokenRepository);
        revocationLockOrder.verify(userRepository).findByIdForUpdate(USER_ID);
        revocationLockOrder.verify(refreshTokenRepository).revokeAllUserTokens(USER_ID, now);
    }

    @Test
    void malformedAndUnknownTokensDoNotTouchSessionsOrUsers() {
        assertThrows(RefreshTokenService.RefreshTokenAuthenticationException.class,
                () -> service.rotateRefreshToken("malformed", "agent", "ip"));
        verifyNoInteractions(refreshTokenRepository, userRepository);

        when(refreshTokenRepository.findUsuarioIdByTokenDigest(sha256(RAW_TOKEN))).thenReturn(Optional.empty());
        assertThrows(RefreshTokenService.RefreshTokenAuthenticationException.class,
                () -> service.rotateRefreshToken(RAW_TOKEN, "agent", "ip"));
        verify(refreshTokenRepository).findUsuarioIdByTokenDigest(sha256(RAW_TOKEN));
        verify(refreshTokenRepository, never()).findByTokenDigestForUpdate(any());
        verify(refreshTokenRepository, never()).revokeAllUserTokens(any(), any());
        verify(refreshTokenRepository, never()).save(any());
        verifyNoInteractions(userRepository);
    }

    @Test
    void revokedTokenReuseRevokesEverySessionAndIsConfiguredToCommitTheRejection() {
        Usuario user = activeUser();
        RefreshToken revoked = storedToken(user, sha256(RAW_TOKEN), LocalDateTime.now().plusMinutes(5));
        revoked.setRevokedAt(LocalDateTime.now().minusSeconds(1));
        when(refreshTokenRepository.findUsuarioIdByTokenDigest(sha256(RAW_TOKEN))).thenReturn(Optional.of(USER_ID));
        when(userRepository.findByIdForUpdate(USER_ID)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.findByTokenDigestForUpdate(sha256(RAW_TOKEN))).thenReturn(Optional.of(revoked));

        assertThrows(RefreshTokenService.RefreshTokenReuseException.class,
                () -> service.rotateRefreshToken(RAW_TOKEN, "agent", "ip"));

        verify(refreshTokenRepository).revokeAllUserTokens(eq(USER_ID), any(LocalDateTime.class));
        verify(refreshTokenRepository, never()).save(any());
        assertNoRollbackFor(RefreshTokenService.RefreshTokenReuseException.class);
    }

    @Test
    void expiredTokenFailsWithoutRevokingAnySessions() {
        Usuario user = activeUser();
        RefreshToken expired = storedToken(user, sha256(RAW_TOKEN), LocalDateTime.now().minusSeconds(1));
        when(refreshTokenRepository.findUsuarioIdByTokenDigest(sha256(RAW_TOKEN))).thenReturn(Optional.of(USER_ID));
        when(userRepository.findByIdForUpdate(USER_ID)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.findByTokenDigestForUpdate(sha256(RAW_TOKEN))).thenReturn(Optional.of(expired));

        assertThrows(RefreshTokenService.RefreshTokenAuthenticationException.class,
                () -> service.rotateRefreshToken(RAW_TOKEN, "agent", "ip"));

        verify(refreshTokenRepository, never()).revokeAllUserTokens(any(), any());
        verify(refreshTokenRepository, never()).save(any());
        verify(userRepository).findByIdForUpdate(USER_ID);
    }

    @Test
    void disabledUserRefreshRevokesSessionsBeforeReturningAnAuthenticationFailure() {
        Usuario disabled = activeUser();
        disabled.setBajaLogica(true);
        RefreshToken refreshToken = storedToken(disabled, sha256(RAW_TOKEN), LocalDateTime.now().plusMinutes(5));
        when(refreshTokenRepository.findUsuarioIdByTokenDigest(sha256(RAW_TOKEN))).thenReturn(Optional.of(USER_ID));
        when(userRepository.findByIdForUpdate(USER_ID)).thenReturn(Optional.of(disabled));
        when(refreshTokenRepository.findByTokenDigestForUpdate(sha256(RAW_TOKEN))).thenReturn(Optional.of(refreshToken));

        assertThrows(RefreshTokenService.DisabledUserRefreshException.class,
                () -> service.rotateRefreshToken(RAW_TOKEN, "agent", "ip"));

        verify(refreshTokenRepository).revokeAllUserTokens(eq(USER_ID), any(LocalDateTime.class));
        verify(refreshTokenRepository, never()).save(any());
        assertNoRollbackFor(RefreshTokenService.DisabledUserRefreshException.class);
    }

    @Test
    void expiredOldestSessionDoesNotEvictAnActiveSession() throws Exception {
        Usuario user = activeUser();
        LocalDateTime now = LocalDateTime.now();
        RefreshToken expiredOldest = storedToken(user, "e".repeat(64), now.minusMinutes(1));
        expiredOldest.setCreatedAt(now.minusDays(2));
        RefreshToken activeOldest = storedToken(user, "a".repeat(64), now.plusMinutes(5));
        activeOldest.setCreatedAt(now.minusDays(1));
        RefreshToken activeNewest = storedToken(user, "n".repeat(64), now.plusMinutes(10));
        ReflectionTestUtils.setField(service, "maxActiveSessions", 2);
        when(userRepository.findByIdForUpdate(USER_ID)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.countByUsuarioIdAndRevokedAtIsNullAndExpiryDateAfter(eq(USER_ID), any()))
                .thenReturn(2L);
        when(refreshTokenRepository.findByUsuarioIdAndRevokedAtIsNull(USER_ID))
                .thenReturn(java.util.List.of(expiredOldest, activeOldest, activeNewest));
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.issueRefreshToken(USER_ID, "agent", "ip");

        assertNull(expiredOldest.getRevokedAt());
        assertNotNull(activeOldest.getRevokedAt());
        assertNull(activeNewest.getRevokedAt());
        ArgumentCaptor<RefreshToken> saved = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository, times(2)).save(saved.capture());
        assertTrue(saved.getAllValues().contains(activeOldest));
    }

    @Test
    void missingUserAgentIsStoredAsANonNullFallback() throws Exception {
        Usuario user = activeUser();
        when(userRepository.findByIdForUpdate(USER_ID)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.countByUsuarioIdAndRevokedAtIsNullAndExpiryDateAfter(eq(USER_ID), any()))
                .thenReturn(0L);
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken issued = service.issueRefreshToken(USER_ID, null, "127.0.0.1").refreshToken();

        assertEquals("Unknown", issued.getUserAgent());
    }

    @Test
    void revokeTokenHashesThePresentedCredentialAndMalformedLogoutIsANoop() {
        when(refreshTokenRepository.findByTokenDigest(sha256(RAW_TOKEN))).thenReturn(Optional.empty());

        service.revokeToken(RAW_TOKEN);
        service.revokeToken("malformed");

        verify(refreshTokenRepository).findByTokenDigest(sha256(RAW_TOKEN));
        verify(refreshTokenRepository, never()).findByTokenDigest("malformed");
    }

    private void assertNoRollbackFor(Class<? extends RuntimeException> exceptionType) {
        try {
            Transactional transaction = RefreshTokenService.class.getMethod(
                    "rotateRefreshToken", String.class, String.class, String.class).getAnnotation(Transactional.class);
            assertTrue(java.util.Arrays.asList(transaction.noRollbackFor()).contains(exceptionType));
        } catch (NoSuchMethodException exception) {
            throw new AssertionError(exception);
        }
    }

    private Usuario activeUser() {
        return Usuario.builder().id(USER_ID).email("refresh@example.test").nombre("Refresh")
                .bajaLogica(false).build();
    }

    private RefreshToken storedToken(Usuario user, String digest, LocalDateTime expiry) {
        RefreshToken token = new RefreshToken();
        token.setUsuario(user);
        token.setTokenDigest(digest);
        token.setCreatedAt(LocalDateTime.now().minusMinutes(1));
        token.setExpiryDate(expiry);
        token.setUserAgent("old-agent");
        token.setIpAddress("198.51.100.1");
        return token;
    }

    private static String sha256(String token) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.US_ASCII)));
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }
}
