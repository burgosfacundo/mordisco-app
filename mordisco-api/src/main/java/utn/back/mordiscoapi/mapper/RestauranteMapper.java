package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.pedido.RestaurantePedidoDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteCreateDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseCardDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseDTO;
import utn.back.mordiscoapi.model.entity.*;


@UtilityClass
public class RestauranteMapper {
    /**
     * Convierte un DTO de restaurante a una entidad de restaurante.
     * @param dto el DTO de restaurante a convertir
     * @return la entidad de restaurante con los datos del DTO
     */
    public static Restaurante toEntity(RestauranteCreateDTO dto) {
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
                .build();
        return Restaurante.builder()
                .razonSocial(dto.razonSocial())
                .imagen(imagen)
                .usuario(usuario)
                .direccion(direccion)
                .build();
    }

    /**
     * Convierte una entidad de restaurante a un DTO de respuesta de restaurante.
     * @param r la entidad de restaurante a convertir
     * @return el DTO de respuesta de restaurante con los datos de la entidad
     */
    public static RestauranteResponseDTO toDTO (Restaurante r){
        var imagen = ImagenMapper.toDTO(r.getImagen());
        var direccion = DireccionMapper.toDTO(r.getDireccion());
        double estrellas = 0.0;
        if (r.getCalificaciones() != null && !r.getCalificaciones().isEmpty()) {
            estrellas = r.getCalificaciones()
                    .stream()
                    .mapToInt(CalificacionRestaurante::getPuntaje)
                    .average()
                    .orElse(0.0);
        }

        return new RestauranteResponseDTO(
                r.getId(),
                r.getRazonSocial(),
                r.getActivo(),
                imagen,
                r.getMenu() == null ? null : r.getMenu().getId(),
                r.getPromociones().stream().map(PromocionMapper::toDTO).toList(),
                r.getHorariosAtencion().stream().map(HorarioAtencionMapper::toDTO).toList(),
                r.getCalificaciones().stream().map(CalificacionRestauranteMapper::toDTO).toList(),
                estrellas,
                direccion
                );
    }

    /**
     * Convierte una entidad de restaurante a un DTO de respuesta de restaurante.
     * @param r la entidad de restaurante a convertir
     * @return el DTO de respuesta de restaurante con los datos de la entidad
     */
    public static RestauranteResponseCardDTO toCardDTO(Restaurante r){
        var imagen = ImagenMapper.toDTO(r.getImagen());
        double estrellas = 0.0;
        if (r.getCalificaciones() != null && !r.getCalificaciones().isEmpty()) {
            estrellas = r.getCalificaciones()
                    .stream()
                    .mapToInt(CalificacionRestaurante::getPuntaje)
                    .average()
                    .orElse(0.0);
        }

        return new RestauranteResponseCardDTO(
                r.getId(),
                r.getRazonSocial(),
                r.getActivo(),
                imagen,
                r.getHorariosAtencion().stream().map(HorarioAtencionMapper::toDTO).toList(),
                estrellas
        );
    }



    /**
     * Convierte una entidad de restaurante a un DTO de respuesta de restaurante para pedidos.
     * @param r la entidad de restaurante a convertir
     * @return el DTO de respuesta de restaurante para pedidos con los datos de la entidad
     */
    public static RestaurantePedidoDTO toRestaurantePedidoDTO(Restaurante r) {
        return new RestaurantePedidoDTO(
                r.getId(),
                r.getRazonSocial(),
                DireccionMapper.toDTO(r.getDireccion()),
                ImagenMapper.toDTO(r.getImagen())
        );
    }
}
