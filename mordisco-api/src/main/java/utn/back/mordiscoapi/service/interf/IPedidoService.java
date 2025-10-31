package utn.back.mordiscoapi.service.interf;

import org.springframework.data.domain.Page;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.pedido.PedidoRequestDTO;
import utn.back.mordiscoapi.model.dto.pedido.PedidoResponseDTO;

public interface IPedidoService {
    void save(PedidoRequestDTO dto) throws NotFoundException, BadRequestException;
    Page<PedidoResponseDTO> findAll(int pageNo, int pageSize);
    Page<PedidoResponseDTO> findAllByRestaurante_Id(int pageNo,int pageSize,Long idRestaurante) throws NotFoundException;
    PedidoResponseDTO findById(Long id) throws NotFoundException;
    void delete(Long id) throws NotFoundException;
    void changeState(Long id, EstadoPedido nuevoEstado) throws NotFoundException, BadRequestException;
    Page<PedidoResponseDTO> findAllByCliente_IdAndEstado(int pageNo,int pageSize,Long idCliente, EstadoPedido estado) throws NotFoundException;
    Page<PedidoResponseDTO> findAllByCliente_Id(int pageNo,int pageSize,Long idCliente) throws NotFoundException;
    Page<PedidoResponseDTO> findAllByRestaurante_IdAndEstado(int pageNo,int pageSize,Long idRestaurante, EstadoPedido estado) throws NotFoundException;
    void cancelarPedido(Long id) throws NotFoundException, BadRequestException;
}
