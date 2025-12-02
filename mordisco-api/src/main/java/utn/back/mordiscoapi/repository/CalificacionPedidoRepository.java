package utn.back.mordiscoapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.CalificacionPedido;
import utn.back.mordiscoapi.model.entity.Pedido;

import java.time.LocalDateTime;
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

    @Query("""
        SELECT
            AVG(cp.puntajeComida)
        FROM CalificacionPedido cp
        WHERE cp.pedido.restaurante.id = :restauranteId
    """)
    Double calcularPromedioComidaRestaurante(@Param("restauranteId") Long restauranteId);

    @Query("""
        SELECT
            AVG(cp.puntajeTiempo)
        FROM CalificacionPedido cp
        WHERE cp.pedido.restaurante.id = :restauranteId
    """)
    Double calcularPromedioTiempoRestaurante(@Param("restauranteId") Long restauranteId);

    @Query("""
        SELECT
            AVG(cp.puntajePackaging)
        FROM CalificacionPedido cp
        WHERE cp.pedido.restaurante.id = :restauranteId
    """)
    Double calcularPromedioPackagingRestaurante(@Param("restauranteId") Long restauranteId);

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

    @Query("""
        SELECT cp FROM CalificacionPedido cp
        WHERE cp.usuario.id = :usuarioId
        ORDER BY cp.fechaHora DESC
    """)
    Page<CalificacionPedido> findCalificacionesRealizadasPorCliente(
            @Param("usuarioId") Long usuarioId,
            Pageable pageable
    );

    @Query(value = """
    SELECT cp.*
    FROM calificaciones_pedido cp
    INNER JOIN usuarios u ON cp.usuario_id = u.id
    INNER JOIN pedidos p ON cp.pedido_id = p.id
    INNER JOIN restaurantes r ON p.restaurante_id = r.id
    WHERE 
        -- FILTRO PROMEDIO DE ESTRELLAS
        (:estrellas IS NULL OR :estrellas = '' 
         OR ROUND((cp.puntaje_comida + cp.puntaje_packaging + cp.puntaje_tiempo) / 3.0, 1) = :estrellas)
    
        -- FILTRO FECHA INICIO
        AND (:fechaInicio IS NULL OR cp.fecha_hora >= :fechaInicio)
    
        -- FILTRO FECHA FIN
        AND (:fechaFin IS NULL OR cp.fecha_hora <= :fechaFin)
    
        -- BUSCADOR TEXTO LIBRE
        AND (
            :search IS NULL OR :search = '' 
            OR LOWER(cp.comentario) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(u.apellido) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(CONCAT(u.nombre, ' ', u.apellido)) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(r.razon_social) LIKE LOWER(CONCAT('%', :search, '%'))
            OR CAST(cp.id AS CHAR) LIKE CONCAT('%', :search, '%')
        )
    ORDER BY cp.fecha_hora DESC
    """,
                countQuery = """
    SELECT COUNT(*)
    FROM calificaciones_pedido cp
    INNER JOIN usuarios u ON cp.usuario_id = u.id
    INNER JOIN pedidos p ON cp.pedido_id = p.id
    INNER JOIN restaurantes r ON p.restaurante_id = r.id
    WHERE 
        (:estrellas IS NULL OR :estrellas = '' 
         OR ROUND((cp.puntaje_comida + cp.puntaje_packaging + cp.puntaje_tiempo) / 3.0, 1) = :estrellas)
        AND (:fechaInicio IS NULL OR cp.fecha_hora >= :fechaInicio)
        AND (:fechaFin IS NULL OR cp.fecha_hora <= :fechaFin)
        AND (
            :search IS NULL OR :search = '' 
            OR LOWER(cp.comentario) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(u.apellido) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(CONCAT(u.nombre, ' ', u.apellido)) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(r.razon_social) LIKE LOWER(CONCAT('%', :search, '%'))
            OR CAST(cp.id AS CHAR) LIKE CONCAT('%', :search, '%')
        )
    """,
            nativeQuery = true)
    Page<CalificacionPedido> filtrarCalificaciones(
            @Param("search") String search,
            @Param("estrellas") String estrellas,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable
    );
}