package utn.back.mordiscoapi.model.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import utn.back.mordiscoapi.common.validation.ValidationConstants;

public record UsuarioUpdateDTO(
        @Size(message = "El nombre del usuario debe tener máximo 50 caracteres", max = ValidationConstants.NOMBRE_MAX_LENGTH)
        @NotBlank(message = "El nombre del usuario es obligatorio")
        String nombre,

        @Size(message = "El apellido del usuario debe tener máximo 50 caracteres", max = ValidationConstants.NOMBRE_MAX_LENGTH)
        @NotBlank(message = "El apellido del usuario es obligatorio")
        String apellido,

        @Pattern(
                regexp = ValidationConstants.TELEFONO_PATTERN,
                message = ValidationConstants.TELEFONO_MESSAGE
        )
        @Size(max = ValidationConstants.TELEFONO_MAX_LENGTH, message = "El teléfono debe tener máximo " + ValidationConstants.TELEFONO_MAX_LENGTH + " caracteres")
        @NotBlank(message = "El teléfono del usuario es obligatorio")
        String telefono
) {
}
