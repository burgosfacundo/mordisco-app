package utn.back.mordiscoapi.service.interf;

import org.springframework.data.domain.Page;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.common.exception.InternalServerErrorException;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.auth.RecoverPasswordDTO;
import utn.back.mordiscoapi.model.dto.auth.ResetPasswordDTO;
import utn.back.mordiscoapi.model.dto.pedido.PedidoResponseDTO;
import utn.back.mordiscoapi.model.dto.usuario.*;

public interface IUsuarioService {
    void save(UsuarioCreateDTO dto) throws NotFoundException;
    Page<UsuarioCardDTO> findAll(int pageNo, int pageSize);
    UsuarioResponseDTO findById(Long id) throws NotFoundException;
    UsuarioResponseDTO getMe() throws NotFoundException, BadRequestException;
    void delete(Long id) throws NotFoundException, BadRequestException;
    Page<PedidoResponseDTO> getPedidosActivosComoCliente(Long usuarioId, int page, int size)
            throws NotFoundException;
    void deleteMe() throws NotFoundException, BadRequestException;
    void changePassword(ChangePasswordDTO dto) throws NotFoundException, BadRequestException, InternalServerErrorException;
    void requestPasswordRecovery(RecoverPasswordDTO dto) throws NotFoundException, InternalServerErrorException, BadRequestException ;
    void resetPassword(ResetPasswordDTO dto) throws BadRequestException, NotFoundException;
    Page<UsuarioCardDTO> findByRolId(int pageNo,int pageSize,Long id) throws NotFoundException;
    void update(Long id, UsuarioUpdateDTO dto) throws NotFoundException, BadRequestException;
    void updateMe(UsuarioUpdateDTO dto) throws NotFoundException, BadRequestException;
}
