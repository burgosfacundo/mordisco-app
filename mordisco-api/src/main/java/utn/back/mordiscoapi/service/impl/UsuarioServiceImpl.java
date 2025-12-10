package utn.back.mordiscoapi.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.common.exception.InternalServerErrorException;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.config.AppProperties;
import utn.back.mordiscoapi.event.auth.CuentaBloqueadaEvent;
import utn.back.mordiscoapi.event.auth.PasswordChangedEvent;
import utn.back.mordiscoapi.event.auth.PasswordResetRequestedEvent;
import utn.back.mordiscoapi.mapper.PedidoMapper;
import utn.back.mordiscoapi.mapper.UsuarioMapper;
import utn.back.mordiscoapi.model.dto.auth.RecoverPasswordDTO;
import utn.back.mordiscoapi.model.dto.auth.ResetPasswordDTO;
import utn.back.mordiscoapi.model.dto.pedido.PedidoResponseDTO;
import utn.back.mordiscoapi.model.dto.usuario.*;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.repository.RolRepository;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.security.jwt.utils.JwtUtil;
import utn.back.mordiscoapi.service.interf.IEmailService;
import utn.back.mordiscoapi.service.interf.IUsuarioService;
import utn.back.mordiscoapi.security.jwt.utils.AuthUtils;
import utn.back.mordiscoapi.common.util.Sanitize;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService, UserDetailsService {
    private final JwtUtil jwtUtil;
    private final UsuarioRepository repository;
    private final RolRepository rolRepository;
    private final AuthUtils authUtils;
    private final AppProperties appProperties;
    private final IEmailService emailService;
    private final ApplicationEventPublisher eventPublisher;
    final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Guarda un usuario.
     * @param dto DTO del usuario a guardar.
     * @throws NotFoundException si el rol no se encuentra.
     */
    @Override
    public void save(UsuarioCreateDTO dto) throws NotFoundException {
        if (!rolRepository.existsById(dto.rolId())){
            throw new NotFoundException("Rol no encontrado");
        }
        var user = UsuarioMapper.toUsuario(dto);
        user.setBajaLogica(false);
        repository.save(user);
    }

    /**
     * Obtiene todos los usuarios paginados.
     * @return una página de usuarios proyectados.
     */
    @Override
    public Page<UsuarioCardDTO> findAll(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return repository.findAll(pageable)
                .map(UsuarioMapper::toUsuarioCardDTO);
    }

    /**
     * Obtiene un usuario por su ID.
     * @param id el ID del usuario a buscar.
     * @return el usuario proyectado.
     * @throws NotFoundException si el usuario no se encuentra.
     */
    @Override
    public UsuarioResponseDTO findById(Long id) throws NotFoundException {
        Optional<Usuario> usuario = repository.findById(id);
        if (usuario.isEmpty()){
            throw new NotFoundException("Usuario no encontrado");
        }

        return UsuarioMapper.toUsuarioResponseDTO(usuario.get());
    }

    @Override
    public UsuarioResponseDTO getMe() throws NotFoundException, BadRequestException {
        var userAuthenticated = authUtils.getUsuarioAutenticado()
                .orElseThrow(() -> new BadRequestException("No autenticado"));

        var user = repository.findById(userAuthenticated.getId()).orElseThrow(
                () -> new NotFoundException("Usuario no encontrado"));

        return UsuarioMapper.toUsuarioResponseDTO(user);
    }

    /**
     * Actualiza un usuario.
     * @param id el ID del usuario a actualizar.
     * @param dto DTO del usuario a actualizar.
     * @throws NotFoundException si el usuario no se encuentra.
     */
    @Transactional
    @Override
    public void update(Long id, UsuarioUpdateDTO dto) throws NotFoundException, BadRequestException {
        Usuario usuario = repository.findById(id).orElseThrow(
                () -> new NotFoundException("Usuario no encontrado")
        );

        usuario.setNombre(Sanitize.collapseSpaces(dto.nombre()));
        usuario.setApellido(Sanitize.collapseSpaces(dto.apellido()));
        usuario.setTelefono(Sanitize.collapseSpaces(dto.telefono()));
    }

    @Override
    public void updateMe(UsuarioUpdateDTO dto) throws NotFoundException, BadRequestException {
        var userAuthenticated = authUtils.getUsuarioAutenticado()
                .orElseThrow(() -> new BadRequestException("No autenticado"));

        var user = repository.findById(userAuthenticated.getId()).orElseThrow(
                () -> new NotFoundException("Usuario no encontrado"));

        user.setNombre(Sanitize.collapseSpaces(dto.nombre()));
        user.setApellido(Sanitize.collapseSpaces(dto.apellido()));
        user.setTelefono(Sanitize.collapseSpaces(dto.telefono()));

        repository.save(user);
    }

    /**
     * Elimina un usuario validando que no tenga pedidos activos
     *
     * @param id del usuario a eliminar
     * @throws NotFoundException si no se encuentra el usuario
     * @throws BadRequestException si el usuario tiene pedidos activos
     */
    @Transactional
    @Override
    public void delete(Long id) throws NotFoundException, BadRequestException {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        long pedidosActivos = repository.countPedidosActivosComoCliente(id);

        if (pedidosActivos > 0) {
            String mensaje = String.format(
                    "No se puede bloquear la cuenta. El usuario tiene %d pedido%s activo%s. " +
                            "Debe esperar a que se completen o cancelarlos antes de bloquear la cuenta.",
                    pedidosActivos,
                    pedidosActivos == 1 ? "" : "s",
                    pedidosActivos == 1 ? "" : "s"
            );

            throw new BadRequestException(mensaje);
        }

        // Baja lógica - bloqueo por administrador
        usuario.setBajaLogica(true);
        usuario.setFechaBaja(java.time.LocalDateTime.now());
        usuario.setMotivoBaja("Bloqueado por administrador");
        repository.save(usuario);
    }

    /**
     *  Obtiene los pedidos activos de un usuario como cliente
     */
    @Override
    public Page<PedidoResponseDTO> getPedidosActivosComoCliente(Long usuarioId, int page, int size)
            throws NotFoundException {

        if (!repository.existsById(usuarioId)) {
            throw new NotFoundException("Usuario no encontrado");
        }

        Pageable pageable = PageRequest.of(page, size);
        return repository.findPedidosActivosComoCliente(usuarioId, pageable)
                .map(PedidoMapper::toDTO);
    }

    @Override
    public void deleteMe() throws NotFoundException, BadRequestException {
        Usuario usuario = authUtils.getUsuarioAutenticado()
                .orElseThrow(() -> new BadRequestException("No autenticado"));

        long pedidosActivos = repository.countPedidosActivosComoCliente(usuario.getId());

        if (pedidosActivos > 0) {
            String mensaje = String.format(
                    "No se puede eliminar la cuenta. Tienes %d pedido%s activo%s. " +
                            "Debe esperar a que se completen o cancelarlos antes de eliminar tu cuenta.",
                    pedidosActivos,
                    pedidosActivos == 1 ? "" : "s",
                    pedidosActivos == 1 ? "" : "s"
            );

            throw new BadRequestException(mensaje);
        }

        // Baja lógica en lugar de eliminación física
        usuario.setBajaLogica(true);
        usuario.setFechaBaja(java.time.LocalDateTime.now());
        usuario.setMotivoBaja("Eliminación solicitada por el usuario");
        repository.save(usuario);
    }

    /**
     * Cambia la contraseña del usuario.
     * @param dto con la contraseña actual y la nueva.
     * @throws NotFoundException si el usuario no se encuentra.
     */
    @Transactional
    @Override
    public void changePassword(ChangePasswordDTO dto) throws NotFoundException, BadRequestException, InternalServerErrorException {
        var userAuthenticated = authUtils.getUsuarioAutenticado()
                .orElseThrow(() -> new BadRequestException("No autenticado"));

        Usuario usuario = repository.findById(userAuthenticated.getId()).orElseThrow(
                () -> new NotFoundException("Usuario no encontrado")
        );

        if (!passwordEncoder.matches(dto.currentPassword(), usuario.getPassword())) {
            throw new NotFoundException("Contraseña actual incorrecta");
        }

        usuario.setPassword(passwordEncoder.encode(dto.newPassword()));
        repository.save(usuario);
        
        // Publicar evento de cambio de contraseña
        String loginLink = appProperties.getFrontendUrl() + "/login";
        eventPublisher.publishEvent(new PasswordChangedEvent(
                usuario.getId(), usuario.getEmail(), usuario.getNombre(), loginLink));
    }

    /**
     * Obtiene una lista de usuarios por rol.
     * @param id del rol a buscar.
     * @return la lista de usuarios pertenecientes a ese rol.
     * @throws NotFoundException si el rol no se encuentra.
     */
    @Override
    public Page<UsuarioCardDTO> findByRolId(int pageNo,int pageSize,Long id) throws NotFoundException {
        if (!rolRepository.existsById(id)){
            throw new NotFoundException("Rol no encontrado");
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return repository.findUsuarioByRol_Id(pageable,id)
                .map(UsuarioMapper::toUsuarioCardDTO);
    }

    /**
     * Carga un usuario por su email.
     * @param username el email del usuario a buscar.
     * @return el usuario encontrado.
     * @throws UsernameNotFoundException si el email no se encuentra registrado.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("El email no se encuentra registrado."));
    }


    @Transactional
    public void requestPasswordRecovery(RecoverPasswordDTO dto)
            throws NotFoundException, InternalServerErrorException {

        Usuario usuario = repository.findByEmail(dto.email())
                .orElseThrow(() -> new NotFoundException("No existe una cuenta con ese email"));

        // Generar token JWT especial para recuperación (válido por 1 hora)
        String recoveryToken = jwtUtil.generateRecoveryPasswordToken(usuario);

        // Construir URL de recuperación
        String resetUrl = appProperties.getFrontendUrl() + "/reset-password?token=" + recoveryToken;

        // Publicar evento de solicitud de reset de contraseña
        eventPublisher.publishEvent(new PasswordResetRequestedEvent(
                usuario.getId(), usuario.getEmail(), usuario.getNombre(), resetUrl));

        log.info("📧 Evento de recuperación publicado para: {}", usuario.getEmail());
    }

    /**
     * Restablece la contraseña usando el token
     */
    @Transactional
    public void resetPassword(ResetPasswordDTO dto)
            throws BadRequestException, NotFoundException {

        // Validar token
        if (!jwtUtil.isTokenValid(dto.token())) {
            throw new BadRequestException("El token es inválido o ha expirado");
        }

        // Extraer email del token
        String email = jwtUtil.extractUserName(dto.token());

        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        // Cambiar contraseña
        usuario.setPassword(passwordEncoder.encode(dto.newPassword()));
        repository.save(usuario);

        // Publicar evento de cambio de contraseña
        String loginLink = appProperties.getFrontendUrl() + "/login";
        eventPublisher.publishEvent(new PasswordChangedEvent(
                usuario.getId(), usuario.getEmail(), usuario.getNombre(), loginLink));

        log.info("✅ Contraseña restablecida para: {}", email);
    }

    /**
     * Da de baja lógicamente a un usuario
     * Si el usuario tiene rol RESTAURANTE, también desactiva el restaurante
     */
    @Transactional
    @Override
    public void darDeBaja(Long usuarioId, String motivo) throws NotFoundException {
        Usuario usuario = repository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        usuario.setBajaLogica(true);
        usuario.setMotivoBaja(motivo);
        usuario.setFechaBaja(java.time.LocalDateTime.now());

        // Si el usuario tiene rol RESTAURANTE, desactivar el restaurante
        if (usuario.getRol() != null && "ROLE_RESTAURANTE".equals(usuario.getRol().getNombre())) {
            if (usuario.getRestaurante() != null) {
                usuario.getRestaurante().setActivo(false);
            }
        }

        repository.save(usuario);
        
        // Publicar evento de cuenta bloqueada
        eventPublisher.publishEvent(new CuentaBloqueadaEvent(
                usuario.getId(), usuario.getEmail(), usuario.getNombre(), motivo));
    }

    /**
     * Reactiva un usuario dado de baja
     * Si el usuario tiene rol RESTAURANTE, también activa el restaurante
     */
    @Transactional
    @Override
    public void reactivar(Long usuarioId) throws NotFoundException {
        Usuario usuario = repository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        usuario.setBajaLogica(false);
        usuario.setMotivoBaja(null);
        usuario.setFechaBaja(null);

        // Si el usuario tiene rol RESTAURANTE, activar el restaurante
        if (usuario.getRol() != null && "ROLE_RESTAURANTE".equals(usuario.getRol().getNombre())) {
            if (usuario.getRestaurante() != null) {
                usuario.getRestaurante().setActivo(true);
            }
        }

        repository.save(usuario);
    }

    @Override
    public Page<UsuarioCardDTO> filtrarUsuarios(
            int pageNo, int pageSize,
            String search,
            String bajaLogica,
            String rol) {

        // Convertir String a Boolean para bajaLogica
        Boolean bajaLogicaBoolean = null;
        if (bajaLogica != null && !bajaLogica.isBlank()) {
            bajaLogicaBoolean = bajaLogica.equals("1"); // "1" = true (bloqueado), "0" = false (activo)
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        return repository.filtrarUsuario(
                search,
                bajaLogicaBoolean,
                rol,
                pageable
        ).map(UsuarioMapper::toUsuarioCardDTO);
    }
}

