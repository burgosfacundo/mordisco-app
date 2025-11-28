package utn.back.mordiscoapi.model.dto.ganancia;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GananciaRepartidorResponseDTO(
        @Schema(description = "ID de la ganancia", example = "1")
        Long id,

        @Schema(description = "ID del pedido", example = "123")
        Long pedidoId,

        @Schema(description = "Número del pedido para mostrar", example = "#123")
        String numeroPedido,

        @Schema(description = "Costo total del delivery", example = "500.00")
        BigDecimal costoDelivery,

        @Schema(description = "Comisión de la plataforma", example = "100.00")
        BigDecimal comisionPlataforma,

        @Schema(description = "Ganancia del repartidor", example = "400.00")
        BigDecimal gananciaRepartidor,

        @Schema(description = "Porcentaje aplicado", example = "80.00")
        BigDecimal porcentajeAplicado,

        @Schema(description = "Fecha de registro", example = "2024-01-15T14:30:00")
        LocalDateTime fechaRegistro
) {
}
