package utn.back.mordiscoapi.service.interf;

import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.UsuarioDTO;
import utn.back.mordiscoapi.model.projection.UsuarioProjection;
import utn.back.mordiscoapi.service.CrudService;

import java.util.List;

public interface IUsuarioService extends CrudService<UsuarioDTO, UsuarioProjection, Long> {
    void changePassword(String oldPassword, String newPassword, Long id) throws NotFoundException, BadRequestException;
    List<UsuarioProjection> findByProjectRol(Long id) ;
}
