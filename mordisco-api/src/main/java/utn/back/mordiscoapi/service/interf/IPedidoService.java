package utn.back.mordiscoapi.service.interf;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.pago.MercadoPagoPreferenceResponse;
import utn.back.mordiscoapi.model.dto.pedido.PedidoRequestDTO;
import utn.back.mordiscoapi.model.dto.pedido.PedidoResponseDTO;

public interface IPedidoService {
    MercadoPagoPreferenceResponse save(PedidoRequestDTO dto) throws NotFoundException, BadRequestException;
    Page<PedidoResponseDTO> findAll(int pageNo, int pageSize);
    Page<PedidoResponseDTO> findAllByRestaurante_Id(int pageNo,int pageSize,Long idRestaurante) throws NotFoundException;
    PedidoResponseDTO findById(Long id) throws NotFoundException;
    void delete(Long id) throws NotFoundException;
    void changeState(Long id, EstadoPedido nuevoEstado) throws NotFoundException, BadRequestException;
    Page<PedidoResponseDTO> findAllByCliente_IdAndEstado(int pageNo,int pageSize,Long idCliente, EstadoPedido estado) throws NotFoundException;
    Page<PedidoResponseDTO> findAllByCliente_Id(int pageNo,int pageSize,Long idCliente) throws NotFoundException;
    Page<PedidoResponseDTO> findAllByRestaurante_IdAndEstado(int pageNo,int pageSize,Long idRestaurante, EstadoPedido estado) throws NotFoundException;
    void cancelarPedido(Long id) throws NotFoundException, BadRequestException;

    @Transactional
    void asignarRepartidor(Long pedidoId, Long repartidorId)
            throws NotFoundException, BadRequestException;

    @Transactional
    void marcarComoEntregado(Long pedidoId, Long repartidorId)
            throws NotFoundException, BadRequestException;

    Page<PedidoResponseDTO> getPedidosDisponiblesParaRepartidor(
            Double latitud, Double longitud, int page, int size);

    PedidoResponseDTO getPedidoActivoRepartidor(Long id) throws NotFoundException, BadRequestException;
}
