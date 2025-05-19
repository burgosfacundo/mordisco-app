package utn.back.mordiscoapi.model.projection;

import io.swagger.v3.oas.annotations.media.Schema;

public interface UsuarioProjection {
    @Schema(description = "ID del usuario", example = "1")
    Long getId();
    @Schema(description = "Nombre del usuario", example = "Juan")
    String getNombre();
    @Schema(description = "Apellido del usuario", example = "Perez")
    String getApellido();
    @Schema(description = "Telefono del usuario", example = "223123456")
    String getTelefono();
    @Schema(description = "Email del usuario", example = "juanperez@example.com")
    String getEmail();
    @Schema(description = "Rol del usuario", example = "1")
    Long getRolId();
}
