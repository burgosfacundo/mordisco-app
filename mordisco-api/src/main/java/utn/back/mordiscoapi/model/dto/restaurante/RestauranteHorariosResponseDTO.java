package utn.back.mordiscoapi.model.dto.restaurante;

import io.swagger.v3.oas.annotations.media.Schema;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionDTO;

import java.util.List;

public record RestauranteHorariosResponseDTO(
        @Schema(description = "Id del restaurante", example = "1")
        Long idRestaurante,
        List<HorarioAtencionDTO> horarios
) {
}
