package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioDeAtencionResponseDTO;
import utn.back.mordiscoapi.model.entity.HorarioAtencion;

@UtilityClass
public class HorarioDeAtencionMapper {
    public static HorarioDeAtencionResponseDTO toDTO(HorarioAtencion horarioAtencion){
        return new HorarioDeAtencionResponseDTO(horarioAtencion.getId(),
                horarioAtencion.getDia(),
                horarioAtencion.getHoraApertura(),
                horarioAtencion.getHoraCierre());
    }
}
