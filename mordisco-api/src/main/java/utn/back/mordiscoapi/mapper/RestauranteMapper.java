package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseDTO;
import utn.back.mordiscoapi.model.entity.*;

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
    public static RestauranteResponseDTO toDTO (Restaurante restaurante){
        return new RestauranteResponseDTO(restaurante.getId(),
                restaurante.getRazonSocial(),
                restaurante.getActivo(),
                ImagenMapper.toDTO(restaurante.getImagen()),
                restaurante.getMenu().getId(),
                restaurante.getPromociones().stream().map(PromocionMapper::toDTO).toList(),
                restaurante.getHorariosAtencion().stream().map(HorarioDeAtencionMapper ::toDTO).toList(),
                restaurante.getCalificaciones().stream().map(CalificacionRestauranteMapper ::toDTO).toList(),
                DireccionMapper.toDTO(restaurante.getDireccion()));
    }

}
