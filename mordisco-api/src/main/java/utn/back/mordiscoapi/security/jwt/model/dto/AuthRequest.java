package utn.back.mordiscoapi.security.jwt.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Email inválido")
        String email,
        @NotBlank(message = "La contraseña es obligatoria")
        String password) {
}