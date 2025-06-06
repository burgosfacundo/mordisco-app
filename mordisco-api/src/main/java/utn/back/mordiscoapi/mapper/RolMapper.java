package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.usuario.RolDTO;
import utn.back.mordiscoapi.model.entity.Rol;

@UtilityClass
public class RolMapper {
    public static RolDTO toDto(Rol rol) {
        return new RolDTO(rol.getId(), rol.getNombre());
    }
}
