package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionDTO;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionResponseDTO;
import utn.back.mordiscoapi.model.entity.HorarioAtencion;


@UtilityClass
public class HorarioAtencionMapper {

    /**
     * Convierte una entidad HorarioAtencion a un DTO HorarioAtencionResponseDTO.
     * @param horarioAtencion la entidad HorarioAtencion a convertir
     * @return un DTO HorarioAtencionResponseDTO con los datos de la entidad
     */
    public static HorarioAtencionResponseDTO toDTO(HorarioAtencion horarioAtencion){
        return new HorarioAtencionResponseDTO(horarioAtencion.getId(),
                horarioAtencion.getDia(),
                horarioAtencion.getHoraApertura(),
                horarioAtencion.getHoraCierre());
    }

    /**
     * Convierte un DTO HorarioAtenciónDTO a una entidad HorarioAtención.
     * @param dto el DTO HorarioAtenciónDTO a convertir
     * @return una entidad HorarioAtención con los datos del DTO
     */
    public static HorarioAtencion toEntity(HorarioAtencionDTO dto) {
        return HorarioAtencion.builder()
                .id(dto.id())
                .dia(dto.dia())
                .horaApertura(dto.horaApertura())
                .horaCierre(dto.horaCierre())
                .build();
    }
}
