package utn.back.mordiscoapi.model.dto.configuracion;

import java.math.BigDecimal;
public record ConfiguracionSistemaGeneralResponseDTO(
        BigDecimal porcentajeGananciasRestaurante,
        BigDecimal radioMaximoEntrega,
        Integer tiempoMaximoEntrega,
        BigDecimal costoBaseDelivery,
        BigDecimal costoPorKilometro,
        BigDecimal montoMinimoPedido,
        BigDecimal porcentajeGananciasRepartidor
) {
}
