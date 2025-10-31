package utn.back.mordiscoapi.service.interf;

import org.springframework.data.domain.Page;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.usuario.*;

public interface IUsuarioService {
    void save(UsuarioCreateDTO dto) throws NotFoundException;
    Page<UsuarioCardDTO> findAll(int pageNo, int pageSize);
    UsuarioResponseDTO findById(Long id) throws NotFoundException;
    UsuarioResponseDTO getMe() throws NotFoundException, BadRequestException;
    void delete(Long id) throws NotFoundException;
    void deleteMe() throws NotFoundException, BadRequestException;
    void changePassword(ChangePasswordDTO dto) throws NotFoundException, BadRequestException;
    Page<UsuarioCardDTO> findByRolId(int pageNo,int pageSize,Long id) throws NotFoundException;
    void update(Long id, UsuarioUpdateDTO dto) throws NotFoundException, BadRequestException;
    UsuarioResponseDTO updateMe(UsuarioUpdateDTO dto) throws NotFoundException, BadRequestException;
}
