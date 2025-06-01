package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionResponseDTO;
import utn.back.mordiscoapi.model.entity.HorarioAtencion;

@UtilityClass
public class HorarioAtencionMapper {
    public static HorarioAtencionResponseDTO toDTO(HorarioAtencion horarioAtencion){
        return new HorarioAtencionResponseDTO(horarioAtencion.getId(),
                horarioAtencion.getDia(),
                horarioAtencion.getHoraApertura(),
                horarioAtencion.getHoraCierre());
    }
}
