package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import utn.back.mordiscoapi.model.dto.UsuarioDTO;
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
        final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Rol rol = Rol.builder()
                .id(dto.rolId())
                .build();

        return Usuario.builder()
                .nombre(dto.nombre())
                .apellido(dto.apellido())
                .telefono(dto.telefono())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .rol(rol)
                .build();
    }
}
