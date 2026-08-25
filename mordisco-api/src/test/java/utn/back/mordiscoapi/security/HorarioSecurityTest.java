package utn.back.mordiscoapi.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.repository.HorarioRepository;
import utn.back.mordiscoapi.security.jwt.utils.AuthUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HorarioSecurityTest {
    private static final Long USER_ID = 10L;
    private static final Long SCHEDULE_ID = 20L;

    @Mock private AuthUtils authUtils;
    @Mock private HorarioRepository horarioRepository;

    private HorarioSecurity horarioSecurity;

    @BeforeEach
    void setUp() {
        horarioSecurity = new HorarioSecurity(authUtils, horarioRepository);
    }

    @Test
    void allowsOnlyTheOwnerToAccessTheSchedule() {
        when(authUtils.getUsuarioAutenticado()).thenReturn(Optional.of(Usuario.builder().id(USER_ID).build()));
        when(horarioRepository.existsByIdAndRestaurante_Usuario_Id(SCHEDULE_ID, USER_ID)).thenReturn(true);

        assertTrue(horarioSecurity.puedeAccederAHorario(SCHEDULE_ID));

        when(horarioRepository.existsByIdAndRestaurante_Usuario_Id(SCHEDULE_ID, USER_ID)).thenReturn(false);
        assertFalse(horarioSecurity.puedeAccederAHorario(SCHEDULE_ID));
    }

    @Test
    void deniesMissingSchedulesAndUnauthenticatedRequests() {
        when(authUtils.getUsuarioAutenticado()).thenReturn(Optional.of(Usuario.builder().id(USER_ID).build()));
        assertFalse(horarioSecurity.puedeAccederAHorario(SCHEDULE_ID));

        clearInvocations(horarioRepository);
        when(authUtils.getUsuarioAutenticado()).thenReturn(Optional.empty());
        assertFalse(horarioSecurity.puedeAccederAHorario(SCHEDULE_ID));
        verify(horarioRepository, never()).existsByIdAndRestaurante_Usuario_Id(SCHEDULE_ID, USER_ID);
    }
}
