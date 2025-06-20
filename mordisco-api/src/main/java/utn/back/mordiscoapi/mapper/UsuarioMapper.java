package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import utn.back.mordiscoapi.model.dto.pedido.UsuarioPedidoDTO;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioCreateDTO;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioResponseDTO;
import utn.back.mordiscoapi.model.entity.Direccion;
import utn.back.mordiscoapi.model.entity.Rol;
import utn.back.mordiscoapi.model.entity.Usuario;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class UsuarioMapper {
    /**
     * Convierte un DTO de usuario a una entidad de usuario.
     * @param dto el DTO de usuario a convertir
     * @return la entidad de usuario con los datos del DTO
     */
    public static Usuario toUsuario(UsuarioCreateDTO dto) {
        final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Rol rol = Rol.builder()
                .id(dto.rolId())
                .build();

        List<Direccion> direcciones = dto.direcciones() != null
                ? dto.direcciones().stream()
                .map(DireccionMapper::toEntity)
                .toList()
                : new ArrayList<>();

        return Usuario.builder()
                .nombre(dto.nombre())
                .apellido(dto.apellido())
                .telefono(dto.telefono())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .rol(rol)
                .direcciones(direcciones)
                .build();
    }

    public static UsuarioResponseDTO toUsuarioResponseDTO(Usuario usuario) {
        var rol = RolMapper.toDto(usuario.getRol());
        var direcciones = usuario.getDirecciones().stream()
                .map(DireccionMapper::toDTO)
                .toList();
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTelefono(),
                rol,
                direcciones
        );
    }

    /**
     * Convierte una entidad de usuario a un DTO de usuario.
     * @param usuario la entidad de usuario a convertir
     * @return el DTO de usuario con los datos de la entidad
     */
    public static UsuarioPedidoDTO toUsuarioPedidoDTO(Usuario usuario) {
        return new UsuarioPedidoDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido()
        );
    }
}
