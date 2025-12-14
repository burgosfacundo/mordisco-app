package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.mapper.CalificacionMapper;
import utn.back.mordiscoapi.mapper.PedidoMapper;
import utn.back.mordiscoapi.model.dto.calificaciones.*;
import utn.back.mordiscoapi.model.dto.pedido.PedidoResponseDTO;
import utn.back.mordiscoapi.model.entity.*;
import utn.back.mordiscoapi.repository.*;
import utn.back.mordiscoapi.security.jwt.utils.AuthUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalificacionServiceImpl {

    private final CalificacionPedidoRepository calificacionPedidoRepository;
    private final CalificacionRepartidorRepository calificacionRepartidorRepository;
    private final RestauranteRepository restauranteRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuthUtils authUtils;

    // ========== CALIFICACIÓN DE PEDIDO ==========

    /**
     * Crear calificación de pedido
     * Solo el cliente que hizo el pedido puede calificarlo
     * Solo se puede calificar pedidos en estado RECIBIDO
     */
    @Transactional
    public CalificacionPedidoResponseDTO calificarPedido(CalificacionPedidoRequestDTO dto)
            throws NotFoundException, BadRequestException {

        // Obtener usuario autenticado
        Usuario cliente = authUtils.getUsuarioAutenticado()
                .orElseThrow(() -> new BadRequestException("Usuario no autenticado"));

        // Obtener pedido
        Pedido pedido = pedidoRepository.findById(dto.pedidoId())
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado"));

        // Validaciones
        validarCalificacionPedido(pedido, cliente);

        // Crear calificación
        var calificacion = CalificacionMapper.toEntity(dto,pedido,cliente);

        calificacion = calificacionPedidoRepository.save(calificacion);

        return CalificacionMapper.toDTO(calificacion);
    }

    /**
     * Obtener calificación de un pedido
     */
    @Transactional(readOnly = true)
    public CalificacionPedidoResponseDTO getCalificacionPedido(Long pedidoId)
            throws NotFoundException {

        CalificacionPedido calificacion = calificacionPedidoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new NotFoundException("El pedido no tiene calificación"));

        return CalificacionMapper.toDTO(calificacion);
    }
        /**
     * Obtener calificacion de un repartidor
     */
    @Transactional(readOnly = true)
    public CalificacionRepartidorResponseDTO getCalificacionRepartidor(Long pedidoId)
            throws NotFoundException {

        CalificacionRepartidor calificacion = calificacionRepartidorRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new NotFoundException("El repartidor no tiene calificación"));

        return CalificacionMapper.toDTO(calificacion);
    }


    /**
     * Obtener calificaciones de un restaurante (desde sus pedidos)
     */
    @Transactional(readOnly = true)
    public Page<CalificacionPedidoResponseDTO> getCalificacionesRestaurante(
            Long restauranteId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return calificacionPedidoRepository.findByRestauranteId(restauranteId, pageable)
                .map(CalificacionMapper::toDTO);
    }

    /**
     * Obtener estadísticas de un restaurante
     */
    @Transactional(readOnly = true)
    public EstadisticasRestauranteDTO getEstadisticasRestaurante(Long restauranteId)
            throws NotFoundException {

        var restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new NotFoundException("Restaurante no encontrado"));

        Double promedioComida = calificacionPedidoRepository.calcularPromedioComidaRestaurante(restauranteId);
        Double promedioTiempo = calificacionPedidoRepository.calcularPromedioTiempoRestaurante(restauranteId);
        Double promedioPackaging = calificacionPedidoRepository.calcularPromedioPackagingRestaurante(restauranteId);
        Double promedio = calificacionPedidoRepository.calcularPromedioRestaurante(restauranteId);
        Long total = calificacionPedidoRepository.contarCalificacionesRestaurante(restauranteId);

        return new EstadisticasRestauranteDTO(
                restauranteId,
                restaurante.getRazonSocial(),
                promedioComida,
                promedioTiempo,
                promedioPackaging,
                promedio != null ? promedio : 0.0,
                total
        );
    }

    // ========== CALIFICACIÓN DE REPARTIDOR ==========

    /**
     * Crear calificación de repartidor
     * Solo el cliente que recibió el pedido puede calificar al repartidor
     */
    @Transactional
    public CalificacionRepartidorResponseDTO calificarRepartidor(
            CalificacionRepartidorRequestDTO dto)
            throws NotFoundException, BadRequestException {

        // Obtener usuario autenticado
        Usuario cliente = authUtils.getUsuarioAutenticado()
                .orElseThrow(() -> new BadRequestException("Usuario no autenticado"));

        // Obtener pedido
        Pedido pedido = pedidoRepository.findById(dto.pedidoId())
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado"));

        // Validaciones
        validarCalificacionRepartidor(pedido, cliente);

        // Crear calificación
        CalificacionRepartidor calificacion = CalificacionMapper.toEntity(dto,pedido,cliente);

        calificacion = calificacionRepartidorRepository.save(calificacion);

        return CalificacionMapper.toDTO(calificacion);
    }

    /**
     * Obtener calificaciones de un repartidor
     */
    @Transactional(readOnly = true)
    public Page<CalificacionRepartidorResponseDTO> getCalificacionesRepartidor(
            Long repartidorId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return calificacionRepartidorRepository.findByRepartidorIdOrderByFechaHoraDesc(
                repartidorId, pageable
        ).map(CalificacionMapper::toDTO);
    }

    /**
     * Obtener estadísticas de un repartidor
     */
    @Transactional(readOnly = true)
    public EstadisticasRepartidorDTO getEstadisticasRepartidor(Long repartidorId)
            throws NotFoundException {
        var repartidor = usuarioRepository.findById(repartidorId)
                .orElseThrow(() -> new NotFoundException("Repartidor no encontrado"));

        Double promedioAtencion = calificacionRepartidorRepository.calcularPromedioAtencionRepartidor(repartidorId);
        Double promedioComunicacion = calificacionRepartidorRepository.calcularPromedioComunicacionRepartidor(repartidorId);
        Double promedioProfesionalismo =calificacionRepartidorRepository.calcularPromedioProfesionalismoRepartidor(repartidorId);
        Double promedio = calificacionRepartidorRepository.calcularPromedioRepartidor(repartidorId);
        Long total = calificacionRepartidorRepository.countByRepartidorId(repartidorId);

        return new EstadisticasRepartidorDTO(
                repartidorId,
                repartidor.getNombre(),
                promedioAtencion,
                promedioComunicacion,
                promedioProfesionalismo,
                promedio != null ? promedio : 0.0,
                total
        );
    }

    /**
     * Eliminar calificación de pedido (solo el autor)
     */
    @Transactional
    public void eliminarCalificacionPedido(Long calificacionId)
            throws NotFoundException, BadRequestException {

        Usuario usuario = authUtils.getUsuarioAutenticado()
                .orElseThrow(() -> new BadRequestException("Usuario no autenticado"));

        CalificacionPedido calificacion = calificacionPedidoRepository.findById(calificacionId)
                .orElseThrow(() -> new NotFoundException("Calificación no encontrada"));

        if (!calificacion.getUsuario().getId().equals(usuario.getId())) {
            throw new BadRequestException("No puedes eliminar una calificación que no es tuya");
        }

        calificacionPedidoRepository.delete(calificacion);
    }

    /**
     * Eliminar calificación de repartidor (solo el autor)
     */
    @Transactional
    public void eliminarCalificacionRepartidor(Long calificacionId)
            throws NotFoundException, BadRequestException {

        Usuario usuario = authUtils.getUsuarioAutenticado()
                .orElseThrow(() -> new BadRequestException("Usuario no autenticado"));

        CalificacionRepartidor calificacion = calificacionRepartidorRepository.findById(calificacionId)
                .orElseThrow(() -> new NotFoundException("Calificación no encontrada"));

        if (!calificacion.getUsuario().getId().equals(usuario.getId())) {
            throw new BadRequestException("No puedes eliminar una calificación que no es tuya");
        }

        calificacionRepartidorRepository.delete(calificacion);
    }

    // ========== VALIDACIONES ==========

    private void validarCalificacionPedido(Pedido pedido, Usuario cliente)
            throws BadRequestException {

        // Verificar que el pedido esté en estado COMPLETADO
        if (pedido.getEstado() != EstadoPedido.COMPLETADO) {
            throw new BadRequestException(
                    "Solo puedes calificar pedidos que ya fueron recibidos"
            );
        }

        // Verificar que sea el cliente del pedido
        if (!pedido.getCliente().getId().equals(cliente.getId())) {
            throw new BadRequestException("Solo puedes calificar tus propios pedidos");
        }

        // Verificar que no haya sido calificado antes
        if (calificacionPedidoRepository.existsByPedidoId(pedido.getId())) {
            throw new BadRequestException("Este pedido ya fue calificado");
        }
    }

    private void validarCalificacionRepartidor(Pedido pedido, Usuario cliente)
            throws BadRequestException {

        // Verificar que el pedido esté en estado COMPLETADO
        if (pedido.getEstado() != EstadoPedido.COMPLETADO) {
            throw new BadRequestException(
                    "Solo puedes calificar el repartidor de pedidos ya recibidos"
            );
        }

        // Verificar que sea el cliente del pedido
        if (!pedido.getCliente().getId().equals(cliente.getId())) {
            throw new BadRequestException("Solo puedes calificar repartidores de tus pedidos");
        }

        // Verificar que el pedido tenga repartidor asignado
        if (pedido.getRepartidor() == null) {
            throw new BadRequestException("Este pedido no tuvo repartidor asignado");
        }

        // Verificar que no haya sido calificado antes
        if (calificacionRepartidorRepository.existsByPedidoId(pedido.getId())) {
            throw new BadRequestException("Ya calificaste al repartidor de este pedido");
        }
    }

    @Transactional(readOnly = true)
    public Page<CalificacionPedidoResponseDTO> getCalificacionesPedidosCliente(
            Long clienteId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return calificacionPedidoRepository.findCalificacionesRealizadasPorCliente(clienteId, pageable)
                .map(CalificacionMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<CalificacionRepartidorResponseDTO> getCalificacionesRepartidoresCliente(
            Long clienteId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return calificacionRepartidorRepository.findCalificacionesRealizadasPorCliente(clienteId, pageable)
                .map(CalificacionMapper::toDTO);
    }

    /**
     * Busca los pedidos por filtro especifico
     */
    public Page<CalificacionPedidoResponseDTO> filtrarCalificaciones(int pageNo, int pageSize, String estrellas, String fechaInicio, String fechaFin, String variable){
        LocalDateTime ini = (fechaInicio == null || fechaInicio.isBlank())
                ? null
                : LocalDateTime.parse(fechaInicio);

        LocalDateTime fin = (fechaFin == null || fechaFin.isBlank())
                ? null
                : LocalDateTime.parse(fechaFin);
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        return calificacionPedidoRepository.filtrarCalificaciones(variable,estrellas, ini, fin, pageable).map(CalificacionMapper::toDTO);
    }
}