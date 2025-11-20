package utn.back.mordiscoapi.service.interf;

import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.configuracion.ConfiguracionSistemaRequestDTO;
import utn.back.mordiscoapi.model.dto.configuracion.ConfiguracionSistemaResponseDTO;

import java.math.BigDecimal;

public interface IConfiguracionSistemaService {
    ConfiguracionSistemaResponseDTO getConfiguracionActual();
    ConfiguracionSistemaResponseDTO actualizarConfiguracion(ConfiguracionSistemaRequestDTO dto, Long usuarioId)
            throws NotFoundException;
    BigDecimal calcularCostoDelivery(BigDecimal distanciaKm);
    BigDecimal getMontoMinimoPedido();
    BigDecimal getRadioMaximoEntrega();
    boolean isEnMantenimiento();
    BigDecimal calcularGananciaRepartidor(BigDecimal costoDelivery);
    BigDecimal calcularComisionPlataforma(BigDecimal totalPedido);
}