package utn.back.mordiscoapi.model.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RecoverPasswordDTO(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe ser válido")
        String email
) {}