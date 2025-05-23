package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.UsuarioMapper;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioDTO;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioUpdateDTO;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.model.projection.UsuarioProjection;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.service.interf.IUsuarioService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {
    private final UsuarioRepository repository;
    /**
     * Guarda un usuario.
     * @param dto DTO del usuario a guardar.
     * @throws BadRequestException si hay un error al guardar el usuario.
     */
    @Override
    public void save(UsuarioDTO dto) throws BadRequestException {
        try{
            Usuario usuario = UsuarioMapper.toUsuario(dto);
            repository.save(usuario);
        }catch (DataIntegrityViolationException e){
            log.error(e.getMessage());
            throw new BadRequestException("Error al guardar usuario");
        }
    }

    /**
     * Obtiene todos las usuarios paginados.
     * @return una página de usuarios proyectados.
     */
    @Override
    public List<UsuarioProjection> findAll() {
        return repository.findAllProject();
    }

    /**
     * Obtiene un usuario por su ID.
     * @param id el ID del usuario a buscar.
     * @return el usuario proyectada.
     * @throws NotFoundException si el usuario no se encuentra.
     */
    @Override
    public UsuarioProjection findById(Long id) throws NotFoundException {
        return repository.findProjectById(id).orElseThrow(
                () -> new NotFoundException("Usuario no encontrado")
        );
    }

    /**
     * Actualiza un usuario.
     * @param id el ID del usuario a actualizar.
     * @param dto DTO del usuario a actualizar.
     * @throws NotFoundException si el usuario no se encuentra.
     * @throws BadRequestException si hay un error al actualizar el usuario.
     */
    @Override
    public void update(Long id, UsuarioUpdateDTO dto) throws NotFoundException, BadRequestException {
        Usuario usuario = repository.findById(id).orElseThrow(
                () -> new NotFoundException("Usuario no encontrado")
        );

        try{
            usuario.setNombre(dto.nombre());
            usuario.setApellido(dto.apellido());
            usuario.setTelefono(dto.telefono());
            repository.save(usuario);
        }catch (DataIntegrityViolationException e){
            log.error(e.getMessage());
            throw new BadRequestException("Error al actualizar el usuario");
        }
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
     * @throws BadRequestException si hay un error al actualizar el usuario.
     */
    @Override
    public void changePassword(String oldPassword, String newPassword, Long id) throws NotFoundException, BadRequestException {
        Usuario usuario = repository.findById(id).orElseThrow(
                () -> new NotFoundException("Usuario no encontrado")
        );

        if(!usuario.getPassword().equals(oldPassword)){
            throw new NotFoundException("Contraseña incorrecta");
        }

        try{
            usuario.setPassword(newPassword);
            repository.changePassword(newPassword, usuario.getId());
        }catch (DataIntegrityViolationException e){
            log.error(e.getMessage());
            throw new BadRequestException("Error al guardar usuario");
        }
    }

    /**
     * Obtiene una lista de usuarios por rol.
     * @param id del rol a buscar.
     * @return la lista de usuarios pertenecientes a ese rol.
     */
    @Override
    public List<UsuarioProjection> findByProjectRol(Long id){
        return repository.findProjectByRol(id);
    }
}
