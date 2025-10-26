package utn.back.mordiscoapi.model.dto.restaurante;

import io.swagger.v3.oas.annotations.media.Schema;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionResponseDTO;
import utn.back.mordiscoapi.model.dto.imagen.ImagenResponseDTO;

import java.util.List;

public record RestauranteResponseCardDTO(
        @Schema(description = "Id del restaurante", example = "7")
        Long id,
        @Schema(description = "Razón social del restaurante", example = "Mc Donald's")
        String razonSocial,
        @Schema(description = "Si el restaurante esta activo", example = "true")
        Boolean activo,
        ImagenResponseDTO logo,
        @Schema(description = "Horarios de atencion del restaurante")
        List<HorarioAtencionResponseDTO> hoariosDeAtencion,
        @Schema(description = "Estrellas del restaurante")
        Double estrellas
        ) {}
