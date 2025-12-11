package utn.back.mordiscoapi.model.dto.configuracion;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ConfiguracionSistemaRequestDTO(

        @NotNull(message = "La comisión de la plataforma es obligatoria")
        @DecimalMin(value = "0.0", message = "La comisión debe ser mayor o igual a 0")
        @DecimalMax(value = "100.0", message = "La comisión no puede ser mayor a 100%")
        BigDecimal porcentajeGananciasRestaurante,

        @NotNull(message = "El radio máximo de entrega es obligatorio")
        @DecimalMin(value = "1.0", message = "El radio debe ser al menos 1 km")
        @DecimalMax(value = "50.0", message = "El radio no puede ser mayor a 50 km")
        BigDecimal radioMaximoEntrega,

        @NotNull(message = "El tiempo máximo de entrega es obligatorio")
        @Min(value = 15, message = "El tiempo mínimo es 15 minutos")
        @Max(value = 180, message = "El tiempo máximo es 180 minutos")
        Integer tiempoMaximoEntrega,

        @NotNull(message = "El costo base de delivery es obligatorio")
        @DecimalMin(value = "0.0", message = "El costo base no puede ser negativo")
        BigDecimal costoBaseDelivery,

        @NotNull(message = "El costo por kilómetro es obligatorio")
        @DecimalMin(value = "0.0", message = "El costo por km no puede ser negativo")
        BigDecimal costoPorKilometro,

        @NotNull(message = "El monto mínimo de pedido es obligatorio")
        @DecimalMin(value = "0.0", message = "El monto mínimo no puede ser negativo")
        BigDecimal montoMinimoPedido,

        @NotNull(message = "El porcentaje de ganancias del repartidor es obligatorio")
        @DecimalMin(value = "0.0", message = "El porcentaje debe ser mayor o igual a 0")
        @DecimalMax(value = "100.0", message = "El porcentaje no puede ser mayor a 100%")
        BigDecimal porcentajeGananciasRepartidor,

        @NotNull(message = "El estado de mantenimiento es obligatorio")
        Boolean modoMantenimiento,

        @Size(max = 500, message = "El mensaje no puede exceder 500 caracteres")
        String mensajeMantenimiento
) {}