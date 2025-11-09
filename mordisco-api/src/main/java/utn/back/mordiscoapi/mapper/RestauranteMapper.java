package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import utn.back.mordiscoapi.model.dto.pedido.RestaurantePedidoDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteCreateDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseCardDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseDTO;
import utn.back.mordiscoapi.model.entity.*;
import utn.back.mordiscoapi.common.util.Sanitize;


@Slf4j
@UtilityClass
public class RestauranteMapper {
    /**
     * Convierte un DTO de restaurante a una entidad de restaurante.
     * @param dto el DTO de restaurante a convertir
     * @return la entidad de restaurante con los datos del DTO
     */
    public static Restaurante toEntity(RestauranteCreateDTO dto) {
        log.debug(dto.toString());
        Usuario usuario = Usuario.builder()
                .id(dto.idUsuario())
                .build();
        Imagen imagen = Imagen.builder()
                .url(Sanitize.collapseSpaces(dto.logo().url()))
                .nombre(Sanitize.collapseSpaces(dto.logo().nombre()))
                .build();
        Direccion direccion = Direccion.builder()
                .calle(Sanitize.collapseSpaces(dto.direccion().calle()))
                .numero(Sanitize.trimToNull(dto.direccion().numero()))
                .piso(Sanitize.trimToNull(dto.direccion().piso()))
                .depto(Sanitize.trimToNull(dto.direccion().depto()))
                .codigoPostal(Sanitize.trimToNull(dto.direccion().codigoPostal()))
                .ciudad(Sanitize.collapseSpaces(dto.direccion().ciudad()))
                .referencias(Sanitize.trimToNull(dto.direccion().referencias()))
                .usuario(usuario)
                .build();
        return Restaurante.builder()
                .razonSocial(Sanitize.trimToNull(dto.razonSocial()))
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
