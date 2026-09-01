package utn.back.mordiscoapi.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.config.AppProperties;
import utn.back.mordiscoapi.event.auth.PasswordChangedEvent;
import utn.back.mordiscoapi.event.auth.PasswordResetRequestedEvent;
import utn.back.mordiscoapi.model.dto.auth.RecoverPasswordDTO;
import utn.back.mordiscoapi.model.dto.auth.ResetPasswordDTO;
import utn.back.mordiscoapi.model.entity.PasswordRecoveryCredential;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.repository.PasswordRecoveryCredentialRepository;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.security.jwt.service.RefreshTokenService;
import utn.back.mordiscoapi.security.recovery.PasswordRecoveryTokenCodec;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordRecoveryServiceTest {
    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final String TOKEN = "A".repeat(43);
    private static final String DIGEST = "d".repeat(64);
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordRecoveryCredentialRepository credentialRepository;
    @Mock private PasswordRecoveryTokenCodec tokenCodec;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private AppProperties appProperties;

    @Test
    void unknownAndDeactivatedAccountsHaveNoCredentialOrEventSideEffects() {
        when(usuarioRepository.findByEmailForUpdate("unknown@example.com")).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> service().requestPasswordRecovery(new RecoverPasswordDTO("unknown@example.com")));

        Usuario deactivated = activeUser();
        deactivated.setBajaLogica(true);
        when(usuarioRepository.findByEmailForUpdate(deactivated.getEmail())).thenReturn(Optional.of(deactivated));
        assertDoesNotThrow(() -> service().requestPasswordRecovery(new RecoverPasswordDTO(deactivated.getEmail())));

        verifyNoInteractions(credentialRepository, tokenCodec, eventPublisher);
    }

    @Test
    void cooldownIsSuppressedBeforeAndAllowedAtItsExactBoundary() {
        Usuario user = activeUser();
        PasswordRecoveryCredential credential = new PasswordRecoveryCredential();
        credential.setCooldownUntil(utcNow().plusNanos(1));
        when(usuarioRepository.findByEmailForUpdate(user.getEmail())).thenReturn(Optional.of(user));
        when(credentialRepository.findByUsuarioId(user.getId())).thenReturn(Optional.of(credential));
        service().requestPasswordRecovery(new RecoverPasswordDTO(user.getEmail()));
        verify(credentialRepository, never()).save(any());

        credential.setCooldownUntil(utcNow());
        configureIssuance();
        service().requestPasswordRecovery(new RecoverPasswordDTO(user.getEmail()));
        verify(credentialRepository).save(credential);
        verify(eventPublisher).publishEvent(any(PasswordResetRequestedEvent.class));
    }

    @Test
    void allowedIssuancePersistsOnlyDigestPublishesOneEventAndSupersedesPriorToken() {
        Usuario user = activeUser();
        PasswordRecoveryCredential credential = new PasswordRecoveryCredential();
        credential.setCooldownUntil(utcNow());
        when(usuarioRepository.findByEmailForUpdate(user.getEmail())).thenReturn(Optional.of(user));
        when(credentialRepository.findByUsuarioId(user.getId())).thenReturn(Optional.of(credential));
        configureIssuance();

        service().requestPasswordRecovery(new RecoverPasswordDTO(user.getEmail()));

        ArgumentCaptor<PasswordRecoveryCredential> saved = ArgumentCaptor.forClass(PasswordRecoveryCredential.class);
        verify(credentialRepository).save(saved.capture());
        assertEquals(DIGEST, saved.getValue().getTokenDigest());
        assertEquals(utcNow(), saved.getValue().getIssuedAt());
        ArgumentCaptor<PasswordResetRequestedEvent> event = ArgumentCaptor.forClass(PasswordResetRequestedEvent.class);
        verify(eventPublisher).publishEvent(event.capture());
        assertEquals("https://frontend.example/reset-password?token=" + TOKEN, event.getValue().getResetLink());
    }

    @Test
    void invalidPasswordIsRejectedBeforeAnyTokenConsumptionOrSecuritySideEffect() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> service().resetPassword(new ResetPasswordDTO(TOKEN, "not-valid")));

        assertEquals("La contraseña debe tener 8-72 caracteres, con al menos una mayúscula, una minúscula, un número y un carácter especial", exception.getMessage());
        verifyNoInteractions(credentialRepository, usuarioRepository, refreshTokenService, eventPublisher);
    }

    @Test
    void malformedUnknownExpiredConsumedSupersededAndDeactivatedTokensHaveTheSameGenericFailureWithoutEffects() {
        assertGenericFailure("malformed", () -> when(tokenCodec.digest("malformed")).thenThrow(new IllegalArgumentException()));
        assertGenericFailure(TOKEN, () -> when(tokenCodec.digest(TOKEN)).thenReturn(DIGEST));

        Usuario active = activeUser();
        assertGenericFailure(TOKEN, () -> {
            when(tokenCodec.digest(TOKEN)).thenReturn(DIGEST);
            when(credentialRepository.findUsuarioIdByTokenDigest(DIGEST)).thenReturn(Optional.of(active.getId()));
            when(usuarioRepository.findByIdForUpdate(active.getId())).thenReturn(Optional.of(active));
            when(credentialRepository.consumeIfCurrentAndUnexpired(active.getId(), DIGEST, utcNow())).thenReturn(0);
        });

        Usuario deactivated = activeUser();
        deactivated.setBajaLogica(true);
        assertGenericFailure(TOKEN, () -> {
            when(tokenCodec.digest(TOKEN)).thenReturn(DIGEST);
            when(credentialRepository.findUsuarioIdByTokenDigest(DIGEST)).thenReturn(Optional.of(deactivated.getId()));
            when(usuarioRepository.findByIdForUpdate(deactivated.getId())).thenReturn(Optional.of(deactivated));
        });
    }

    @Test
    void onlyTheConditionalConsumeWinnerChangesPasswordRevokesAffectedSessionsAndPublishesOneEvent() throws Exception {
        Usuario user = activeUser();
        when(tokenCodec.digest(TOKEN)).thenReturn(DIGEST);
        when(credentialRepository.findUsuarioIdByTokenDigest(DIGEST)).thenReturn(Optional.of(user.getId()));
        when(usuarioRepository.findByIdForUpdate(user.getId())).thenReturn(Optional.of(user));
        when(credentialRepository.consumeIfCurrentAndUnexpired(user.getId(), DIGEST, utcNow())).thenReturn(1);
        when(appProperties.getFrontendUrl()).thenReturn("https://frontend.example");

        service().resetPassword(new ResetPasswordDTO(TOKEN, "Password1!"));

        verify(usuarioRepository).save(user);
        verify(refreshTokenService).revokeAllUserSessions(eq(user.getId()), eq(utcNow()));
        verify(eventPublisher).publishEvent(any(PasswordChangedEvent.class));
    }

    private void assertGenericFailure(String token, Runnable setup) {
        org.mockito.Mockito.reset(usuarioRepository, credentialRepository, tokenCodec, refreshTokenService, eventPublisher);
        setup.run();
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> service().resetPassword(new ResetPasswordDTO(token, "Password1!")));
        assertEquals("El token es inválido o ha expirado", exception.getMessage());
        verifyNoInteractions(refreshTokenService, eventPublisher);
        verify(usuarioRepository, never()).save(any());
    }

    private void configureIssuance() {
        configuredProperties();
        when(tokenCodec.generateToken()).thenReturn(TOKEN);
        when(tokenCodec.digest(TOKEN)).thenReturn(DIGEST);
    }

    private AppProperties configuredProperties() {
        AppProperties properties = new AppProperties();
        properties.setFrontendUrl("https://frontend.example");
        when(appProperties.getFrontendUrl()).thenReturn(properties.getFrontendUrl());
        when(appProperties.getPasswordRecovery()).thenReturn(properties.getPasswordRecovery());
        return properties;
    }

    private PasswordRecoveryService service() {
        return new PasswordRecoveryService(usuarioRepository, credentialRepository, tokenCodec, refreshTokenService,
                eventPublisher, appProperties, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    private LocalDateTime utcNow() {
        return LocalDateTime.ofInstant(NOW, ZoneOffset.UTC);
    }

    private Usuario activeUser() {
        return Usuario.builder().id(7L).email("active@example.com").nombre("Active").bajaLogica(false).build();
    }
}
