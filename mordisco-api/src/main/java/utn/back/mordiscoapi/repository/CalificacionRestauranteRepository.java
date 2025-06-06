package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import utn.back.mordiscoapi.model.entity.CalificacionRestaurante;
import utn.back.mordiscoapi.model.projection.CalificacionRestauranteProjection;

import java.util.List;

public interface CalificacionRestauranteRepository extends JpaRepository<CalificacionRestaurante, Long> {

    @Query("""
    SELECT
        c.id AS id,
        c.puntaje AS puntaje,
        c.comentario AS comentario,
        c.fechaHora AS fecha,
        c.restaurante.id AS restauranteId,
        c.usuario.id AS usuarioId
    FROM CalificacionRestaurante c
""")
    List<CalificacionRestauranteProjection> findAllProjection();
}
