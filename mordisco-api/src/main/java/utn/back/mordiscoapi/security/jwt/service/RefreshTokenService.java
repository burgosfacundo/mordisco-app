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

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository userRepository;

    @Value("${jwt.refresh.expiration:2592000000}") // 30 días default
    private Long refreshTokenDuration;

    @Value("${jwt.max-sessions:5}") // Máximo 5 sesiones activas
    private int maxActiveSessions;

    @Transactional
    public RefreshToken createRefreshToken(Long userId, String userAgent, String ipAddress) throws NotFoundException {
        long activeSessions = refreshTokenRepository
                .countByUsuarioIdAndRevokedAtIsNullAndExpiryDateAfter(
                        userId, LocalDateTime.now());

        if (activeSessions >= maxActiveSessions) {
            revokeOldestSession(userId);
        }

        Usuario usuario = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuario(usuario);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(refreshTokenDuration / 1000));
        refreshToken.setCreatedAt(LocalDateTime.now());
        refreshToken.setUserAgent(userAgent);
        refreshToken.setIpAddress(ipAddress);

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken rotateRefreshToken(String oldToken, String userAgent, String ipAddress) throws NotFoundException {
        RefreshToken oldRefreshToken = verifyRefreshToken(oldToken);

        oldRefreshToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(oldRefreshToken);

        return createRefreshToken(
                oldRefreshToken.getUsuario().getId(),
                userAgent,
                ipAddress
        );
    }


    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new AccessDeniedException("Refresh token no válido"));

        if (refreshToken.isRevoked()) {
            log.warn("Intento de uso de refresh token revocado: {}", token);
            revokeAllUserSessions(refreshToken.getUsuario().getId());
            throw new SecurityException("Token comprometido - sesiones revocadas");
        }

        if (refreshToken.isExpired()) {
            throw new AccessDeniedException("Refresh token expirado");
        }

        return refreshToken;
    }

    @Transactional
    public void revokeToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(rt);
        });
    }

    @Transactional
    public void revokeAllUserSessions(Long userId) {
        refreshTokenRepository.revokeAllUserTokens(userId, LocalDateTime.now());
        log.info("Todas las sesiones del usuario {} han sido revocadas", userId);
    }

    private void revokeOldestSession(Long userId) {
        List<RefreshToken> activeSessions = refreshTokenRepository
                .findByUsuarioIdAndRevokedAtIsNull(userId);

        activeSessions.stream()
                .min(Comparator.comparing(RefreshToken::getCreatedAt))
                .ifPresent(oldest -> {
                    oldest.setRevokedAt(LocalDateTime.now());
                    refreshTokenRepository.save(oldest);
                });
    }

    // Job para limpiar tokens expirados
    @Scheduled(cron = "0 0 2 * * ?") // 2 AM todos los días
    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.revokeExpiredTokens(LocalDateTime.now());
        log.info("Limpieza de refresh tokens expirados completada");
    }
}
