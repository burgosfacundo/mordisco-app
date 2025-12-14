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
        Usuario usuario = Usuario.builder()
                .id(dto.idUsuario())
                .build();

        Imagen imagen = Imagen.builder()
                .url(Sanitize.collapseSpaces(dto.logo().url()))
                .nombre(Sanitize.collapseSpaces(dto.logo().nombre()))
                .build();

        Direccion direccion = Direccion.builder()
                .calle(Sanitize.toTitleCase(dto.direccion().calle()))
                .numero(Sanitize.trimToNull(dto.direccion().numero()))
                .piso(Sanitize.trimToNull(dto.direccion().piso()))
                .depto(Sanitize.trimToNull(dto.direccion().depto()))
                .codigoPostal(Sanitize.trimToNull(dto.direccion().codigoPostal()))
                .ciudad(Sanitize.toTitleCase(dto.direccion().ciudad()))
                .referencias(Sanitize.trimToNull(dto.direccion().referencias()))
                .usuario(usuario)
                .build();

        return Restaurante.builder()
                .razonSocial(Sanitize.trimToNull(dto.razonSocial()))
                .activo(dto.activo())
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

        return new RestauranteResponseDTO(
                r.getId(),
                r.getRazonSocial(),
                r.getActivo(),
                imagen,
                r.getMenu() == null ? null : r.getMenu().getId(),
                r.getPromedioCalificaciones(),
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
        var direccion = r.getDireccion();
        
        // Determinar si el restaurante está abierto en este momento
        boolean estaAbierto = isRestauranteAbierto(r);

        return new RestauranteResponseCardDTO(
                r.getId(),
                r.getRazonSocial(),
                r.getActivo(),
                imagen,
                r.getHorariosAtencion().stream().map(HorarioAtencionMapper::toDTO).toList(),
                r.getPromedioCalificaciones(),
                direccion != null ? direccion.getLatitud() : null,
                direccion != null ? direccion.getLongitud() : null,
                estaAbierto
        );
    }

    /**
     * Determina si un restaurante está abierto en este momento
     * @param r la entidad de restaurante
     * @return true si el restaurante está abierto, false en caso contrario
     */
    private static boolean isRestauranteAbierto(Restaurante r) {
        if (r.getHorariosAtencion() == null || r.getHorariosAtencion().isEmpty()) {
            return false;
        }

        var now = java.time.LocalDateTime.now();
        var currentDay = now.getDayOfWeek();
        var currentTime = now.toLocalTime();

        return r.getHorariosAtencion().stream()
                .anyMatch(horario -> 
                    horario.getDia().equals(currentDay) &&
                    !currentTime.isBefore(horario.getHoraApertura()) &&
                    !currentTime.isAfter(horario.getHoraCierre())
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
