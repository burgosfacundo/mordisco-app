package utn.back.mordiscoapi.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.data.jpa.repository.Query;
import org.mockito.junit.jupiter.MockitoExtension;
import utn.back.mordiscoapi.config.AppProperties;
import utn.back.mordiscoapi.repository.PasswordRecoveryCredentialRepository;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.security.jwt.service.RefreshTokenService;
import utn.back.mordiscoapi.security.recovery.PasswordRecoveryTokenCodec;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordRecoveryCleanupTest {
    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00.123456Z");

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordRecoveryCredentialRepository credentialRepository;
    @Mock private PasswordRecoveryTokenCodec tokenCodec;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private org.springframework.context.ApplicationEventPublisher eventPublisher;
    @Mock private AppProperties appProperties;

    @Test
    void cleanupProcessesAtMostOneCandidateBatchPerInvocation() {
        java.util.List<Long> batch = java.util.List.of(11L, 12L);
        when(credentialRepository.findCleanupCandidateIds(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(batch, java.util.List.of());
        when(credentialRepository.deleteExpiredOrConsumedAfterCooldown(org.mockito.ArgumentMatchers.anyList(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(2);

        service().cleanupExpiredCredentials();

        java.time.LocalDateTime expectedNow = java.time.LocalDateTime.ofInstant(NOW, ZoneOffset.UTC);
        org.springframework.data.domain.PageRequest page = org.springframework.data.domain.PageRequest.of(0, 100);
        org.mockito.Mockito.verify(credentialRepository, org.mockito.Mockito.times(1))
                .findCleanupCandidateIds(expectedNow, page);
        org.mockito.Mockito.verify(credentialRepository, org.mockito.Mockito.times(1))
                .deleteExpiredOrConsumedAfterCooldown(batch, expectedNow);
        org.mockito.Mockito.verifyNoMoreInteractions(credentialRepository);
    }

    @Test
    void cleanupUsesTheUtcDailyScheduleAndRechecksTheTerminalRetentionPredicateBeforeDelete() throws Exception {
        Scheduled scheduled = PasswordRecoveryService.class.getDeclaredMethod("cleanupExpiredCredentials")
                .getAnnotation(Scheduled.class);
        org.junit.jupiter.api.Assertions.assertNotNull(scheduled);
        org.junit.jupiter.api.Assertions.assertEquals("UTC", scheduled.zone());
        org.junit.jupiter.api.Assertions.assertEquals("0 15 3 * * *", scheduled.cron());

        Query candidates = PasswordRecoveryCredentialRepository.class
                .getDeclaredMethod("findCleanupCandidateIds", java.time.LocalDateTime.class, org.springframework.data.domain.Pageable.class)
                .getAnnotation(Query.class);
        Query deletion = PasswordRecoveryCredentialRepository.class
                .getDeclaredMethod("deleteExpiredOrConsumedAfterCooldown", java.util.List.class, java.time.LocalDateTime.class)
                .getAnnotation(Query.class);
        String predicate = "credential.cooldownUntil <= :now\n  and (credential.consumedAt is not null or credential.expiresAt <= :now)";
        org.junit.jupiter.api.Assertions.assertTrue(candidates.value().contains(predicate));
        org.junit.jupiter.api.Assertions.assertTrue(deletion.value().contains(predicate));
    }

    @Test
    void cleanupDoesNotDeleteWhenTheCandidateBatchIsEmpty() {
        when(credentialRepository.findCleanupCandidateIds(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(java.util.List.of());

        service().cleanupExpiredCredentials();

        verify(credentialRepository).findCleanupCandidateIds(
                java.time.LocalDateTime.ofInstant(NOW, ZoneOffset.UTC), org.springframework.data.domain.PageRequest.of(0, 100));
        org.mockito.Mockito.verify(credentialRepository, org.mockito.Mockito.never())
                .deleteExpiredOrConsumedAfterCooldown(org.mockito.ArgumentMatchers.anyList(), org.mockito.ArgumentMatchers.any());
        org.mockito.Mockito.verifyNoMoreInteractions(credentialRepository);
    }

    @Test
    void cleanupDoesNotRetryWhenConditionalDeleteMakesNoProgress() {
        java.util.List<Long> batch = java.util.List.of(21L);
        when(credentialRepository.findCleanupCandidateIds(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(batch, java.util.List.of());
        when(credentialRepository.deleteExpiredOrConsumedAfterCooldown(org.mockito.ArgumentMatchers.anyList(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(0);

        service().cleanupExpiredCredentials();

        java.time.LocalDateTime expectedNow = java.time.LocalDateTime.ofInstant(NOW, ZoneOffset.UTC);
        org.springframework.data.domain.PageRequest page = org.springframework.data.domain.PageRequest.of(0, 100);
        org.mockito.Mockito.verify(credentialRepository, org.mockito.Mockito.times(1))
                .findCleanupCandidateIds(expectedNow, page);
        org.mockito.Mockito.verify(credentialRepository, org.mockito.Mockito.times(1))
                .deleteExpiredOrConsumedAfterCooldown(batch, expectedNow);
        org.mockito.Mockito.verifyNoMoreInteractions(credentialRepository);
    }

    @Test
    void cleanupDeletesSuccessfulCandidateBatchOnce() {
        java.util.List<Long> batch = java.util.List.of(31L, 32L, 33L);
        when(credentialRepository.findCleanupCandidateIds(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(batch, java.util.List.of());
        when(credentialRepository.deleteExpiredOrConsumedAfterCooldown(org.mockito.ArgumentMatchers.anyList(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(batch.size());

        service().cleanupExpiredCredentials();

        java.time.LocalDateTime expectedNow = java.time.LocalDateTime.ofInstant(NOW, ZoneOffset.UTC);
        org.mockito.Mockito.verify(credentialRepository).findCleanupCandidateIds(expectedNow,
                org.springframework.data.domain.PageRequest.of(0, 100));
        verify(credentialRepository).deleteExpiredOrConsumedAfterCooldown(batch, expectedNow);
        org.mockito.Mockito.verifyNoMoreInteractions(credentialRepository);
    }

    private PasswordRecoveryService service() {
        return new PasswordRecoveryService(usuarioRepository, credentialRepository, tokenCodec, refreshTokenService,
                eventPublisher, appProperties, Clock.fixed(NOW, ZoneOffset.UTC));
    }
}
