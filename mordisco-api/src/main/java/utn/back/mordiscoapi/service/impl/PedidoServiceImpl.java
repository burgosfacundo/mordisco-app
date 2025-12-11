package utn.back.mordiscoapi.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.enums.*;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.event.order.*;
import utn.back.mordiscoapi.mapper.PedidoMapper;
import utn.back.mordiscoapi.model.dto.pago.MercadoPagoPreferenceResponse;
import utn.back.mordiscoapi.model.dto.pedido.PedidoRequestDTO;
import utn.back.mordiscoapi.model.dto.pedido.PedidoResponseDTO;
import utn.back.mordiscoapi.model.entity.*;
import utn.back.mordiscoapi.repository.*;
import utn.back.mordiscoapi.security.jwt.utils.AuthUtils;
import utn.back.mordiscoapi.service.MercadoPagoService;
import utn.back.mordiscoapi.service.interf.IGananciaRepartidorService;
import utn.back.mordiscoapi.service.interf.IPedidoService;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final PinService pinService;

    private final MercadoPagoService mercadoPagoService;
    private final ConfiguracionSistemaServiceImpl configuracionService;
    private final AuthUtils authUtils;
    private final IGananciaRepartidorService gananciaRepartidorService;
    private final ApplicationEventPublisher eventPublisher;

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
        pedido.setPin(pinService.generarPin());
        BigDecimal total = calcularTotalYValidarProductos(pedido);
        System.out.println(total);
        pedido.setTotal(total);

        // Validar monto mínimo del pedido
        BigDecimal montoMinimo = configuracionService.getConfiguracionGeneralActual().montoMinimoPedido();
        if (total.compareTo(montoMinimo) < 0) {
            throw new BadRequestException(
                    String.format("El monto mínimo del pedido es $%.2f. Tu pedido es de $%.2f",
                            montoMinimo, total)
            );
        }


        validarYAsignarDireccion(pedido, dto, restaurante, cliente);

        // Calcular costo de delivery si es DELIVERY
        if (pedido.getTipoEntrega() == TipoEntrega.DELIVERY && pedido.getDireccionEntrega() != null) {
            // Calcular distancia entre restaurante y dirección de entrega
            BigDecimal distanciaKm = calcularDistancia(
                    restaurante.getDireccion().getLatitud(),
                    restaurante.getDireccion().getLongitud(),
                    pedido.getDireccionEntrega().getLatitud(),
                    pedido.getDireccionEntrega().getLongitud()
            );
            pedido.setDistanciaKm(distanciaKm);

            // Calcular costo de delivery basado en la distancia
            BigDecimal costoDelivery = configuracionService.calcularCostoDelivery(distanciaKm);
            pedido.setCostoDelivery(costoDelivery);

            // Guardar el subtotal de productos (sin delivery) para el restaurante
            pedido.setSubtotalProductos(total);

            // Actualizar el total para incluir el costo de delivery (para el cliente)
            // Total = Subtotal productos + Costo delivery
            pedido.setTotal(total.add(costoDelivery));
        } else {
            // Para RETIRO_POR_LOCAL, el total y subtotal son iguales
            pedido.setSubtotalProductos(total);
        }

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
            // Forzar carga de relaciones necesarias para eventos asíncronos
            restaurante.getUsuario().getId(); // Forzar carga del usuario del restaurante

            // Para efectivo, publicar evento de nuevo pedido
            eventPublisher.publishEvent(new PedidoCreatedEvent(pedido));

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

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("fechaHora").descending());
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
    @Transactional
    @Override
    public void changeState(Long id, EstadoPedido nuevoEstado)
            throws NotFoundException, BadRequestException {

        Pedido pedido = pedidoRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado"));

        EstadoPedido estadoActual = pedido.getEstado();

        // Validar que el nuevo estado sea diferente
        if (estadoActual == nuevoEstado) {
            throw new BadRequestException("El pedido ya se encuentra en ese estado");
        }

        if (!estadoActual.puedeCambiarA(nuevoEstado, pedido.getTipoEntrega())) {
            throw new BadRequestException(
                    String.format("No se puede cambiar de '%s' a '%s' para pedidos de tipo '%s'",
                            estadoActual.getDisplayName(),
                            nuevoEstado.getDisplayName(),
                            pedido.getTipoEntrega())
            );
        }

        validarCambioEstadoEspecifico(pedido, nuevoEstado);

        // Actualizar estado
        pedido.setEstado(nuevoEstado);
        pedidoRepository.save(pedido);

        // Notificar según el nuevo estado
        notificarCambioEstado(pedido, nuevoEstado);
    }

    /**
            * Validaciones específicas según el nuevo estado
     */
    private void validarCambioEstadoEspecifico(Pedido pedido, EstadoPedido nuevoEstado)
            throws BadRequestException {
        switch (nuevoEstado) {
            case EN_CAMINO -> {
                // Solo válido para DELIVERY
                if (pedido.getTipoEntrega() != TipoEntrega.DELIVERY) {
                    throw new BadRequestException(
                            "El estado 'EN_CAMINO' solo es válido para pedidos de delivery"
                    );
                }
                // Debe tener repartidor asignado
                if (pedido.getRepartidor() == null) {
                    throw new BadRequestException(
                            "No se puede marcar EN_CAMINO sin asignar un repartidor"
                    );
                }
            }
            case LISTO_PARA_RETIRAR -> {
                // Solo válido para RETIRO_POR_LOCAL
                if (pedido.getTipoEntrega() != TipoEntrega.RETIRO_POR_LOCAL) {
                    throw new BadRequestException(
                            "El estado 'LISTO_PARA_RETIRAR' solo es válido para pedidos de retiro por local"
                    );
                }
            }
            case ASIGNADO_A_REPARTIDOR -> {
                // Solo válido para DELIVERY
                if (pedido.getTipoEntrega() != TipoEntrega.DELIVERY) {
                    throw new BadRequestException(
                            "El estado 'PEDIDO_ASIGNADO_A_REPARTIDOR' solo es válido para pedidos de delivery"
                    );
                }
            }

            case LISTO_PARA_ENTREGAR -> {
                // Solo válido para DELIVERY
                if (pedido.getTipoEntrega() != TipoEntrega.DELIVERY) {
                    throw new BadRequestException(
                            "El estado 'LISTO_PARA_ENTREGAR' solo es válido para pedidos de delivery"
                    );
                }
            }
            case COMPLETADO -> {
                // Validar estado anterior correcto
                boolean estadoValidoParaCompletar = switch (pedido.getTipoEntrega()) {
                    case RETIRO_POR_LOCAL -> pedido.getEstado() == EstadoPedido.LISTO_PARA_RETIRAR;
                    case DELIVERY -> pedido.getEstado() == EstadoPedido.EN_CAMINO;
                };

                if (!estadoValidoParaCompletar) {
                    throw new BadRequestException(
                            String.format("No se puede completar un pedido de %s que está en estado %s",
                                    pedido.getTipoEntrega(), pedido.getEstado())
                    );
                }
            }
        }
    }


    private void notificarCambioEstado(Pedido pedido, EstadoPedido nuevoEstado) {
        switch (nuevoEstado) {
            case EN_PREPARACION -> eventPublisher.publishEvent(new PedidoEnPreparacionEvent(pedido));
            case LISTO_PARA_RETIRAR -> eventPublisher.publishEvent(new PedidoListoParaRetirarEvent(pedido));
            case LISTO_PARA_ENTREGAR -> eventPublisher.publishEvent(new PedidoListoParaEntregarEvent(pedido));
            case EN_CAMINO -> eventPublisher.publishEvent(new PedidoEnCaminoEvent(pedido));
            case COMPLETADO -> eventPublisher.publishEvent(new PedidoCompletadoEvent(pedido));
            case CANCELADO -> eventPublisher.publishEvent(new PedidoCanceladoEvent(pedido, "Cancelado por el sistema"));
        }
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

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("fechaHora").descending());

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

        Pageable pageable = PageRequest.of(pageNo, pageSize,Sort.by("fechaHora").descending());
        return pedidoRepository.findAllByCliente_Id(pageable,idCliente)
                .map(PedidoMapper::toDTO);
    }

    /**
     * Lista los pedidos de un Restaurante por el estado
     * @param idRestaurante el ID del Restaurante a buscar sus pedidos.
     * @param estado el EstadoPedido por el que hay que filtrar.
     * @throws NotFoundException si el Restaurante no se encuentra.
     */
    @Override
    public Page<PedidoResponseDTO> findAllByRestaurante_IdAndEstado(int pageNo,int pageSize,Long idRestaurante, EstadoPedido estado) throws NotFoundException {
        if (!restauranteRepository.existsById(idRestaurante)) {
            throw new NotFoundException("Restaurante no encontrado");
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("fechaHora").descending());
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
        var pedido = pedidoRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado"));

        EstadoPedido estadoActual = pedido.getEstado();

        if (!estadoActual.esCancelable()) {
            throw new BadRequestException("No se puede cancelar un pedido en estado " + estadoActual);
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoRepository.save(pedido);

        // Publicar evento de pedido cancelado
        eventPublisher.publishEvent(new PedidoCanceladoEvent(pedido, "Cancelado por el usuario"));
    }


    private BigDecimal calcularTotalYValidarProductos(Pedido pedido) throws NotFoundException, BadRequestException {
        BigDecimal total = BigDecimal.ZERO;

        for (ProductoPedido item : pedido.getItems()) {
            item.setPedido(pedido);

            Producto producto = productoRepository.findById(item.getProducto().getId())
                    .orElseThrow(() -> new NotFoundException(
                            "El producto con ID " + item.getProducto().getId() + " no existe"
                    ));

            // Validar que el producto pertenezca al restaurante del pedido
            if (!producto.getMenu().getRestaurante().getId().equals(pedido.getRestaurante().getId())) {
                throw new BadRequestException(
                        String.format(
                                "No se pueden agregar productos de diferentes restaurantes. " +
                                "El producto '%s' pertenece a '%s', pero el pedido es de '%s'.",
                                producto.getNombre(),
                                producto.getMenu().getRestaurante().getRazonSocial(),
                                pedido.getRestaurante().getRazonSocial()
                        )
                );
            }

            // Validar que el producto esté disponible
            if (!producto.getDisponible()) {
                throw new BadRequestException(
                        "El producto '" + producto.getNombre() + "' no está disponible"
                );
            }

            // Calcular subtotal
            BigDecimal subtotal = item.getPrecioUnitario()
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

            // Validar que ambas direcciones tengan coordenadas
            if (direccion.getLatitud() == null || direccion.getLongitud() == null) {
                throw new BadRequestException(
                        "La dirección de entrega no tiene coordenadas válidas. " +
                        "Por favor, actualiza la dirección o selecciona otra."
                );
            }

            if (restaurante.getDireccion().getLatitud() == null ||
                restaurante.getDireccion().getLongitud() == null) {
                throw new BadRequestException(
                        "El restaurante no tiene coordenadas configuradas. " +
                        "Por favor, contacta al soporte."
                );
            }

            // Calcular distancia entre restaurante y dirección de entrega
            BigDecimal distanciaKm = calcularDistancia(
                    restaurante.getDireccion().getLatitud(),
                    restaurante.getDireccion().getLongitud(),
                    direccion.getLatitud(),
                    direccion.getLongitud()
            );

            // Obtener radio máximo de entrega desde configuración
            BigDecimal radioMaximo = configuracionService.getConfiguracionGeneralActual().radioMaximoEntrega();

            // Validar que la distancia no exceda el radio máximo
            if (distanciaKm.compareTo(radioMaximo) > 0) {
                throw new BadRequestException(
                        String.format(
                                "La dirección está demasiado lejos del restaurante. " +
                                "Distancia: %.2f km. Máximo permitido: %.2f km.",
                                distanciaKm.doubleValue(),
                                radioMaximo.doubleValue()
                        )
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
        
        // Cambiar estado a EN_CAMINO cuando el repartidor acepta
        pedido.setEstado(EstadoPedido.ASIGNADO_A_REPARTIDOR);

        pedidoRepository.save(pedido);

}

    @Transactional
    @Override
    public void marcarComoEntregado(Long pedidoId, Long repartidorId)
            throws NotFoundException, BadRequestException {

        Pedido pedido = pedidoRepository.findByIdWithRelations(pedidoId)
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado"));

        // Validar que el repartidor sea el asignado
        if(pedido.getTipoEntrega().equals(TipoEntrega.DELIVERY)) {
            if (pedido.getRepartidor() == null ||
                    !pedido.getRepartidor().getId().equals(repartidorId)) {
                throw new BadRequestException("Este pedido no está asignado a ti");
            }
        }
        //Validar restaurante??
        // Validar estado
        if (pedido.getEstado() != EstadoPedido.EN_CAMINO && pedido.getEstado() != EstadoPedido.LISTO_PARA_RETIRAR) {
            throw new BadRequestException(
                    "Solo se pueden marcar como entregados pedidos EN_CAMINO o LISTO_PARA_RETIRAR"
            );
        }

        // Marcar como entregado
        pedido.setEstado(EstadoPedido.COMPLETADO);
        pedido.setFechaEntrega(LocalDateTime.now());

        pedidoRepository.save(pedido);

        // Registrar ganancia del repartidor
        if(pedido.getTipoEntrega().equals(TipoEntrega.DELIVERY)){
            gananciaRepartidorService.registrarGanancia(pedido);
        }
        eventPublisher.publishEvent(new PedidoCompletadoEvent(pedido));
    }

    @Override
    public Page<PedidoResponseDTO> getPedidosDisponiblesParaRepartidor(
            Double latitud, Double longitud, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        // Obtener radio máximo de entrega desde configuración
        BigDecimal radioMaximo = configuracionService.getConfiguracionGeneralActual().radioMaximoEntrega();

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

        if (pedido.getEstado().equals(EstadoPedido.EN_CAMINO) || pedido.getEstado().equals(EstadoPedido.ASIGNADO_A_REPARTIDOR)) {
            throw new BadRequestException("Pedido no esta activo");
        }

        return PedidoMapper.toDTO(pedido);
    }

    /**
     * Calcula la distancia entre dos puntos geográficos usando la fórmula de Haversine
     * @param lat1 Latitud del punto 1
     * @param lon1 Longitud del punto 1
     * @param lat2 Latitud del punto 2
     * @param lon2 Longitud del punto 2
     * @return Distancia en kilómetros
     */
    private BigDecimal calcularDistancia(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int RADIO_TIERRA_KM = 6371;

        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distancia = RADIO_TIERRA_KM * c;

        return BigDecimal.valueOf(distancia).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Cancelar pedido con motivo obligatorio
     */
    @Transactional
    @Override
    public void darDeBaja(Long pedidoId, String motivo) throws NotFoundException {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado"));

        pedido.setEstadoAntesDeCancelado(pedido.getEstado());
        pedido.setEstado(EstadoPedido.CANCELADO);
        pedido.setBajaLogica(true);
        pedido.setMotivoBaja(motivo);
        pedido.setFechaBaja(java.time.LocalDateTime.now());

        pedidoRepository.save(pedido);
    }

    /**
     * Anula cancelacion a un pedido con motivo
     */
    @Transactional
    @Override
    public void reactivar(Long pedidoId) throws NotFoundException {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado"));

        pedido.setEstado(pedido.getEstadoAntesDeCancelado());
        pedido.setEstadoAntesDeCancelado(null);
        pedido.setBajaLogica(false);
        pedido.setMotivoBaja(null);
        pedido.setFechaBaja(null);

        pedidoRepository.save(pedido);
    }

    /**
     * Busca los pedidos por filtro especifico
     */
    public Page<PedidoResponseDTO> filtrarPedidos(int pageNo,int pageSize,String estado, String tipoEntrega, String fechaInicio, String fechaFin, String variable){
        LocalDateTime ini = (fechaInicio == null || fechaInicio.isBlank())
                ? null
                : LocalDateTime.parse(fechaInicio);

        LocalDateTime fin = (fechaFin == null || fechaFin.isBlank())
                ? null
                : LocalDateTime.parse(fechaFin);
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        return pedidoRepository.filtrarPedidos(variable,estado,tipoEntrega, ini, fin, pageable).map(PedidoMapper::toDTO);
    }
    /**
     * Busca los pedidos de un restaurante por filtro especifico
     */
    @Override
    public Page<PedidoResponseDTO> filtrarPedidosRestaurantes(
            int pageNo, int pageSize,
            Long id,
            String estado,
            String tipoEntrega,
            String fechaInicio,
            String fechaFin,
            String variable) {

        LocalDateTime ini = (fechaInicio == null || fechaInicio.isBlank())
                ? null
                : LocalDateTime.parse(fechaInicio);

        LocalDateTime fin = (fechaFin == null || fechaFin.isBlank())
                ? null
                : LocalDateTime.parse(fechaFin);

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        return pedidoRepository.filtrarPedidosRestaurante(
                id, variable, estado, tipoEntrega, ini, fin, pageable
        ).map(PedidoMapper::toDTO);
    }
    /*
     * Busca los pedidos de un cliente por filtro especifico
     */
    @Override
    public Page<PedidoResponseDTO> filtrarPedidosCliente(
            int pageNo, int pageSize,
            Long id,
            String estado,
            String tipoEntrega,
            String fechaInicio,
            String fechaFin,
            String variable) {

        LocalDateTime ini = (fechaInicio == null || fechaInicio.isBlank())
                ? null
                : LocalDateTime.parse(fechaInicio);

        LocalDateTime fin = (fechaFin == null || fechaFin.isBlank())
                ? null
                : LocalDateTime.parse(fechaFin);

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return pedidoRepository.filtrarPedidosCliente(
                id,          // clienteId
                estado,      // estado
                variable,    // search
                tipoEntrega, // tipoEntrega
                ini,         // fechaInicio
                fin,         // fechaFin
                pageable     // pageable
        ).map(PedidoMapper::toDTO);
    }
    /**
     * Busca los pedidos de un repartidor por filtro especifico
     */
    @Override
    public Page<PedidoResponseDTO> filtrarPedidosRepartidor(int pageNo, int pageSize, Long id, String estado, String fechaInicio, String fechaFin, String variable)  {
        LocalDateTime ini = (fechaInicio == null || fechaInicio.isBlank())
                ? null
                : LocalDateTime.parse(fechaInicio);

        LocalDateTime fin = (fechaFin == null || fechaFin.isBlank())
                ? null
                : LocalDateTime.parse(fechaFin);
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        return pedidoRepository.filtrarPedidosRepartidores(id,variable,estado, ini, fin, pageable).map(PedidoMapper::toDTO);
    }
}

