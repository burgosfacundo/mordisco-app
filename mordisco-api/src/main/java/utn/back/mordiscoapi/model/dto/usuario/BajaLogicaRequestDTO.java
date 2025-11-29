package utn.back.mordiscoapi.model.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BajaLogicaRequestDTO(
        @NotBlank(message = "El motivo de la baja es obligatorio")
        @Size(min = 10, max = 500, message = "El motivo debe tener entre 10 y 500 caracteres")
        String motivo
) {}
