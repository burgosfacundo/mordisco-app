package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.repository.PromocionRepository;
import utn.back.mordiscoapi.security.jwt.service.RefreshTokenService;
import utn.back.mordiscoapi.service.MaintenanceService;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MaintenanceServiceImpl implements MaintenanceService {

    private final PromocionRepository promocionRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordRecoveryService passwordRecoveryService;

    @Override
    public MaintenanceResult runMaintenance() {
        promocionRepository.desactivarPromocionesVencidas(LocalDate.now());
        refreshTokenService.cleanupExpiredTokens();
        passwordRecoveryService.cleanupExpiredCredentials();
        return new MaintenanceResult("completed");
    }
}
