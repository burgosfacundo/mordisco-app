package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.ConfiguracionSistemaMapper;
import utn.back.mordiscoapi.model.dto.configuracion.ConfiguracionSistemaRequestDTO;
import utn.back.mordiscoapi.model.dto.configuracion.ConfiguracionSistemaResponseDTO;
import utn.back.mordiscoapi.model.entity.ConfiguracionSistema;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.repository.ConfiguracionSistemaRepository;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.service.interf.IConfiguracionSistemaService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfiguracionSistemaServiceImpl implements IConfiguracionSistemaService {

    private final ConfiguracionSistemaRepository configuracionRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Obtiene la configuración actual del sistema
     * Si no existe, crea una con valores por defecto
     */
    @Override
    public ConfiguracionSistemaResponseDTO getConfiguracionActual() {
        ConfiguracionSistema config = configuracionRepository.findConfiguracionActual()
                .orElseGet(this::crearConfiguracionPorDefecto);

        return ConfiguracionSistemaMapper.toDTO(config);
    }

    /**
     * Actualiza la configuración del sistema
     * Solo puede haber una configuración activa
     */
    @Transactional
    @Override
    public ConfiguracionSistemaResponseDTO actualizarConfiguracion(
            ConfiguracionSistemaRequestDTO dto,
            Long usuarioId) throws NotFoundException {

        // Obtener configuración actual o crear una nueva
        ConfiguracionSistema config = configuracionRepository.findConfiguracionActual()
                .orElseGet(ConfiguracionSistema::new);

        // Obtener usuario que realiza la modificación
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        // Aplicar cambios
        ConfiguracionSistemaMapper.applyUpdate(dto, config);
        config.setUsuarioModificacion(usuario);
        config.setFechaActualizacion(LocalDateTime.now());

        // Guardar
        config = configuracionRepository.save(config);

        log.info("✅ Configuración del sistema actualizada por usuario #{} ({})",
                usuarioId, usuario.getEmail());

        return ConfiguracionSistemaMapper.toDTO(config);
    }

    /**
     * Calcula el costo de delivery basado en la distancia
     */
    @Override
    public BigDecimal calcularCostoDelivery(BigDecimal distanciaKm) {
        ConfiguracionSistema config = configuracionRepository.findConfiguracionActual()
                .orElseGet(this::crearConfiguracionPorDefecto);

        return config.calcularCostoDelivery(distanciaKm);
    }

    /**
     * Obtiene el monto mínimo de pedido configurado
     */
    @Override
    public BigDecimal getMontoMinimoPedido() {
        ConfiguracionSistema config = configuracionRepository.findConfiguracionActual()
                .orElseGet(this::crearConfiguracionPorDefecto);

        return config.getMontoMinimoPedido();
    }

    /**
     * Obtiene el radio máximo de entrega configurado
     */
    @Override
    public BigDecimal getRadioMaximoEntrega() {
        ConfiguracionSistema config = configuracionRepository.findConfiguracionActual()
                .orElseGet(this::crearConfiguracionPorDefecto);

        return config.getRadioMaximoEntrega();
    }

    /**
     * Verifica si el sistema está en modo mantenimiento
     */
    @Override
    public boolean isEnMantenimiento() {
        ConfiguracionSistema config = configuracionRepository.findConfiguracionActual()
                .orElseGet(this::crearConfiguracionPorDefecto);

        return config.getModoMantenimiento();
    }

    /**
     * Calcula la ganancia del repartidor para un delivery
     */
    @Override
    public BigDecimal calcularGananciaRepartidor(BigDecimal costoDelivery) {
        ConfiguracionSistema config = configuracionRepository.findConfiguracionActual()
                .orElseGet(this::crearConfiguracionPorDefecto);

        return config.calcularGananciaRepartidor(costoDelivery);
    }

    /**
     * Calcula la comisión de la plataforma sobre el pedido
     */
    @Override
    public BigDecimal calcularComisionPlataforma(BigDecimal totalPedido) {
        ConfiguracionSistema config = configuracionRepository.findConfiguracionActual()
                .orElseGet(this::crearConfiguracionPorDefecto);

        return config.calcularComisionPlataforma(totalPedido);
    }

    /**
     * Crea una configuración con valores por defecto
     */
    private ConfiguracionSistema crearConfiguracionPorDefecto() {
        log.warn("⚠️ No existe configuración del sistema. Creando valores por defecto...");

        ConfiguracionSistema config = ConfiguracionSistema.builder()
                .comisionPlataforma(BigDecimal.valueOf(15.0))
                .radioMaximoEntrega(BigDecimal.valueOf(10.0))
                .tiempoMaximoEntrega(45)
                .costoBaseDelivery(BigDecimal.valueOf(200.0))
                .costoPorKilometro(BigDecimal.valueOf(50.0))
                .montoMinimoPedido(BigDecimal.valueOf(500.0))
                .porcentajeGananciasRepartidor(BigDecimal.valueOf(80.0))
                .modoMantenimiento(false)
                .fechaActualizacion(LocalDateTime.now())
                .build();

        return configuracionRepository.save(config);
    }
}