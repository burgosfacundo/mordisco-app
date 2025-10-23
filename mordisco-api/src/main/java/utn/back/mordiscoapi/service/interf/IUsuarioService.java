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
    UsuarioResponseDTO getMe() throws NotFoundException, BadRequestException;
    void delete(Long id) throws NotFoundException;
    void deleteMe() throws NotFoundException, BadRequestException;
    void changePassword(ChangePasswordDTO dto) throws NotFoundException, BadRequestException;
    List<UsuarioResponseDTO> findByRolId(Long id) throws NotFoundException;
    void update(Long id, UsuarioUpdateDTO dto) throws NotFoundException, BadRequestException;
    UsuarioResponseDTO updateMe(UsuarioUpdateDTO dto) throws NotFoundException, BadRequestException;
}
