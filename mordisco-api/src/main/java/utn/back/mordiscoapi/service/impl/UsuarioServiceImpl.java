package utn.back.mordiscoapi.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.UsuarioMapper;
import utn.back.mordiscoapi.model.dto.usuario.ChangePasswordDTO;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioCreateDTO;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioResponseDTO;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioUpdateDTO;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.repository.RolRepository;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.service.interf.IUsuarioService;
import utn.back.mordiscoapi.security.jwt.utils.AuthUtils;
import utn.back.mordiscoapi.utils.Sanitize;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService, UserDetailsService {
    private final UsuarioRepository repository;
    private final RolRepository rolRepository;
    private final AuthUtils authUtils;
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
        repository.save(UsuarioMapper.toUsuario(dto));
    }

    /**
     * Obtiene todos los usuarios paginados.
     * @return una página de usuarios proyectados.
     */
    @Override
    public List<UsuarioResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(UsuarioMapper::toUsuarioResponseDTO)
                .toList();
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
    public UsuarioResponseDTO updateMe(UsuarioUpdateDTO dto) throws NotFoundException, BadRequestException {
        var userAuthenticated = authUtils.getUsuarioAutenticado()
                .orElseThrow(() -> new BadRequestException("No autenticado"));

        var user = repository.findById(userAuthenticated.getId()).orElseThrow(
                () -> new NotFoundException("Usuario no encontrado"));

        user.setNombre(Sanitize.collapseSpaces(dto.nombre()));
        user.setApellido(Sanitize.collapseSpaces(dto.apellido()));
        user.setTelefono(Sanitize.collapseSpaces(dto.telefono()));

        return UsuarioMapper.toUsuarioResponseDTO(user);
    }

    /**
     * Elimina un usuario por su ID.
     * @param id el ID del usuario a eliminar.
     * @throws NotFoundException si el usuario no se encuentra.
     */
    @Override
    public void delete(Long id) throws NotFoundException {
        if(!repository.existsById(id)){
            throw new NotFoundException("Usuario no encontrado");
        }
        repository.deleteById(id);
    }

    @Override
    public void deleteMe() throws NotFoundException, BadRequestException {
        var userAuthenticated = authUtils.getUsuarioAutenticado()
                .orElseThrow(() -> new BadRequestException("No autenticado"));

        if(!repository.existsById(userAuthenticated.getId())){
            throw new NotFoundException("Usuario no encontrado");
        }

        repository.deleteById(userAuthenticated.getId());
    }

    /**
     * Cambia la contraseña del usuario.
     * @param dto con la contraseña actual y la nueva.
     * @throws NotFoundException si el usuario no se encuentra.
     */
    @Transactional
    @Override
    public void changePassword(ChangePasswordDTO dto) throws NotFoundException, BadRequestException {
        var userAuthenticated = authUtils.getUsuarioAutenticado()
                .orElseThrow(() -> new BadRequestException("No autenticado"));

        Usuario usuario = repository.findById(userAuthenticated.getId()).orElseThrow(
                () -> new NotFoundException("Usuario no encontrado")
        );

        if (!passwordEncoder.matches(dto.currentPassword(), usuario.getPassword())) {
            throw new NotFoundException("Contraseña actual incorrecta");
        }

        usuario.setPassword(passwordEncoder.encode(dto.newPassword()));
    }

    /**
     * Obtiene una lista de usuarios por rol.
     * @param id del rol a buscar.
     * @return la lista de usuarios pertenecientes a ese rol.
     * @throws NotFoundException si el rol no se encuentra.
     */
    @Override
    public List<UsuarioResponseDTO> findByRolId(Long id) throws NotFoundException {
        if (!rolRepository.existsById(id)){
            throw new NotFoundException("Rol no encontrado");
        }
        return repository.findUsuarioByRol_Id(id).stream()
                .map(UsuarioMapper::toUsuarioResponseDTO)
                .toList();
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
}
