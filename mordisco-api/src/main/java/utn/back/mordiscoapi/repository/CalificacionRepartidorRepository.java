package utn.back.mordiscoapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.CalificacionRepartidor;

import java.util.Optional;

@Repository
public interface CalificacionRepartidorRepository extends JpaRepository<CalificacionRepartidor, Long> {

    /**
     * Buscar calificación por pedido
     */
    Optional<CalificacionRepartidor> findByPedidoId(Long pedidoId);

    /**
     * Verificar si un repartidor ya fue calificado en un pedido
     */
    boolean existsByPedidoId(Long pedidoId);

    /**
     * Obtener calificaciones de un repartidor
     */
    Page<CalificacionRepartidor> findByRepartidorIdOrderByFechaHoraDesc(
            Long repartidorId,
            Pageable pageable
    );

    /**
     * Calcular promedio de calificaciones de un repartidor
     */
    @Query("""
        SELECT
            AVG((cr.puntajeAtencion + cr.puntajeComunicacion + cr.puntajeProfesionalismo) / 3.0)
        FROM CalificacionRepartidor cr
        WHERE cr.repartidor.id = :repartidorId
    """)
    Double calcularPromedioRepartidor(@Param("repartidorId") Long repartidorId);

    /**
     * Contar calificaciones de un repartidor
     */
    Long countByRepartidorId(Long repartidorId);

    /**
     * Obtener calificaciones realizadas por un cliente
     */
    Page<CalificacionRepartidor> findByUsuarioIdOrderByFechaHoraDesc(
            Long usuarioId,
            Pageable pageable
    );
}