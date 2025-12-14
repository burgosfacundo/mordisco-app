package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionRequestDTO;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionResponseDTO;
import utn.back.mordiscoapi.model.entity.HorarioAtencion;
import utn.back.mordiscoapi.model.entity.Restaurante;


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
                horarioAtencion.getHoraCierre(),
                horarioAtencion.getCruzaMedianoche());
    }

    /**
     * Convierte un DTO HorarioAtenciónDTO a una entidad HorarioAtención.
     * @param dto el DTO HorarioAtenciónDTO a convertir
     * @return una entidad HorarioAtención con los datos del DTO
     */
    public static HorarioAtencion toEntity(Restaurante restaurante, HorarioAtencionRequestDTO dto) {
        return HorarioAtencion.builder()
                .dia(dto.dia())
                .horaApertura(dto.horaApertura())
                .horaCierre(dto.horaCierre())
                .cruzaMedianoche(dto.cruzaMedianoche() != null ? dto.cruzaMedianoche() : false)
                .restaurante(restaurante)
                .build();
    }

    public static void applyUpdate(HorarioAtencionRequestDTO dto, HorarioAtencion target) {
        target.setDia(dto.dia());
        target.setHoraApertura(dto.horaApertura());
        target.setHoraCierre(dto.horaCierre());
        target.setCruzaMedianoche(dto.cruzaMedianoche() != null ? dto.cruzaMedianoche() : false);
    }
}
