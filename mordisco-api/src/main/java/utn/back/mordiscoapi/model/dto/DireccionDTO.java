package utn.back.mordiscoapi.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.beans.XMLEncoder;

public record DireccionDTO(
    @Size(message = "La calle no puede contener mas de 100 caracteres", max = 100)
    @NotNull(message = "El mensaje no puede ser nulo.")
    @Schema(description = "Calle de la direccion", example = "Alvarado")
    String calle,
    @Size(message = "El numero no puede contener mas de 20 caracteres",max =20)
    @NotNull(message = "El numero de la calle no puede ser nulo.")
    @Schema(description = "Numero de la direccion", example = "2621")
    String numero,
    @Size (message = "El piso no puede contener mas de 50 caracteres", max = 50)
    @Schema(description = "Piso de la direccion", example = "12")
    String piso,
    @Size(message = "El depto no puede contener mas de 50 caracteres", max = 50)
    @Schema(description = "Depto de la direccion", example = "2")
    String depto,
    @Size(message = "El codigo postal no puede contener mas de 50 caracteeres", max = 50)
    @NotNull(message = "El codigo postal no puede ser nulo")
    @Schema(description = "Codigo postal de la direccion", example = "7600")
    String codigoPostal,
    @Size(message = "La referencia no puede contener mas de 255 caracteres", max = 255)
    @Schema(description = "Referencia de la direccion", example = "Edificio al lado de porton rojo")
    String referencias,
    @Size(message = "La latitud no puede contener mas de 50 caracteres", max = 50)
    @NotNull(message = "La latitud no puede ser nula.")
    @Schema(description = "Latitud de la direccion", example = "-38.00965070799693")
    Double latitud,
    @Size(message = "La longitud no puede contener mas de 50 caracteres", max = 50)
    @NotNull(message = "La longitud no puede ser nula.")
    @Schema(description = "Longitud de la direccion", example = "-57.55554272466376")
    Double longitud,
    @Size(message = "La ciudad no puede contener mas de 50 caracteres", max = 50)
    @NotNull(message = "La ciudad no puede ser nula.")
    @Schema(description = "Ciudad de la direccion", example = "Mar del Plata")
    String ciudad ///TIPO CIUDAD PERO NO EXISTE AUN
) {}
