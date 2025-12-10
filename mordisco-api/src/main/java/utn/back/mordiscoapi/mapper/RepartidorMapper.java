package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.repartidor.RepartidorBasicDTO;
import utn.back.mordiscoapi.model.entity.Usuario;

@UtilityClass
public class RepartidorMapper {

    public static RepartidorBasicDTO toRepartidorBasicDTO (Usuario r){
        return new RepartidorBasicDTO(r.getId(), r.getNombre(), r.getApellido());
    }
}
