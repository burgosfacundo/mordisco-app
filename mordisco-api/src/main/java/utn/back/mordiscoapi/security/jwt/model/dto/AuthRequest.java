package utn.back.mordiscoapi.security.jwt.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AuthRequest(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Email inválido")
        String email,
        @NotBlank(message = "La contraseña es obligatoria")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,72}$",
                message = "La contraseña debe tener 8-72 caracteres, con al menos una mayúscula, una minúscula, un número y un caracter especial"
        )
        String password) {
}