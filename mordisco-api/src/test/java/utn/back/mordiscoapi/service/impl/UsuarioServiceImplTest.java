package utn.back.mordiscoapi.service.impl;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.config.AppProperties;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioCreateDTO;
import utn.back.mordiscoapi.model.entity.Rol;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.repository.RolRepository;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.security.jwt.utils.AuthUtils;
import utn.back.mordiscoapi.security.jwt.utils.JwtUtil;
import utn.back.mordiscoapi.service.interf.IEmailService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {
    private static final Long ROLE_ID = 1L;
    private static final String PLAINTEXT_PASSWORD = "Password1!";

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UsuarioRepository repository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private AuthUtils authUtils;
    @Mock
    private AppProperties appProperties;
    @Mock
    private IEmailService emailService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private UsuarioServiceImpl service;

    @ParameterizedTest
    @ValueSource(strings = {"ROLE_CLIENTE", "ROLE_RESTAURANTE", "ROLE_REPARTIDOR"})
    void savePersistsUserWithResolvedAllowedRole(String roleName) throws Exception {
        Rol resolvedRole = Rol.builder().id(ROLE_ID).nombre(roleName).build();
        when(rolRepository.findById(ROLE_ID)).thenReturn(Optional.of(resolvedRole));

        service.save(createDto());

        ArgumentCaptor<Usuario> userCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(userCaptor.capture());
        Usuario savedUser = userCaptor.getValue();
        assertSame(resolvedRole, savedUser.getRol());
        assertFalse(savedUser.getBajaLogica());
        assertTrue(new BCryptPasswordEncoder().matches(PLAINTEXT_PASSWORD, savedUser.getPassword()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ROLE_ADMIN", "ROLE_AUDITOR"})
    void saveRejectsDisallowedResolvedRole(String roleName) {
        when(rolRepository.findById(ROLE_ID)).thenReturn(Optional.of(Rol.builder().nombre(roleName).build()));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> service.save(createDto()));

        assertEquals("Rol no permitido para registro público", exception.getMessage());
        verifyNoInteractions(repository);
    }

    @org.junit.jupiter.api.Test
    void saveRejectsUnknownRole() {
        when(rolRepository.findById(ROLE_ID)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.save(createDto()));

        assertEquals("Rol no encontrado", exception.getMessage());
        verifyNoInteractions(repository);
    }

    private UsuarioCreateDTO createDto() {
        return new UsuarioCreateDTO(
                "Test",
                "User",
                "1234567890",
                "test@example.com",
                PLAINTEXT_PASSWORD,
                ROLE_ID
        );
    }
}
