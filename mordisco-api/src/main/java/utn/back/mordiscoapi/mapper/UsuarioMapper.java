package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioDTO;
import utn.back.mordiscoapi.model.entity.Rol;
import utn.back.mordiscoapi.model.entity.Usuario;

@UtilityClass
public class UsuarioMapper {
    /**
     * Convierte un DTO de usuario a una entidad de usuario.
     * @param dto el DTO de usuario a convertir
     * @return la entidad de usuario con los datos del DTO
     */
    public static Usuario toUsuario(UsuarioDTO dto) {
        Rol rol = Rol.builder()
                .id(dto.rolId())
                .build();

        return Usuario.builder()
                .nombre(dto.nombre())
                .apellido(dto.apellido())
                .telefono(dto.telefono())
                .email(dto.email())
                .password(dto.password())
                .rol(rol)
                .build();
    }
}
