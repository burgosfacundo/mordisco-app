package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.direccion.DireccionResponseDTO;
import utn.back.mordiscoapi.model.dto.imagen.ImagenResponseDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseListarDTO;
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

    public static RestauranteResponseListarDTO toDTO (Restaurante dto){
        ImagenResponseDTO imagen = new ImagenResponseDTO(dto.getImagen().getId(), dto.getImagen().getUrl(), dto.getImagen().getNombre());
        DireccionResponseDTO direccionDto = new DireccionResponseDTO(dto.getDireccion().getId(),dto.getDireccion().getCalle(),dto.getDireccion().getNumero(),dto.getDireccion().getPiso(),dto.getDireccion().getDepto(),dto.getDireccion().getCodigoPostal(),dto.getDireccion().getReferencias(),dto.getDireccion().getLatitud(),dto.getDireccion().getLongitud(),dto.getDireccion().getCiudad());
        return new RestauranteResponseListarDTO(
                dto.getId(),
                dto.getRazonSocial(),
                dto.getActivo(),
                imagen,
                dto.getMenu().getId(),
                direccionDto,
                dto.getUsuario().getId()
                );
    }

}
