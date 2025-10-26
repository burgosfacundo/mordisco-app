package utn.back.mordiscoapi.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.enums.TipoEntrega;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.PedidoMapper;
import utn.back.mordiscoapi.model.dto.pedido.PedidoRequestDTO;
import utn.back.mordiscoapi.model.dto.pedido.PedidoResponseDTO;
import utn.back.mordiscoapi.model.entity.*;
import utn.back.mordiscoapi.model.projection.ProductoProjection;
import utn.back.mordiscoapi.repository.*;
import utn.back.mordiscoapi.service.interf.IPedidoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements IPedidoService {
    private final DireccionRepository direccionRepository;
    private final PedidoRepository pedidoRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Guarda un pedido.
     * @param dto DTORequest del pedido a guardar.
     * @throws NotFoundException si el restaurante, producto o la dirección no existen,
     */
    @Transactional
    @Override
    public void save(PedidoRequestDTO dto) throws NotFoundException, BadRequestException {
       var restaurante = restauranteRepository.findById(dto.idRestaurante())
                .orElseThrow(() -> new NotFoundException("El restaurante no existe"));

        var usuario = usuarioRepository.findById(dto.idCliente())
                .orElseThrow(() -> new NotFoundException("El cliente no existe"));

        Pedido pedido = PedidoMapper.toEntity(dto);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setFechaHora(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;

        for (ProductoPedido item : pedido.getItems()) {
            item.setPedido(pedido);

            Optional<ProductoProjection> p = productoRepository.findCompleteById(item.getProducto().getId());
            if (p.isPresent()) {
                item.setPrecioUnitario(p.get().getPrecio());
                BigDecimal subtotal = p.get().getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
                total = total.add(subtotal);
            } else {
                throw new NotFoundException("El producto con ID " + item.getProducto().getId() + " no existe");
            }
        }

        Optional<Direccion> direccionOpt = direccionRepository.findById(dto.idDireccion());
        if (direccionOpt.isEmpty()) {
            throw new NotFoundException("La dirección no existe");
        }
        var direccion = direccionOpt.get();

        if (pedido.getTipoEntrega().equals(TipoEntrega.DELIVERY)){
            if (!usuario.getDirecciones().contains(direccion)) {
                throw new BadRequestException("La dirección no pertenece al cliente");
            }
            pedido.setDireccionEntrega(direccion);
        } else if (pedido.getTipoEntrega().equals(TipoEntrega.RETIRO_POR_LOCAL)) {
            if (!restaurante.getDireccion().equals(direccion)) {
                throw new BadRequestException("La dirección no pertenece al restaurante");
            }
            pedido.setDireccionEntrega(direccion);
        }

        String ciudad = direccion.getCiudad();

        pedido.setTotal(total);
        pedidoRepository.save(pedido);
    }


    /**
     * Lista todos los pedidos.
     * @return una lista de pedidos.
     */
    @Override
    public List<PedidoResponseDTO> findAll() {
        return pedidoRepository.findAll().stream()
                .map(PedidoMapper::toDTO)
                .toList();
    }


    /**
     * Lista los pedidos por restaurante.
     * @param idRestaurante el ID del restaurante para listar sus pedidos.
     * @throws NotFoundException si el restaurante no se encuentra.
     */
    @Override
    public List<PedidoResponseDTO> findAllByRestaurante_Id(Long idRestaurante) throws NotFoundException {
        if(restauranteRepository.findById(idRestaurante).isEmpty()){
            throw new NotFoundException("El restaurante no existe");
        }
        return pedidoRepository.findAllByRestaurante_Id(idRestaurante).stream()
                .map(PedidoMapper::toDTO)
                .toList();
    }

    /**
     * Busca un pedido por su ID.
     * @param id el ID del pedido a buscar.
     * @throws NotFoundException si el pedido no se encuentra.
     */
    @Override
    public PedidoResponseDTO findById(Long id) throws NotFoundException {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        if (pedido.isEmpty()) {
            throw new NotFoundException("Pedido no encontrado");
        }
        return PedidoMapper.toDTO(pedido.get());
    }

    /**
     * Elimina un pedido por su ID.
     * @param id el ID del pedido a eliminar.
     * @throws NotFoundException si el pedido no se encuentra.
     */
    @Override
    public void delete(Long id) throws NotFoundException {
        if(!pedidoRepository.existsById(id)){
            throw new NotFoundException("Pedido no encontrado");
        }
        pedidoRepository.deleteById(id);
    }

    /**
     * Cambia el estado del pedido.
     * @param id el ID del pedido a actualizar.
     * @param nuevoEstado nuevo estado.
     * @throws NotFoundException si el pedido no se encuentra.
     * @throws BadRequestException si hay un error al actualizar el pedido.
     */
    @Override
    public void changeState(Long id, EstadoPedido nuevoEstado) throws NotFoundException, BadRequestException {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado"));

        EstadoPedido estadoActual = pedido.getEstado();

        if (estadoActual == nuevoEstado) {
            throw new BadRequestException("El pedido ya se encuentra en ese estado");
        }

        if (!estadoActual.puedeCambiarA(nuevoEstado)) {
            throw new BadRequestException("No se puede cambiar de " + estadoActual + " a " + nuevoEstado);
        }

        if (nuevoEstado == EstadoPedido.CANCELADO && !estadoActual.esCancelable()) {
            throw new BadRequestException("No se puede cancelar un pedido en estado " + estadoActual);
        }

        pedido.setEstado(nuevoEstado);
        pedidoRepository.changeState(id, nuevoEstado);
    }

    /**
     * Lista los pedidos de un cliente por el estado
     * @param idCliente el ID del cliente a buscar sus pedidos.
     * @param estado el EstadoPedido por el que hay que filtrar.
     * @throws NotFoundException si el cliente no se encuentra.
     */
    @Override
    public List<PedidoResponseDTO> findAllByCliente_IdAndEstado(Long idCliente, EstadoPedido estado) throws NotFoundException {
        if (!usuarioRepository.existsById(idCliente)) {
            throw new NotFoundException("Usuario no encontrado");
        }

        return pedidoRepository.findAllByCliente_IdAndEstado(idCliente, estado).stream()
                .map(PedidoMapper::toDTO)
                .toList();
    }

    /**
     * Lista todos los pedidos de un cliente
     * @param idCliente el ID del cliente a buscar sus pedidos.
     * @throws NotFoundException si el cliente no se encuentra.
     */
    @Override
    public List<PedidoResponseDTO> findAllByCliente_Id(Long idCliente) throws NotFoundException {
        if (!usuarioRepository.existsById(idCliente)) {
            throw new NotFoundException("Usuario no encontrado");
        }
        return pedidoRepository.findAllByCliente_Id(idCliente).stream()
                .map(PedidoMapper::toDTO)
                .toList();
    }

    /**
     * Lista los pedidos de un Restaurante por el estado
     * @param idRestaurante el ID del Restaurante a buscar sus pedidos.
     * @param estado el EstadoPedido por el que hay que filtrar.
     * @throws NotFoundException si el Restaurante no se encuentra.
     *      * @throws BadRequestException si el estado del pedido no es válido.
     */
    @Override
    public List<PedidoResponseDTO> findAllByRestaurante_IdAndEstado(Long idRestaurante, EstadoPedido estado) throws NotFoundException {
        if (!restauranteRepository.existsById(idRestaurante)) {
            throw new NotFoundException("Restaurante no encontrado");
        }

        return pedidoRepository.findAllByRestaurante_IdAndEstado(idRestaurante, estado).stream()
                .map(PedidoMapper::toDTO)
                .toList();
    }

    /**
     * Cancela un pedido por su ID.
     * @param id el ID del pedido a cancelar.
     * @throws NotFoundException si el pedido no se encuentra.
     */
    @Override
    public void cancelarPedido(Long id) throws NotFoundException, BadRequestException {
        var pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado"));

        EstadoPedido estadoActual = pedido.getEstado();

        if (!estadoActual.esCancelable()) {
            throw new BadRequestException("No se puede cancelar un pedido en estado " + estadoActual);
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoRepository.save(pedido);
    }
}

