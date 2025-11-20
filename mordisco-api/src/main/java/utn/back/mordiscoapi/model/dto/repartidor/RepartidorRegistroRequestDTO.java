package utn.back.mordiscoapi.model.dto.repartidor;

import jakarta.validation.constraints.*;

public record RepartidorRegistroRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
        String apellido,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Formato de teléfono inválido")
        String telefono,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato de email inválido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,

        @NotBlank(message = "El CUIL es obligatorio")
        @Pattern(regexp = "^\\d{11}$", message = "El CUIL debe tener 11 dígitos")
        String cuil,

        @NotBlank(message = "La foto del DNI es obligatoria")
        String fotoDni
) {}