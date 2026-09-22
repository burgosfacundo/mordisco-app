package utn.back.mordiscoapi.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utn.back.mordiscoapi.repository.PromocionRepository;
import utn.back.mordiscoapi.security.jwt.service.RefreshTokenService;
import utn.back.mordiscoapi.service.MaintenanceService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class MaintenanceServiceImplTest {

    @Mock
    private PromocionRepository promocionRepository;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private PasswordRecoveryService passwordRecoveryService;

    @Test
    void runsOnlyTheEstablishedIdempotentMaintenanceOperations() {
        LocalDate today = LocalDate.now();
        MaintenanceService service = new MaintenanceServiceImpl(
                promocionRepository, refreshTokenService, passwordRecoveryService);

        MaintenanceService.MaintenanceResult result = service.runMaintenance();

        assertEquals("completed", result.status());
        InOrder operations = inOrder(promocionRepository, refreshTokenService, passwordRecoveryService);
        operations.verify(promocionRepository).desactivarPromocionesVencidas(today);
        operations.verify(refreshTokenService).cleanupExpiredTokens();
        operations.verify(passwordRecoveryService).cleanupExpiredCredentials();
        verifyNoMoreInteractions(promocionRepository, refreshTokenService, passwordRecoveryService);
    }
}
