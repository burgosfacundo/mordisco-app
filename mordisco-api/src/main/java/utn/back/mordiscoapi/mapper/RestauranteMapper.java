package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.direccion.DireccionRestauranteDTO;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionDTO;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioDeAtencionResponseDTO;
import utn.back.mordiscoapi.model.dto.imagen.ImagenResponseRestauranteDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseListarDTO;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioResponseDTO;
import utn.back.mordiscoapi.model.entity.*;

import java.util.List;

@UtilityClass
public class RestauranteMapper {
    /**
     * Convierte un DTO de restaurante a una entidad de restaurante.
     * @param dto el DTO de restaurante a convertir
     * @return la entidad de restaurante con los datos del DTO
     */
    public static Restaurante toEntity(RestauranteDTO dto) {
        Usuario usuario = Usuario.builder()
                .id(dto.idUsuario())
                .build();
        Imagen imagen = Imagen.builder()
                .url(dto.logo().url())
                .nombre(dto.logo().nombre())
                .build();
        Direccion direccion = Direccion.builder()
                .calle(dto.direccion().calle())
                .numero(dto.direccion().numero())
                .ciudad(dto.direccion().ciudad())
                .codigoPostal(dto.direccion().codigoPostal())
                .depto(dto.direccion().depto())
                .referencias(dto.direccion().referencias())
                .latitud(dto.direccion().latitud())
                .longitud(dto.direccion().longitud())
                .esTemporal(dto.direccion().esTemporal())
                .build();
        return Restaurante.builder()
                .razonSocial(dto.razonSocial())
                .activo(dto.activo())
                .imagen(imagen) //preguntar
                .usuario(usuario) //preguntar
                .direccion(direccion) //preguntar
                .build();
    }
    public static RestauranteResponseDTO toDTOX (Restaurante restaurante){
        return new RestauranteResponseDTO(restaurante.getId(),
                restaurante.getRazonSocial(),
                restaurante.getActivo(),
                restaurante.getImagen() != null ? ImagenMapper.toDTO(restaurante.getImagen()) : null,
                restaurante.getMenu() != null ? restaurante.getMenu().getId() : null,
                restaurante.getPromociones() != null ? restaurante.getPromociones().stream().map(PromocionMapper::toDTO).toList() : List.of(),
                restaurante.getHorariosAtencion() != null ? restaurante.getHorariosAtencion().stream().map(HorarioDeAtencionMapper::toDTO).toList() : List.of(),
                restaurante.getCalificaciones() != null ? restaurante.getCalificaciones().stream().map(CalificacionRestauranteMapper::toDTO).toList() : List.of(),
                restaurante.getDireccion() != null ? DireccionMapper.toDTO(restaurante.getDireccion()) : null

        );
    }
    public static RestauranteResponseListarDTO toDTO (Restaurante dto){
        UsuarioResponseDTO usuarioRDTO = new UsuarioResponseDTO(dto.getUsuario().getId());
        ImagenResponseRestauranteDTO imagen = new ImagenResponseRestauranteDTO(dto.getImagen().getId());
        DireccionRestauranteDTO direccionDto = new DireccionRestauranteDTO(dto.getDireccion().getId());

        return new RestauranteResponseListarDTO(
                dto.getId(),
                dto.getRazonSocial(),
                dto.getActivo(),
                imagen,
                dto.getMenu().getId(),
                dto.getMenu().getId(),//preguntar
                direccionDto.id() //preguntar
                );


    }

}
