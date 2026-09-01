package utn.back.mordiscoapi.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.config.AppProperties;
import utn.back.mordiscoapi.config.TimeConfiguration;
import utn.back.mordiscoapi.common.validation.ValidationConstants;
import utn.back.mordiscoapi.event.auth.PasswordChangedEvent;
import utn.back.mordiscoapi.event.auth.PasswordResetRequestedEvent;
import utn.back.mordiscoapi.model.dto.auth.RecoverPasswordDTO;
import utn.back.mordiscoapi.model.dto.auth.ResetPasswordDTO;
import utn.back.mordiscoapi.model.entity.PasswordRecoveryCredential;
import utn.back.mordiscoapi.repository.PasswordRecoveryCredentialRepository;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.security.jwt.service.RefreshTokenService;
import utn.back.mordiscoapi.security.recovery.PasswordRecoveryTokenCodec;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordRecoveryService {
    private static final String GENERIC_RESET_ERROR = "El token es inválido o ha expirado";
    private static final int CLEANUP_BATCH_SIZE = 100;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(ValidationConstants.PASSWORD_PATTERN);

    private final UsuarioRepository usuarioRepository;
    private final PasswordRecoveryCredentialRepository credentialRepository;
    private final PasswordRecoveryTokenCodec tokenCodec;
    private final RefreshTokenService refreshTokenService;
    private final ApplicationEventPublisher eventPublisher;
    private final AppProperties appProperties;
    private final Clock clock;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public void requestPasswordRecovery(RecoverPasswordDTO dto) {
        LocalDateTime now = TimeConfiguration.toUtcMicroseconds(clock.instant());
        var usuario = usuarioRepository.findByEmailForUpdate(dto.email());
        if (usuario.isEmpty() || !usuario.get().isEnabled()) {
            return;
        }

        var current = credentialRepository.findByUsuarioId(usuario.get().getId());
        if (current.isPresent() && now.isBefore(current.get().getCooldownUntil())) {
            return;
        }

        String token = tokenCodec.generateToken();
        PasswordRecoveryCredential credential = current.orElseGet(PasswordRecoveryCredential::new);
        credential.setUsuario(usuario.get());
        credential.setTokenDigest(tokenCodec.digest(token));
        credential.setIssuedAt(now);
        credential.setExpiresAt(now.plusSeconds(appProperties.getPasswordRecovery().getExpirationSeconds()));
        credential.setCooldownUntil(now.plusSeconds(appProperties.getPasswordRecovery().getCooldownSeconds()));
        credential.setConsumedAt(null);
        credentialRepository.save(credential);
        eventPublisher.publishEvent(new PasswordResetRequestedEvent(usuario.get().getId(), usuario.get().getEmail(),
                usuario.get().getNombre(), appProperties.getFrontendUrl() + "/reset-password?token=" + token));
    }

    @Transactional
    public void resetPassword(ResetPasswordDTO dto) throws BadRequestException {
        if (dto.newPassword() == null || dto.newPassword().isBlank()) {
            throw new BadRequestException("La contraseña es obligatoria");
        }
        if (!PASSWORD_PATTERN.matcher(dto.newPassword()).matches()) {
            throw new BadRequestException(ValidationConstants.PASSWORD_MESSAGE);
        }
        final String digest;
        try {
            digest = tokenCodec.digest(dto.token());
        } catch (IllegalArgumentException exception) {
            throw invalidToken();
        }
        Long userId = credentialRepository.findUsuarioIdByTokenDigest(digest).orElseThrow(this::invalidToken);
        var usuario = usuarioRepository.findByIdForUpdate(userId).orElseThrow(this::invalidToken);
        if (!usuario.isEnabled()) {
            throw invalidToken();
        }
        LocalDateTime now = TimeConfiguration.toUtcMicroseconds(clock.instant());
        if (credentialRepository.consumeIfCurrentAndUnexpired(userId, digest, now) != 1) {
            throw invalidToken();
        }
        usuario.setPassword(passwordEncoder.encode(dto.newPassword()));
        usuarioRepository.save(usuario);
        refreshTokenService.revokeAllUserSessions(userId, now);
        eventPublisher.publishEvent(new PasswordChangedEvent(userId, usuario.getEmail(), usuario.getNombre(),
                appProperties.getFrontendUrl() + "/login"));
    }

    @Scheduled(cron = "0 15 3 * * *", zone = "UTC")
    @Transactional
    public void cleanupExpiredCredentials() {
        LocalDateTime now = TimeConfiguration.toUtcMicroseconds(clock.instant());
        while (true) {
            List<Long> credentialIds = credentialRepository.findCleanupCandidateIds(now,
                    org.springframework.data.domain.PageRequest.of(0, CLEANUP_BATCH_SIZE));
            if (credentialIds.isEmpty()) {
                return;
            }

            int deleted = credentialRepository.deleteExpiredOrConsumedAfterCooldown(credentialIds, now);
            log.info("Password recovery credential cleanup deleted {} records", deleted);
        }
    }

    private BadRequestException invalidToken() {
        return new BadRequestException(GENERIC_RESET_ERROR);
    }
}
