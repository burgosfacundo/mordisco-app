package utn.back.mordiscoapi.model.dto.configuracion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConfiguracionSistemaResponseDTO(
        Long id,
        BigDecimal porcentajeGananciasRestaurante,
        BigDecimal radioMaximoEntrega,
        BigDecimal costoBaseDelivery,
        BigDecimal costoPorKilometro,
        BigDecimal montoMinimoPedido,
        BigDecimal porcentajeGananciasRepartidor,
        LocalDateTime fechaActualizacion,
        String emailUsuarioModificacion
) {}