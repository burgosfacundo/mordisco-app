package utn.back.mordiscoapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.CalificacionPedido;

import java.util.Optional;

@Repository
public interface CalificacionPedidoRepository extends JpaRepository<CalificacionPedido, Long> {

    /**
     * Buscar calificación por pedido
     */
    Optional<CalificacionPedido> findByPedidoId(Long pedidoId);

    /**
     * Verificar si un pedido ya fue calificado
     */
    boolean existsByPedidoId(Long pedidoId);

    /**
     * Obtener calificaciones de un restaurante (desde sus pedidos)
     */
    @Query("""
        SELECT cp FROM CalificacionPedido cp
        WHERE cp.pedido.restaurante.id = :restauranteId
        ORDER BY cp.fechaHora DESC
    """)
    Page<CalificacionPedido> findByRestauranteId(
            @Param("restauranteId") Long restauranteId,
            Pageable pageable
    );

    /**
     * Calcular promedio de calificaciones de un restaurante
     */
    @Query("""
        SELECT
            AVG((cp.puntajeComida + cp.puntajeTiempo + cp.puntajePackaging) / 3.0)
        FROM CalificacionPedido cp
        WHERE cp.pedido.restaurante.id = :restauranteId
    """)
    Double calcularPromedioRestaurante(@Param("restauranteId") Long restauranteId);

    /**
     * Contar calificaciones de un restaurante
     */
    @Query("""
        SELECT COUNT(cp) FROM CalificacionPedido cp
        WHERE cp.pedido.restaurante.id = :restauranteId
    """)
    Long contarCalificacionesRestaurante(@Param("restauranteId") Long restauranteId);

    /**
     * Obtener calificaciones de un cliente
     */
    Page<CalificacionPedido> findByUsuarioIdOrderByFechaHoraDesc(
            Long usuarioId,
            Pageable pageable
    );
}