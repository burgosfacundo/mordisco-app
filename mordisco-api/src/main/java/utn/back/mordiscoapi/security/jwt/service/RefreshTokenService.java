package utn.back.mordiscoapi.security.jwt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.security.jwt.model.entity.RefreshToken;
import utn.back.mordiscoapi.security.jwt.repository.RefreshTokenRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {
    private static final int TOKEN_BYTES = 32;
    private static final String UNKNOWN_USER_AGENT = "Unknown";
    private static final Pattern TOKEN_PATTERN = Pattern.compile("[A-Za-z0-9_-]{43}");
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository userRepository;

    @Value("${app.jwt.refresh.expiration:2592000000}")
    private Long refreshTokenDuration;

    @Value("${app.jwt.max-sessions:5}")
    private int maxActiveSessions;

    @Transactional
    public RefreshToken createRefreshToken(Long userId, String userAgent, String ipAddress) throws NotFoundException {
        return issueRefreshToken(userId, userAgent, ipAddress).refreshToken();
    }

    @Transactional
    public IssuedRefreshToken issueRefreshToken(Long userId, String userAgent, String ipAddress) throws NotFoundException {
        Usuario usuario = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        return issueRefreshToken(usuario, userAgent, ipAddress, LocalDateTime.now());
    }

    private IssuedRefreshToken issueRefreshToken(
            Usuario usuario, String userAgent, String ipAddress, LocalDateTime now) {
        Long userId = usuario.getId();
        long activeSessions = refreshTokenRepository
                .countByUsuarioIdAndRevokedAtIsNullAndExpiryDateAfter(userId, now);

        if (activeSessions >= maxActiveSessions) {
            revokeOldestSession(userId, now);
        }

        String opaqueToken = generateToken();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuario(usuario);
        refreshToken.setTokenDigest(digestToken(opaqueToken));
        refreshToken.setExpiryDate(now.plus(Duration.ofMillis(refreshTokenDuration)));
        refreshToken.setCreatedAt(now);
        refreshToken.setUserAgent(userAgent == null ? UNKNOWN_USER_AGENT : userAgent);
        refreshToken.setIpAddress(ipAddress);

        RefreshToken persistedToken = refreshTokenRepository.save(refreshToken);
        return new IssuedRefreshToken(persistedToken, opaqueToken);
    }

    @Transactional(noRollbackFor = {
            RefreshTokenReuseException.class,
            DisabledUserRefreshException.class
    })
    public IssuedRefreshToken rotateRefreshToken(String rawToken, String userAgent, String ipAddress) throws NotFoundException {
        String tokenDigest = digestToken(rawToken);
        Long userId = refreshTokenRepository.findUsuarioIdByTokenDigest(tokenDigest)
                .orElseThrow(RefreshTokenAuthenticationException::new);
        Usuario usuario = userRepository.findByIdForUpdate(userId)
                .orElseThrow(RefreshTokenAuthenticationException::new);
        RefreshToken oldRefreshToken = refreshTokenRepository.findByTokenDigestForUpdate(tokenDigest)
                .orElseThrow(RefreshTokenAuthenticationException::new);
        LocalDateTime now = LocalDateTime.now();

        if (oldRefreshToken.isRevoked()) {
            revokeAllUserSessions(userId, now);
            throw new RefreshTokenReuseException();
        }

        if (!oldRefreshToken.getExpiryDate().isAfter(now)) {
            throw new RefreshTokenAuthenticationException();
        }

        if (!usuario.isEnabled()) {
            revokeAllUserSessions(userId, now);
            throw new DisabledUserRefreshException();
        }

        oldRefreshToken.setRevokedAt(now);
        refreshTokenRepository.save(oldRefreshToken);

        return issueRefreshToken(usuario, userAgent, ipAddress, now);
    }

    @Transactional(noRollbackFor = {
            RefreshTokenReuseException.class,
            DisabledUserRefreshException.class
    })
    public RefreshToken verifyRefreshToken(String rawToken) {
        String tokenDigest = digestToken(rawToken);
        Long userId = refreshTokenRepository.findUsuarioIdByTokenDigest(tokenDigest)
                .orElseThrow(RefreshTokenAuthenticationException::new);
        Usuario usuario = userRepository.findByIdForUpdate(userId)
                .orElseThrow(RefreshTokenAuthenticationException::new);
        RefreshToken refreshToken = refreshTokenRepository.findByTokenDigestForUpdate(tokenDigest)
                .orElseThrow(RefreshTokenAuthenticationException::new);
        LocalDateTime now = LocalDateTime.now();

        if (refreshToken.isRevoked()) {
            revokeAllUserSessions(userId, now);
            throw new RefreshTokenReuseException();
        }
        if (!refreshToken.getExpiryDate().isAfter(now)) {
            throw new RefreshTokenAuthenticationException();
        }
        if (!usuario.isEnabled()) {
            revokeAllUserSessions(userId, now);
            throw new DisabledUserRefreshException();
        }

        return refreshToken;
    }

    @Transactional
    public void revokeToken(String rawToken) {
        if (!hasExpectedTokenShape(rawToken)) {
            return;
        }

        refreshTokenRepository.findByTokenDigest(digestToken(rawToken)).ifPresent(refreshToken -> {
            if (!refreshToken.isRevoked()) {
                refreshToken.setRevokedAt(LocalDateTime.now());
                refreshTokenRepository.save(refreshToken);
            }
        });
    }

    @Transactional
    public void revokeAllUserSessions(Long userId) {
        revokeAllUserSessions(userId, LocalDateTime.now());
    }

    @Transactional
    public void revokeAllUserSessions(Long userId, LocalDateTime now) {
        userRepository.findByIdForUpdate(userId);
        refreshTokenRepository.revokeAllUserTokens(userId, now);
        log.info("All refresh sessions revoked for user {}", userId);
    }

    private void revokeOldestSession(Long userId, LocalDateTime now) {
        refreshTokenRepository.findByUsuarioIdAndRevokedAtIsNull(userId).stream()
                .filter(session -> session.getExpiryDate().isAfter(now))
                .min(Comparator.comparing(RefreshToken::getCreatedAt))
                .ifPresent(oldest -> {
                    oldest.setRevokedAt(now);
                    refreshTokenRepository.save(oldest);
                });
    }

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.revokeExpiredTokens(LocalDateTime.now());
        log.info("Expired session cleanup completed");
    }

    private static String generateToken() {
        byte[] tokenBytes = new byte[TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private static String digestToken(String rawToken) {
        if (!hasExpectedTokenShape(rawToken)) {
            throw new RefreshTokenAuthenticationException();
        }

        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(rawToken.getBytes(StandardCharsets.US_ASCII));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    private static boolean hasExpectedTokenShape(String rawToken) {
        return rawToken != null && TOKEN_PATTERN.matcher(rawToken).matches();
    }

    public record IssuedRefreshToken(RefreshToken refreshToken, String opaqueToken) {
        @Override
        public String toString() {
            return "IssuedRefreshToken[opaqueToken=<redacted>]";
        }
    }

    public static class RefreshTokenAuthenticationException extends AccessDeniedException {
        public RefreshTokenAuthenticationException() {
            super("Refresh token is invalid or unavailable");
        }
    }

    public static final class RefreshTokenReuseException extends RefreshTokenAuthenticationException {
    }

    public static final class DisabledUserRefreshException extends RefreshTokenAuthenticationException {
    }
}
