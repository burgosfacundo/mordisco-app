package utn.back.mordiscoapi.model.dto.pedido;

import io.swagger.v3.oas.annotations.media.Schema;

public record UsuarioPedidoDTO(
    @Schema(description = "ID del cliente", example = "1")
    Long id,
    @Schema(description = "Nombre del cliente", example = "Juan")
    String nombre,
    @Schema(description = "Apellido del cliente", example = "Pérez")
    String apellido,
    @Schema(description = "Email del cliente", example = "juan@gmail.com")
    String email,
    @Schema(description = "Teléfono del cliente", example = "+541112345678")
    String telefono
) {
}
