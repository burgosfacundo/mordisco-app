package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.UsuarioMapper;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioDTO;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioResponseDTO;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioUpdateDTO;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.repository.RolRepository;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.service.interf.IUsuarioService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {
    private final UsuarioRepository repository;
    private final RolRepository rolRepository;
    /**
     * Guarda un usuario.
     * @param dto DTO del usuario a guardar.
     * @throws NotFoundException si el rol no se encuentra.
     */
    @Override
    public void save(UsuarioDTO dto) throws NotFoundException {
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

    /**
     * Actualiza un usuario.
     * @param id el ID del usuario a actualizar.
     * @param dto DTO del usuario a actualizar.
     * @throws NotFoundException si el usuario no se encuentra.
     */
    @Override
    public void update(Long id, UsuarioUpdateDTO dto) throws NotFoundException {
        Usuario usuario = repository.findById(id).orElseThrow(
                () -> new NotFoundException("Usuario no encontrado")
        );

        usuario.setNombre(dto.nombre());
        usuario.setApellido(dto.apellido());
        usuario.setTelefono(dto.telefono());
        repository.save(usuario);
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

    /**
     * Cambia la contraseña del usuario.
     * @param id el ID del usuario a actualizar.
     * @param oldPassword vieja contraseña.
     * @param newPassword nueva contraseña.
     * @throws NotFoundException si el usuario no se encuentra.
     */
    @Override
    public void changePassword(String oldPassword, String newPassword, Long id) throws NotFoundException {
        Usuario usuario = repository.findById(id).orElseThrow(
                () -> new NotFoundException("Usuario no encontrado")
        );

        if(!usuario.getPassword().equals(oldPassword)){
            throw new NotFoundException("Contraseña incorrecta");
        }

        usuario.setPassword(newPassword);
        repository.changePassword(newPassword, usuario.getId());
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
}
