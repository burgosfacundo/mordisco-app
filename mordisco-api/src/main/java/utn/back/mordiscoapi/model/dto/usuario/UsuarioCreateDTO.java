package utn.back.mordiscoapi.model.dto.usuario;

import jakarta.validation.constraints.*;
import utn.back.mordiscoapi.common.validation.ValidationConstants;


public record UsuarioCreateDTO(
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
        String telefono,

        @Size(message = "El email del usuario debe tener máximo 100 caracteres", max = 100)
        @NotBlank(message = "El email del usuario es obligatorio")
        @Email(message = "El email del usuario debe ser válido")
        String email,

        @Size(message = "La contraseña del usuario debe tener mínimo 8 caracteres", min = ValidationConstants.PASSWORD_MIN_LENGTH, max = ValidationConstants.PASSWORD_MAX_LENGTH)
        @Pattern(
                regexp = ValidationConstants.PASSWORD_PATTERN,
                message = ValidationConstants.PASSWORD_MESSAGE
        )
        @NotBlank(message = "La contraseña del usuario es obligatoria")
        String password,

        @NotNull(message = "El rol del usuario es obligatorio")
        Long rolId
) {

}
