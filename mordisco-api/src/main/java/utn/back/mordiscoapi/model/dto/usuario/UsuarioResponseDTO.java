package utn.back.mordiscoapi.model.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import utn.back.mordiscoapi.model.dto.direccion.DireccionResponseDTO;

import java.util.List;

public record UsuarioResponseDTO(
        @Schema(description = "ID del usuario", example = "1")
        Long id,
        @Schema(description = "Nombre del usuario", example = "Juan")
        String nombre,
        @Schema(description = "Apellido del usuario", example = "Pérez")
        String apellido,
        @Schema(description = "Teléfono del usuario", example = "123456789")
        String telefono,
        @Schema(description = "Email del usuario", example = "juan@gmail.com")
        String email,
        @Schema(description = "Rol del usuario")
        RolDTO rol,
        @Schema(description = "Direcciones del usuario")
        List<DireccionResponseDTO> direcciones,
        @Schema(description = "Indica si el usuario está dado de baja", example = "false")
        Boolean bajaLogica,
        @Schema(description = "Motivo de la baja lógica", example = "Violación de términos de servicio")
        String motivoBaja,
        @Schema(description = "Fecha de la baja lógica")
        java.time.LocalDateTime fechaBaja
) {
}
