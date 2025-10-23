package utn.back.mordiscoapi.model.dto.usuario;

import jakarta.validation.constraints.*;


public record UsuarioCreateDTO(
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
        String telefono,

        @Size(message = "El email del usuario debe tener máximo 100 caracteres", max = 100)
        @NotBlank(message = "El email del usuario es obligatorio")
        @Email(message = "El email del usuario debe ser válido")
        String email,

        @Size(message = "La contraseña del usuario debe tener mínimo 8 caracteres", min = 8,max = 72)
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,72}$",
                message = "La contraseña debe tener 8-72 caracteres, con al menos una mayúscula, una minúscula, un número y un caracter especial"
        )
        @NotBlank(message = "El contraseña del usuario es obligatoria")
        String password,

        @NotNull(message = "El rol del usuario es obligatorio")
        Long rolId
) {
}
