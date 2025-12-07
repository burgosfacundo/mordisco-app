package utn.back.mordiscoapi.model.dto.direccion;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record DireccionRequestDTO(
    @Size(message = "La calle no puede contener mas de 50 caracteres", max = 50)
    @NotBlank(message = "La calle es obligatoria")
    @Schema(description = "Calle de la dirección", example = "Alvarado")
    String calle,
    @Size(message = "El numero no puede contener mas de 5 caracteres",max = 5)
    @NotBlank(message = "El número de la calle no puede ser nulo.")
    @Pattern(
            regexp = "^[ \\t]*[0-9A-Za-z/\\-]{1,10}[ \\t]*$",
            message = "Número inválido. Permitidos dígitos, letras, / y - (1 a 10 caracteres)."
    )

    @Schema(description = "Número de la dirección", example = "2621")
    String numero,
    @Size(message = "El piso no puede contener mas de 20 caracteres", max = 20)
    @Schema(description = "Piso de la dirección", example = "12")
    String piso,
    @Size(message = "El departamento no puede contener mas de 20 caracteres", max = 20)
    @Schema(description = "Departamento de la dirección", example = "C")
    String depto,
    @Size(message = "El código postal no puede contener mas de 10 caracteres", max = 10)
    @NotBlank(message = "El código postal es obligatorio")
    @Schema(description = "Código postal de la dirección", example = "7600")
    @Pattern(regexp = "^[0-9A-Za-z\\- ]{1,12}$")
    String codigoPostal,
    @Size(message = "La referencia no puede contener mas de 255 caracteres", max = 255)
    @Schema(description = "Referencia de la dirección", example = "Edificio al lado de porton rojo")
    String referencias,
    @Size(message = "El alias no puede contener mas de 50 caracteres", max = 50)
    @Schema(description = "Alias de la dirección", example = "Casa de mi madre")
    String alias,
    @Size(message = "La ciudad no puede contener mas de 50 caracteres", max = 50)
    @NotBlank(message = "La ciudad es obligatoria")
    @Schema(description = "Ciudad de la dirección", example = "Mar del Plata")
    String ciudad
) {}
