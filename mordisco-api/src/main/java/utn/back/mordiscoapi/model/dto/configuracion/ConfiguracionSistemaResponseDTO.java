package utn.back.mordiscoapi.model.dto.configuracion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConfiguracionSistemaResponseDTO(
        Long id,
        BigDecimal comisionPlataforma,
        BigDecimal radioMaximoEntrega,
        Integer tiempoMaximoEntrega,
        BigDecimal costoBaseDelivery,
        BigDecimal costoPorKilometro,
        BigDecimal montoMinimoPedido,
        BigDecimal porcentajeGananciasRepartidor,
        Boolean modoMantenimiento,
        String mensajeMantenimiento,
        LocalDateTime fechaActualizacion,
        String emailUsuarioModificacion
) {}