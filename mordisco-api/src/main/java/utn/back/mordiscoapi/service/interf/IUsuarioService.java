package utn.back.mordiscoapi.service.interf;

import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioDTO;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioUpdateDTO;
import utn.back.mordiscoapi.model.projection.UsuarioProjection;

import java.util.List;

public interface IUsuarioService {
    void save(UsuarioDTO dto) throws BadRequestException, NotFoundException;
    List<UsuarioProjection> findAll();
    UsuarioProjection findById(Long id) throws NotFoundException;
    void delete(Long id) throws NotFoundException;
    void changePassword(String oldPassword, String newPassword, Long id) throws NotFoundException, BadRequestException;
    List<UsuarioProjection> findByProjectRol(Long id) throws NotFoundException;
    void update(Long id, UsuarioUpdateDTO dto) throws NotFoundException, BadRequestException;
}
