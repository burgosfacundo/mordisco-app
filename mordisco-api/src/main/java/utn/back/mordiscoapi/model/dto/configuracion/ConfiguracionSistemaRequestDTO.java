package utn.back.mordiscoapi.model.dto.configuracion;

import jakarta.validation.constraints.*;
import utn.back.mordiscoapi.common.validation.ValidationConstants;

import java.math.BigDecimal;

import static utn.back.mordiscoapi.common.validation.ValidationConstants.*;

public record ConfiguracionSistemaRequestDTO(

        @NotNull(message = "La comisión de la plataforma es obligatoria")
        @DecimalMin(value = PORCENTAJE_GANANCIAS_RESTAURANTE_MIN, message = PORCENTAJE_GANANCIAS_RESTAURANTE_MESSAGE)
        @DecimalMax(value = PORCENTAJE_GANANCIAS_RESTAURANTE_MAX, message = PORCENTAJE_GANANCIAS_RESTAURANTE_MESSAGE)
        BigDecimal porcentajeGananciasRestaurante,

        @NotNull(message = "El radio máximo de entrega es obligatorio")
        @DecimalMin(value = RADIO_MAXIMO_ENTREGA_MIN, message = RADIO_MAXIMO_ENTREGA_MESSAGE)
        @DecimalMax(value = RADIO_MAXIMO_ENTREGA_MAX, message = RADIO_MAXIMO_ENTREGA_MESSAGE)
        BigDecimal radioMaximoEntrega,

        @NotNull(message = "El costo base de delivery es obligatorio")
        @DecimalMin(value = COSTO_BASE_DELIVERY_MIN, message = COSTO_BASE_DELIVERY_MESSAGE)
        @DecimalMax(value = COSTO_BASE_DELIVERY_MAX, message = COSTO_BASE_DELIVERY_MESSAGE)
        BigDecimal costoBaseDelivery,

        @NotNull(message = "El costo por kilómetro es obligatorio")
        @DecimalMin(value = COSTO_POR_KILOMETRO_MIN, message = COSTO_POR_KILOMETRO_MESSAGE)
        @DecimalMax(value = COSTO_POR_KILOMETRO_MAX, message = COSTO_POR_KILOMETRO_MESSAGE)
        BigDecimal costoPorKilometro,

        @NotNull(message = "El monto mínimo de pedido es obligatorio")
        @DecimalMin(value = MONTO_MINIMO_PEDIDO_MIN, message = MONTO_MINIMO_PEDIDO_MESSAGE)
        @DecimalMax(value = MONTO_MINIMO_PEDIDO_MAX, message = MONTO_MINIMO_PEDIDO_MESSAGE)
        BigDecimal montoMinimoPedido,

        @NotNull(message = "El porcentaje de ganancias del repartidor es obligatorio")
        @DecimalMin(value = PORCENTAJE_GANANCIAS_REPARTIDOR_MIN, message = PORCENTAJE_GANANCIAS_REPARTIDOR_MESSAGE)
        @DecimalMax(value = PORCENTAJE_GANANCIAS_REPARTIDOR_MAX, message = PORCENTAJE_GANANCIAS_REPARTIDOR_MESSAGE)
        BigDecimal porcentajeGananciasRepartidor
) {}