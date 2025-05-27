package utn.back.mordiscoapi.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.PedidoMapper;
import utn.back.mordiscoapi.mapper.PromocionMapper;
import utn.back.mordiscoapi.model.dto.pedido.PedidoDTORequest;
import utn.back.mordiscoapi.model.dto.pedido.PedidoDTOResponse;
import utn.back.mordiscoapi.model.dto.productoPedido.ProductoPedidoDTO;
import utn.back.mordiscoapi.model.entity.*;
import utn.back.mordiscoapi.repository.PedidoRepository;
import utn.back.mordiscoapi.repository.ProductoRepository;
import utn.back.mordiscoapi.repository.RestauranteRepository;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.service.CrudService;

import java.time.LocalDate;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements CrudService<PedidoDTORequest, PedidoDTOResponse,Long> {

    private final PedidoRepository pedidoRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    /**
     * Guarda un pedido.
     * @param dto DTORequest del pedido a guardar.
     * @throws BadRequestException si hay un error al guardar el pedido.
     */

    @Override
    public void save(PedidoDTORequest dto) throws BadRequestException {
        if(!restauranteRepository.existsById(dto.idRestaurante())){
            throw new BadRequestException("El restaurante no existe");
        }
        for (ProductoPedidoDTO productoPedidoDTO : dto.productos()){
            if(productoRepository.findCompleteById(productoPedidoDTO.producto_id()).isEmpty()){
                throw new BadRequestException("El producto no existe");
            }
        }
        try {
            Pedido pedido = PedidoMapper.toEntity(dto);
            pedidoRepository.save(pedido);
        } catch (DataIntegrityViolationException e) {
            log.error(e.getMessage());
            throw new BadRequestException("Error al guardar el pedido");
        }
    }



    /**
     * Lista todos los pedidos.
     */

    @Override
    public List<PedidoDTOResponse> findAll() {
        return pedidoRepository.findAllDTO();
    }

    /**
     * Lista los pedidos por restaurante.
     * @param id el ID del restaurante para listar sus pedidos.
     * @throws NotFoundException si el restaurante no se encuentra.
     */

    public List<PedidoDTOResponse> findAllCompleteByRestaurante(Long id) throws BadRequestException {
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

    @Override
    public PedidoDTOResponse findById(Long id) throws NotFoundException {
        return pedidoRepository.findByProjectID(id).orElseThrow(
                () -> new NotFoundException("Usuario no encontrado")
        );
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
    public void changeState(Long id, EstadoPedido nuevoEstado) throws NotFoundException, BadRequestException {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Pedido no encontrado")
        );

        if(!pedido.getEstado().equals(nuevoEstado)){
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
     * @throws BadRequestException si hay un error al actualizar el pedido.
     */
    public List<PedidoDTOResponse> findAllXClientesXEstado(Long id, EstadoPedido estado) throws NotFoundException, BadRequestException {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Usuario no encontrado")
        );

        return pedidoRepository.findAllXClientesXEstado(id, estado);


    }
}

