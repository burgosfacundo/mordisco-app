package utn.back.mordiscoapi.service.interf;

import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.usuario.ChangePasswordDTO;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioCreateDTO;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioResponseDTO;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioUpdateDTO;

import java.util.List;

public interface IUsuarioService {
    void save(UsuarioCreateDTO dto) throws NotFoundException;
    List<UsuarioResponseDTO> findAll();
    UsuarioResponseDTO findById(Long id) throws NotFoundException;
    void delete(Long id) throws NotFoundException;
    void changePassword(Long id, ChangePasswordDTO dto) throws NotFoundException;
    List<UsuarioResponseDTO> findByRolId(Long id) throws NotFoundException;
    void update(Long id, UsuarioUpdateDTO dto) throws NotFoundException, BadRequestException;
}
