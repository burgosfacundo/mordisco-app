package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.PedidoMapper;
import utn.back.mordiscoapi.model.dto.pedido.PedidoDTORequest;
import utn.back.mordiscoapi.model.entity.*;
import utn.back.mordiscoapi.model.projection.PedidoProjection;
import utn.back.mordiscoapi.model.projection.ProductoProjection;
import utn.back.mordiscoapi.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl{

    private final PedidoRepository pedidoRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    /**
     * Guarda un pedido.
     * @param dto DTORequest del pedido a guardar.
     * @throws BadRequestException si hay un error al guardar el pedido.
     */
    public void save(PedidoDTORequest dto) throws BadRequestException {
        if(!restauranteRepository.existsById(dto.idRestaurante())){
            throw new BadRequestException("El restaurante no existe");
        }

        try {
            Pedido pedido = PedidoMapper.toEntity(dto);
            pedido.setEstado(EstadoPedido.PENDIENTE);
            pedido.setFechaHora(LocalDateTime.now());

            BigDecimal total = BigDecimal.ZERO;

            for (ProductoPedido item : pedido.getItems()) {
                item.setPedido(pedido);

                Optional<ProductoProjection> p = productoRepository.findCompleteById(item.getProducto().getId());
                if (p.isPresent()) {
                    item.setPrecioUnitario(p.get().getPrecio());
                    var subtotal = p.get().getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
                    total = total.add(subtotal);
                } else {
                    throw new BadRequestException("El producto con ID " + item.getProducto().getId() + " no existe");
                }
            }

            pedido.setTotal(total);
            pedidoRepository.save(pedido);
        } catch (DataIntegrityViolationException e) {
            log.error(e.getMessage());
            throw new BadRequestException("Error al guardar el pedido");
        }
    }

    /**
     * Lista todos los pedidos.
     */

    public List<PedidoProjection> findAll() {
        return pedidoRepository.findAllDTO();
    }


    /**
     * Lista los pedidos por restaurante.
     * @param id el ID del restaurante para listar sus pedidos.
     * @throws NotFoundException si el restaurante no se encuentra.
     */
    public List<PedidoProjection> findAllCompleteByRestaurante(Long id) throws BadRequestException {
        if(restauranteRepository.findById(id).isEmpty()){
            throw new BadRequestException("El restaurante no existe");
        }
        return pedidoRepository.findAllCompleteByRestaurante(id);
    }

    /**
     * Busca un pedido por su ID.
     * @param id el ID del pedido a buscar.
     * @throws NotFoundException si el pedido no se encuentra.
     */
    public List<PedidoProjection> findById(Long id) throws NotFoundException {
        return pedidoRepository.findByProjectID(id);
    }

    /**
     * Elimina un pedido por su ID.
     * @param id el ID del pedido a eliminar.
     * @throws NotFoundException si el pedido no se encuentra.
     */
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
    public void changeState(Long id, EstadoPedido nuevoEstado) throws NotFoundException, BadRequestException {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Pedido no encontrado")
        );

        if(pedido.getEstado().equals(nuevoEstado)){
            throw new BadRequestException("El pedido ya se encuentra en ese estado");
        }

        try{
            pedido.setEstado(nuevoEstado);
            pedidoRepository.changeState(id, nuevoEstado);
        }catch (DataIntegrityViolationException e){
            log.error(e.getMessage());
            throw new BadRequestException("Error al guardar pedido");
        }
    }

    /**
     * Lista los pedidos de un cliente por el estado
     * @param id el ID del cliente a buscar sus pedidos.
     * @param estado el EstadoPedido por el que  hay que filtrar.
     * @throws NotFoundException si el cliente no se encuentra.
     */
    public List<PedidoProjection> findAllXClientesXEstado(Long id, EstadoPedido estado) throws NotFoundException {
        usuarioRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Usuario no encontrado")
        );
        return pedidoRepository.findAllXClientesXEstado(id, estado);
    }

    /**
     * Lista todos los pedidos de un cliente
     * @param id el ID del cliente a buscar sus pedidos.
     * @throws NotFoundException si el cliente no se encuentra.
     */
    public List<PedidoProjection> findAllXClientes(Long id) throws NotFoundException {
        usuarioRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Usuario no encontrado")
        );
        return pedidoRepository.findAllXClientes(id);
    }

    /**
     * Lista los pedidos de un Restaurante por el estado
     * @param id el ID del Restaurante a buscar sus pedidos.
     * @param estado el EstadoPedido por el que  hay que filtrar.
     * @throws NotFoundException si el Restaurante no se encuentra.
     */
    public List<PedidoProjection> findAllXRestauranteXEstado(Long id, EstadoPedido estado) throws NotFoundException {
        restauranteRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Restaurante no encontrado")
        );
        return pedidoRepository.findAllXRestauranteXEstado(id, estado);
    }

    /**
     * Cantidad de pedidos de un Restaurante por el estado
     * @param id el ID del Restaurante a buscar sus pedidos.
     * @param estado el EstadoPedido por el que  hay que filtrar.
     * @throws NotFoundException si el Restaurante no se encuentra.
     */

    public Optional<Long> cantidadPedidosXEstado(Long id, EstadoPedido estado) throws NotFoundException {
        restauranteRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Restaurante no encontrado")
        );
        return pedidoRepository.cantidadPedidosXEstado(id, estado);
    }
}

