package utn.back.mordiscoapi.service.interf;

import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.pedido.PedidoDTORequest;
import utn.back.mordiscoapi.model.projection.PedidoProjection;

import java.util.List;

public interface IPedidoService {
    void save(PedidoDTORequest dto) throws BadRequestException;
    List<PedidoProjection> findAll();
    List<PedidoProjection> findAllCompleteByRestaurante(Long id) throws NotFoundException;
    PedidoProjection findById(Long id) throws NotFoundException;
    void delete(Long id) throws NotFoundException;
    void changeState(Long id, EstadoPedido nuevoEstado) throws NotFoundException, BadRequestException;
    List<PedidoProjection> findAllXClientesXEstado(Long id, EstadoPedido estado) throws NotFoundException;
    List<PedidoProjection> findAllXClientes(Long id) throws NotFoundException;
    List<PedidoProjection> findAllXRestauranteXEstado(Long id, EstadoPedido estado) throws NotFoundException;
    Long cantidadPedidosXEstado(Long id, EstadoPedido estado) throws NotFoundException;
}
