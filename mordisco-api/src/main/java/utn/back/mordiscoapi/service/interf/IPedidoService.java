package utn.back.mordiscoapi.service.interf;

import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.pedido.PedidoRequestDTO;
import utn.back.mordiscoapi.model.dto.pedido.PedidoResponseDTO;

import java.util.List;

public interface IPedidoService {
    void save(PedidoRequestDTO dto) throws NotFoundException, BadRequestException;
    List<PedidoResponseDTO> findAll();
    List<PedidoResponseDTO> findAllByRestaurante_Id(Long idRestaurante) throws NotFoundException;
    PedidoResponseDTO findById(Long id) throws NotFoundException;
    void delete(Long id) throws NotFoundException;
    void changeState(Long id, EstadoPedido nuevoEstado) throws NotFoundException, BadRequestException;
    List<PedidoResponseDTO> findAllByCliente_IdAndEstado(Long idCliente, EstadoPedido estado) throws NotFoundException;
    List<PedidoResponseDTO> findAllByCliente_Id(Long idCliente) throws NotFoundException;
    List<PedidoResponseDTO> findAllByRestaurante_IdAndEstado(Long idRestaurante, EstadoPedido estado) throws NotFoundException;
    void cancelarPedido(Long id) throws NotFoundException, BadRequestException;
}
