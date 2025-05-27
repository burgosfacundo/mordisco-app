package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.direccion.DireccionResponseDTO;
import utn.back.mordiscoapi.model.entity.Direccion;

@UtilityClass
public class DireccionMapper {
    public static DireccionResponseDTO toDTO (Direccion direcciones){
        return new DireccionResponseDTO(direcciones.getId(),
                direcciones.getCalle(),
                direcciones.getNumero(),
                direcciones.getPiso(),
                direcciones.getDepto(),
                direcciones.getCodigoPostal(),
                direcciones.getReferencias(),
                direcciones.getLatitud(),
                direcciones.getLongitud(),
                direcciones.getCiudad());
    }
}
