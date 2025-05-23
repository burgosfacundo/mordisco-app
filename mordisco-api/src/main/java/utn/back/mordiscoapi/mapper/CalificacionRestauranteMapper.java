package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.calificacionRestaurante.CalificacionRestauranteDTO;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioDTO;
import utn.back.mordiscoapi.model.entity.CalificacionRestaurante;
import utn.back.mordiscoapi.model.entity.Restaurante;
import utn.back.mordiscoapi.model.entity.Rol;
import utn.back.mordiscoapi.model.entity.Usuario;

@UtilityClass
public class CalificacionRestauranteMapper {
    /**
     * Convierte un DTO de calificacion de restaurante a una entidad de calificacion de restaurante.
     * @param dto el DTO de calificacion de restaurante a convertir
     * @return la entidad de calificacion de restaurante con los datos del DTO
     */
    public static CalificacionRestaurante toCalificacionRestaurante(CalificacionRestauranteDTO dto) {
        Restaurante restaurante = Restaurante.builder()
                                    .id(dto.restauranteId())
                                    .build();
        Usuario usuario = Usuario.builder()
                        .id(dto.usuarioId())
                        .build();

        return CalificacionRestaurante.builder()
                .puntaje(dto.puntaje())
                .comentario(dto.comentario())
                .fechaHora(dto.fechaHora())
                .restaurante(restaurante)
                .usuario(usuario)
                .build();
    }
}