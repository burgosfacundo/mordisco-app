package utn.back.mordiscoapi.model.dto.restaurante;

import io.swagger.v3.oas.annotations.media.Schema;
import utn.back.mordiscoapi.model.dto.calificacionRestaurante.CalificacionRestauranteResponseDTO;
import utn.back.mordiscoapi.model.dto.direccion.DireccionResponseDTO;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioDeAtencionResponseDTO;
import utn.back.mordiscoapi.model.dto.imagen.ImagenResponseDTO;
import utn.back.mordiscoapi.model.dto.promocion.PromocionResponseDTO;

import java.util.List;

public record RestauranteResponseDTO (
        @Schema(description = "Id del restaurante", example = "7")
    Long id,
        @Schema(description = "Razón social del restaurante", example = "Mc Donald's")
    String razonSocial,
        @Schema(description = "Si el restaurante esta activo", example = "true")
    Boolean activo,
        ImagenResponseDTO logo,
        Long menuId,
        List<PromocionResponseDTO> promociones,
        List<HorarioDeAtencionResponseDTO> hoariosDeAtencion,
        List<CalificacionRestauranteResponseDTO> calificacionRestaurante,
        DireccionResponseDTO direccion
    ){

}


