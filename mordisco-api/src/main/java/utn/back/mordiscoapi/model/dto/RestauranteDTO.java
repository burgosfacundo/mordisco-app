package utn.back.mordiscoapi.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RestauranteDTO(
                            @Size(message = "La razón social del restaurante debe tener máximo 50 caracteres", max = 50)
                            @NotNull(message = "La razón social del restaurante es obligatoria")
                            String razonSocial,
                            @NotBlank(message = "El horario de atención es obligatorio")
                            List<HorarioAtencionDTO> horarioAtencion,
                            @NotNull(message = "El id del usuario es obligatorio")
                            Long idUsuario
                            //DireccionDTO direccion,
                            //ImagenDTO logo,
                            ){
}
