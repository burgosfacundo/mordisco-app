package utn.back.mordiscoapi.model.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateDTO(
        @Size(message = "El nombre del usuario debe tener máximo 50 caracteres", max = 50)
        @NotBlank(message = "El nombre del usuario es obligatorio")
        String nombre,

        @Size(message = "El apellido del usuario debe tener máximo 50 caracteres", max = 50)
        @NotBlank(message = "El apellido del usuario es obligatorio")
        String apellido,

        @Pattern(
                regexp = "^(?=.{6,25}$)[0-9+()\\- ]+$",
                message = "Teléfono inválido. Permitidos dígitos, + ( ) - y espacios; 6 a 25 caracteres"
        )
        @NotBlank(message = "El teléfono del usuario es obligatorio")
        String telefono
) {
}
