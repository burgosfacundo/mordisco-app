package utn.back.mordiscoapi.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.enums.*;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.PedidoMapper;
import utn.back.mordiscoapi.model.dto.pago.MercadoPagoPreferenceResponse;
import utn.back.mordiscoapi.model.dto.pedido.PedidoRequestDTO;
import utn.back.mordiscoapi.model.dto.pedido.PedidoResponseDTO;
import utn.back.mordiscoapi.model.entity.*;
import utn.back.mordiscoapi.repository.*;
import utn.back.mordiscoapi.security.jwt.utils.AuthUtils;
import utn.back.mordiscoapi.service.MercadoPagoService;
import utn.back.mordiscoapi.service.NotificacionService;
import utn.back.mordiscoapi.service.interf.IPedidoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private final PagoRepository pagoRepository;

    private final MercadoPagoService mercadoPagoService;
    private final NotificacionService notificacionService;
    private final ConfiguracionSistemaServiceImpl configuracionService;
    private final AuthUtils authUtils;

    /**
     * Guarda un pedido y gestiona el pago
     * @param dto DTORequest del pedido a guardar.
     * @return MercadoPagoPreferenceResponse si el tipo de pago es Mercado Pago, null si es efectivo
     * @throws NotFoundException si el restaurante, producto o dirección no existen
     * @throws BadRequestException si hay errores de validación
     */
    @Transactional
    @Override
    public MercadoPagoPreferenceResponse save(PedidoRequestDTO dto) throws NotFoundException, BadRequestException {
        Restaurante restaurante = restauranteRepository.findById(dto.idRestaurante())
                .orElseThrow(() -> new NotFoundException("El restaurante no existe"));

        Usuario cliente = usuarioRepository.findById(dto.idCliente())
                .orElseThrow(() -> new NotFoundException("El cliente no existe"));

        if (!restaurante.getActivo()) {
            throw new BadRequestException(
                    "El restaurante '" + restaurante.getRazonSocial() + "' no está aceptando pedidos en este momento"
            );
        }

        Pedido pedido = PedidoMapper.toEntity(dto);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setFechaHora(LocalDateTime.now());
        pedido.setRestaurante(restaurante);
        pedido.setCliente(cliente);

        BigDecimal total = calcularTotalYValidarProductos(pedido);
        pedido.setTotal(total);

        validarYAsignarDireccion(pedido, dto, restaurante, cliente);

        if (pedido.getDireccionEntrega() != null) {
            pedido.setDireccionSnapshot(pedido.getDireccionEntrega().toSnapshot());
        }


        pedido = pedidoRepository.save(pedido);

        Pago pago = crearRegistroPago(pedido, dto.metodoPago());
        pagoRepository.save(pago);

        if (dto.metodoPago() == MetodoPago.MERCADO_PAGO) {
            // Crear preferencia de pago en Mercado Pago
            MercadoPagoPreferenceResponse preference = mercadoPagoService.crearPreferenciaDePago(pedido);

            // Guardar preferenceId en el pago
            pago.setMercadoPagoPreferenceId(preference.preferenceId());
            pagoRepository.save(pago);

            return preference;

        } else if (dto.metodoPago() == MetodoPago.EFECTIVO) {
            // Para efectivo, notificar directamente al restaurante
            notificacionService.notificarNuevoPedidoARestaurante(pedido);

            // Retornar response sin preference (para que el front sepa que es efectivo)
            return new MercadoPagoPreferenceResponse(null, null, null,
                    pedido.getId());
        }
        return null;
    }


    /**
     * Lista todos los pedidos.
     * @return una lista de pedidos.
     */
    @Override
    public Page<PedidoResponseDTO> findAll(int pageNo,int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return pedidoRepository.findAll(pageable)
                .map(PedidoMapper::toDTO);
    }


    /**
     * Lista los pedidos por restaurante.
     * @param idRestaurante el ID del restaurante para listar sus pedidos.
     * @throws NotFoundException si el restaurante no se encuentra.
     */
    @Override
    public Page<PedidoResponseDTO> findAllByRestaurante_Id(int pageNo,int pageSize,Long idRestaurante) throws NotFoundException {
        if(restauranteRepository.findById(idRestaurante).isEmpty()){
            throw new NotFoundException("El restaurante no existe");
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return pedidoRepository.findAllByRestaurante_Id(pageable,idRestaurante)
                .map(PedidoMapper::toDTO);
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

        notificacionService.notificarCambioEstadoACliente(pedido);

        log.info("✅ Pedido #{} cambió de {} a {} y cliente notificado",
                id, estadoActual, nuevoEstado);

    }

    /**
     * Lista los pedidos de un cliente por el estado
     * @param idCliente el ID del cliente a buscar sus pedidos.
     * @param estado el EstadoPedido por el que hay que filtrar.
     * @throws NotFoundException si el cliente no se encuentra.
     */
    @Override
    public Page<PedidoResponseDTO> findAllByCliente_IdAndEstado(int pageNo,int pageSize,Long idCliente, EstadoPedido estado) throws NotFoundException {
        if (!usuarioRepository.existsById(idCliente)) {
            throw new NotFoundException("Usuario no encontrado");
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        return pedidoRepository.findAllByCliente_IdAndEstado(pageable,idCliente, estado)
                .map(PedidoMapper::toDTO);
    }

    /**
     * Lista todos los pedidos de un cliente
     * @param idCliente el ID del cliente a buscar sus pedidos.
     * @throws NotFoundException si el cliente no se encuentra.
     */
    @Override
    public Page<PedidoResponseDTO> findAllByCliente_Id(int pageNo,int pageSize,Long idCliente) throws NotFoundException {
        if (!usuarioRepository.existsById(idCliente)) {
            throw new NotFoundException("Usuario no encontrado");
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return pedidoRepository.findAllByCliente_Id(pageable,idCliente)
                .map(PedidoMapper::toDTO);
    }

    /**
     * Lista los pedidos de un Restaurante por el estado
     * @param idRestaurante el ID del Restaurante a buscar sus pedidos.
     * @param estado el EstadoPedido por el que hay que filtrar.
     * @throws NotFoundException si el Restaurante no se encuentra.
     *      * @throws BadRequestException si el estado del pedido no es válido.
     */
    @Override
    public Page<PedidoResponseDTO> findAllByRestaurante_IdAndEstado(int pageNo,int pageSize,Long idRestaurante, EstadoPedido estado) throws NotFoundException {
        if (!restauranteRepository.existsById(idRestaurante)) {
            throw new NotFoundException("Restaurante no encontrado");
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return pedidoRepository.findAllByRestaurante_IdAndEstado(pageable,idRestaurante, estado)
                .map(PedidoMapper::toDTO);
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

        notificacionService.notificarCambioEstadoACliente(pedido);

        log.info("❌ Pedido #{} cancelado", id);
    }


    private BigDecimal calcularTotalYValidarProductos(Pedido pedido) throws NotFoundException, BadRequestException {
        BigDecimal total = BigDecimal.ZERO;

        for (ProductoPedido item : pedido.getItems()) {
            item.setPedido(pedido);

            Producto producto = productoRepository.findById(item.getProducto().getId())
                    .orElseThrow(() -> new NotFoundException(
                            "El producto con ID " + item.getProducto().getId() + " no existe"
                    ));

            // Validar que el producto esté disponible
            if (!producto.getDisponible()) {
                throw new BadRequestException(
                        "El producto '" + producto.getNombre() + "' no está disponible"
                );
            }

            // Asignar precio actual del producto
            item.setPrecioUnitario(producto.getPrecio());

            // Calcular subtotal
            BigDecimal subtotal = producto.getPrecio()
                    .multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotal);
        }

        return total;
    }

    /**
     /**
     * Valida y asigna la dirección según el tipo de entrega
     */
    private void validarYAsignarDireccion(Pedido pedido, PedidoRequestDTO dto,
                                          Restaurante restaurante, Usuario cliente)
            throws NotFoundException, BadRequestException {

        if (pedido.getTipoEntrega().equals(TipoEntrega.DELIVERY)) {
            // Para DELIVERY, validar que venga idDireccion
            if (dto.idDireccion() == null) {
                throw new BadRequestException("Debe proporcionar una dirección para delivery");
            }

            Direccion direccion = direccionRepository.findById(dto.idDireccion())
                    .orElseThrow(() -> new NotFoundException("La dirección no existe"));

            // Validar que la dirección pertenezca al cliente
            if (!cliente.getDirecciones().contains(direccion)) {
                throw new BadRequestException("La dirección no pertenece al cliente");
            }

            // Validar que la dirección esté en la misma ciudad que el restaurante
            if (!direccion.getCiudad().equalsIgnoreCase(restaurante.getDireccion().getCiudad())) {
                throw new BadRequestException(
                        "No se puede realizar delivery a otra ciudad. " +
                                "Restaurante: " + restaurante.getDireccion().getCiudad() +
                                ", Dirección: " + direccion.getCiudad()
                );
            }

            pedido.setDireccionEntrega(direccion);

        } else if (pedido.getTipoEntrega().equals(TipoEntrega.RETIRO_POR_LOCAL)) {
            if (restaurante.getDireccion() == null) {
                throw new BadRequestException("El restaurante no tiene dirección configurada");
            }

            pedido.setDireccionEntrega(restaurante.getDireccion());
        }
    }


    /**
     * Crea el registro de pago inicial
     */
    private Pago crearRegistroPago(Pedido pedido, MetodoPago metodoPago) {
        return Pago.builder()
                .pedido(pedido)
                .metodoPago(metodoPago)
                .monto(pedido.getTotal())
                .estado(EstadoPago.PENDIENTE)
                .build();
    }

    @Transactional
    @Override
    public void asignarRepartidor(Long pedidoId, Long repartidorId)
            throws NotFoundException, BadRequestException {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado"));

        Usuario repartidor = usuarioRepository.findById(repartidorId)
                .orElseThrow(() -> new NotFoundException("Repartidor no encontrado"));

        // Validaciones
        if (!repartidor.isRepartidor()) {
            throw new BadRequestException("El usuario no es un repartidor");
        }

        if (!pedido.puedeSerAceptadoPorRepartidor()) {
            throw new BadRequestException(
                    "El pedido no puede ser asignado. Estado actual: " + pedido.getEstado()
            );
        }

        if (!TipoEntrega.DELIVERY.equals(pedido.getTipoEntrega())) {
            throw new BadRequestException("Solo los pedidos de delivery necesitan repartidor");
        }

        // Asignar repartidor
        pedido.setRepartidor(repartidor);
        pedido.setFechaAceptacionRepartidor(LocalDateTime.now());

        pedidoRepository.save(pedido);
    }

    @Transactional
    @Override
    public void marcarComoEntregado(Long pedidoId, Long repartidorId)
            throws NotFoundException, BadRequestException {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado"));

        // Validar que el repartidor sea el asignado
        if (pedido.getRepartidor() == null ||
                !pedido.getRepartidor().getId().equals(repartidorId)) {
            throw new BadRequestException("Este pedido no está asignado a ti");
        }

        // Validar estado
        if (pedido.getEstado() != EstadoPedido.EN_CAMINO) {
            throw new BadRequestException(
                    "Solo se pueden marcar como entregados pedidos EN_CAMINO"
            );
        }

        // Marcar como entregado
        pedido.setEstado(EstadoPedido.RECIBIDO);
        pedido.setFechaEntrega(LocalDateTime.now());

        pedidoRepository.save(pedido);


        // TODO: Notificar al cliente
        // TODO: Calcular ganancia del repartidor
    }

    @Override
    public Page<PedidoResponseDTO> getPedidosDisponiblesParaRepartidor(
            Double latitud, Double longitud, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        // Obtener radio máximo de entrega desde configuración
        BigDecimal radioMaximo = configuracionService.getRadioMaximoEntrega();

        // Query para pedidos EN_CAMINO sin repartidor asignado, cerca de la ubicación
        return pedidoRepository.findPedidosDisponiblesParaRepartidor(
                latitud, longitud, radioMaximo.doubleValue(), pageable
        ).map(PedidoMapper::toDTO);
    }

    @Override
    public PedidoResponseDTO getPedidoActivoRepartidor(Long id) throws NotFoundException, BadRequestException {
        var pedido = pedidoRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Pedido no encontrado")
        );

        var userAuthenticated = authUtils.getUsuarioAutenticado().orElseThrow(
                ()-> new NotFoundException("Usuario no encontrado")
        );

        if (!userAuthenticated.isRepartidor()) {
            throw new BadRequestException("Usuario no es repartidor");
        }

        if (!userAuthenticated.getId().equals(pedido.getRepartidor().getId())) {
            throw new BadRequestException("El pedido no te corresponde");
        }

        if (pedido.getEstado() != EstadoPedido.EN_CAMINO) {
            throw new BadRequestException("Pedido no esta activo");
        }

        return PedidoMapper.toDTO(pedido);
    }
}

