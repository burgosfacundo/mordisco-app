package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.calificacionRestaurante.CalificacionDTO;
import utn.back.mordiscoapi.model.dto.calificacionRestaurante.CalificacionRestauranteDTO;
import utn.back.mordiscoapi.model.dto.calificacionRestaurante.CalificacionRestauranteResponseDTO;
import utn.back.mordiscoapi.model.entity.CalificacionRestaurante;
import utn.back.mordiscoapi.model.entity.Restaurante;
import utn.back.mordiscoapi.model.entity.Usuario;

import java.time.LocalDateTime;

@UtilityClass
public class CalificacionRestauranteMapper {

    /**
     * Convierte un DTO de calificación de restaurante a una entidad de calificación de restaurante.
     * @param dto el DTO de calificación de restaurante a convertir
     * @return la entidad de calificación de restaurante con los datos del DTO
     */
    public static CalificacionRestaurante toCalificacionRestaurante(CalificacionRestauranteDTO dto) {
        Restaurante restaurante = Restaurante.builder()
                                    .id(dto.restauranteId())
                                    .build();
        Usuario usuario = Usuario.builder()
                        .id(dto.calificacionDTO().idUsuario())
                        .build();

        return CalificacionRestaurante.builder()
                .puntaje(dto.calificacionDTO().puntaje())
                .comentario(dto.calificacionDTO().comentario())
                .fechaHora(LocalDateTime.now())
                .restaurante(restaurante)
                .usuario(usuario)
                .build();
    }

    /**
     * Convierte una entidad de calificación de restaurante a un DTO de calificación de restaurante.
     * @param c la entidad de calificación de restaurante a convertir
     * @return el DTO de calificación de restaurante con los datos de la entidad
     */
    public static CalificacionRestauranteResponseDTO toDTO(CalificacionRestaurante c){
        return new CalificacionRestauranteResponseDTO(c.getId(),
                c.getFechaHora(),
                new CalificacionDTO(
                        c.getUsuario().getId(),
                        c.getPuntaje(),
                        c.getComentario()
                ));
    }
}