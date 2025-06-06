package utn.back.mordiscoapi.model.dto.pedido;

import io.swagger.v3.oas.annotations.media.Schema;
import utn.back.mordiscoapi.model.dto.direccion.DireccionResponseDTO;
import utn.back.mordiscoapi.model.dto.imagen.ImagenResponseDTO;

public record RestaurantePedidoDTO(
        @Schema(description = "ID del restaurante", example = "1")
        Long id,
        @Schema(description = "Razón social del restaurante", example = "Restaurante Mordisco")
        String razonSocial,
        @Schema(description = "Dirección del restaurante", example = "Av. Siempre Viva 123")
        DireccionResponseDTO direccion,
        @Schema(description = "Logo del restaurante")
        ImagenResponseDTO logo
) {
}
