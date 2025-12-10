package utn.back.mordiscoapi.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.model.entity.Pedido;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Repository
public interface PedidoRepository extends JpaRepository<Pedido,Long> {

    Page<Pedido> findAllByRestaurante_Id(Pageable pageable, @Param("id")Long id);

    Page<Pedido> findAllByCliente_IdAndEstado(Pageable pageable,@Param("id") Long id, @Param("estado") EstadoPedido estado);

    Page<Pedido> findAllByCliente_Id(Pageable pageable,Long id);

    Page<Pedido> findAllByRestaurante_IdAndEstado(Pageable pageable,@Param("id")Long id, @Param("estado") EstadoPedido estado);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Pedido SET estado = :nuevoEstado WHERE id =:id
            """)
    void changeState(@Param("id") Long id, @Param("nuevoEstado") EstadoPedido nuevoEstado);

    boolean existsByCliente_IdAndRestaurante_Id(Long clienteId, Long restauranteId);

    boolean existsByIdAndCliente_Id(Long id, Long clienteId);
    @Modifying
    @Query("""
            UPDATE Pedido p SET p.direccionEntrega = NULL
            WHERE p.direccionEntrega.id = :direccionId""")
    void desasociarDireccion(@Param("direccionId") Long direccionId);


    /**
     * Encuentra pedidos disponibles para repartidores cerca de una ubicación
     * Calcula la distancia TOTAL del recorrido:
     * 1. Desde ubicación del repartidor hasta el RESTAURANTE (recogida)
     * 2. Desde el RESTAURANTE hasta la DIRECCIÓN DE ENTREGA
     */
    @Query(value =
            "SELECT p.* FROM pedidos p " +
                    "INNER JOIN restaurantes r ON p.restaurante_id = r.id " +
                    "INNER JOIN direcciones dr ON r.direccion_id = dr.id " +
                    "INNER JOIN direcciones de ON p.direccion_id = de.id " +
                    "WHERE p.estado = 'LISTO_PARA_ENTREGAR' " +
                    "AND p.tipo_entrega = 'DELIVERY' " +
                    "AND p.repartidor_id IS NULL " +
                    "AND dr.latitud IS NOT NULL " +
                    "AND dr.longitud IS NOT NULL " +
                    "AND de.latitud IS NOT NULL " +
                    "AND de.longitud IS NOT NULL " +
                    "AND ( " +
                    "  ( " +
                    "    6371 * acos( " +
                    "      cos(radians(:latitud)) * cos(radians(dr.latitud)) * " +
                    "      cos(radians(dr.longitud) - radians(:longitud)) + " +
                    "      sin(radians(:latitud)) * sin(radians(dr.latitud)) " +
                    "    ) " +
                    "  ) + ( " +
                    "    6371 * acos( " +
                    "      cos(radians(dr.latitud)) * cos(radians(de.latitud)) * " +
                    "      cos(radians(de.longitud) - radians(dr.longitud)) + " +
                    "      sin(radians(dr.latitud)) * sin(radians(de.latitud)) " +
                    "    ) " +
                    "  ) " +
                    ") <= :radioKm " +
                    "ORDER BY p.fecha_hora DESC",
            nativeQuery = true
    )
    Page<Pedido> findPedidosDisponiblesParaRepartidor(
            @Param("latitud") Double latitud,
            @Param("longitud") Double longitud,
            @Param("radioKm") Double radioKm,
            Pageable pageable
    );

    @Query(value = """
    SELECT p.*
    FROM pedidos p
    INNER JOIN usuarios u ON p.usuario_id = u.id
    INNER JOIN restaurantes r ON p.restaurante_id = r.id
    LEFT JOIN usuarios rep ON p.repartidor_id = rep.id
    WHERE 
        -- FILTRO ESTADO
        (:estado IS NULL OR :estado = '' OR p.estado = :estado)

        -- FILTRO TIPO ENTREGA
        AND (:tipoEntrega IS NULL OR :tipoEntrega = '' OR p.tipo_entrega = :tipoEntrega)

        -- FILTRO FECHA INICIO
        AND (:fechaInicio IS NULL OR p.fecha_hora >= :fechaInicio)

        -- FILTRO FECHA FIN
        AND (:fechaFin IS NULL OR p.fecha_hora <= :fechaFin)

        -- BUSCADOR TEXTO LIBRE
        AND (
                :search IS NULL OR :search = '' 
                OR LOWER(p.direccion_snapshot) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.estado) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.tipo_entrega) LIKE LOWER(CONCAT('%', :search, '%'))
                OR CAST(p.id AS CHAR) LIKE CONCAT('%', :search, '%')
                OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(u.apellido) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(r.razon_social) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(CONCAT(rep.nombre, ' ', rep.apellido)) LIKE LOWER(CONCAT('%', :search, '%'))
        )
    """,
            countQuery = """
    SELECT COUNT(*)
    FROM pedidos p
    INNER JOIN usuarios u ON p.usuario_id = u.id
    INNER JOIN restaurantes r ON p.restaurante_id = r.id
    LEFT JOIN usuarios rep ON p.repartidor_id = rep.id
    WHERE 
        (:estado IS NULL OR :estado = '' OR p.estado = :estado)
        AND (:tipoEntrega IS NULL OR :tipoEntrega = '' OR p.tipo_entrega = :tipoEntrega)
        AND (:fechaInicio IS NULL OR p.fecha_hora >= :fechaInicio)
        AND (:fechaFin IS NULL OR p.fecha_hora <= :fechaFin)
        AND (
                :search IS NULL OR :search = '' 
                OR LOWER(p.direccion_snapshot) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.estado) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.tipo_entrega) LIKE LOWER(CONCAT('%', :search, '%'))
                OR CAST(p.id AS CHAR) LIKE CONCAT('%', :search, '%')
                OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(u.apellido) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(r.razon_social) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(CONCAT(rep.nombre, ' ', rep.apellido)) LIKE LOWER(CONCAT('%', :search, '%'))
        )
    """,
            nativeQuery = true)
    Page<Pedido> filtrarPedidos(
            @Param("search") String search,
            @Param("estado") String estado,
            @Param("tipoEntrega") String tipoEntrega,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable
    );@Query(value = """

SELECT p.*
FROM pedidos p
INNER JOIN usuarios u ON p.usuario_id = u.id
INNER JOIN restaurantes r ON p.restaurante_id = r.id
LEFT JOIN usuarios rep ON p.repartidor_id = rep.id
WHERE 
    p.restaurante_id = :restauranteId
    AND (:estado IS NULL OR :estado = '' OR p.estado = :estado)
    AND (:tipoEntrega IS NULL OR :tipoEntrega = '' OR p.tipo_entrega = :tipoEntrega)
    AND (:fechaInicio IS NULL OR p.fecha_hora >= :fechaInicio)
    AND (:fechaFin IS NULL OR p.fecha_hora <= :fechaFin)
    AND (
        :search IS NULL OR :search = '' 
        OR LOWER(p.direccion_snapshot) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(p.estado) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(p.tipo_entrega) LIKE LOWER(CONCAT('%', :search, '%'))
        OR CAST(p.id AS CHAR) LIKE CONCAT('%', :search, '%')
        OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(u.apellido) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(CONCAT(rep.nombre, ' ', rep.apellido)) LIKE LOWER(CONCAT('%', :search, '%'))
    )
""",
            countQuery = """
SELECT COUNT(*)
FROM pedidos p
INNER JOIN usuarios u ON p.usuario_id = u.id
INNER JOIN restaurantes r ON p.restaurante_id = r.id
LEFT JOIN usuarios rep ON p.repartidor_id = rep.id
WHERE 
    p.restaurante_id = :restauranteId
    AND (:estado IS NULL OR :estado = '' OR p.estado = :estado)
    AND (:tipoEntrega IS NULL OR :tipoEntrega = '' OR p.tipo_entrega = :tipoEntrega)
    AND (:fechaInicio IS NULL OR p.fecha_hora >= :fechaInicio)
    AND (:fechaFin IS NULL OR p.fecha_hora <= :fechaFin)
    AND (
        :search IS NULL OR :search = '' 
        OR LOWER(p.direccion_snapshot) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(p.estado) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(p.tipo_entrega) LIKE LOWER(CONCAT('%', :search, '%'))
        OR CAST(p.id AS CHAR) LIKE CONCAT('%', :search, '%')
        OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(u.apellido) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(CONCAT(rep.nombre, ' ', rep.apellido)) LIKE LOWER(CONCAT('%', :search, '%'))
    )
""",
            nativeQuery = true)
    Page<Pedido> filtrarPedidosRestaurante(
                    @Param("restauranteId") Long restauranteId,
                    @Param("search") String search,
                    @Param("estado") String estado,
                    @Param("tipoEntrega") String tipoEntrega,
                    @Param("fechaInicio") LocalDateTime fechaInicio,
                    @Param("fechaFin") LocalDateTime fechaFin,
                    Pageable pageable
            );

    // Para CLIENTE - solo ve sus propios pedidos
    @Query(value = """
    SELECT p.*
    FROM pedidos p
    INNER JOIN usuarios u ON p.usuario_id = u.id
    INNER JOIN restaurantes r ON p.restaurante_id = r.id
    LEFT JOIN usuarios rep ON p.repartidor_id = rep.id
    WHERE 
        p.usuario_id = :clienteId
        AND (:tipoEntrega IS NULL OR :tipoEntrega = '' OR p.tipo_entrega = :tipoEntrega)
        AND (:fechaInicio IS NULL OR p.fecha_hora >= :fechaInicio)
        AND (:fechaFin IS NULL OR p.fecha_hora <= :fechaFin)
        AND (:estado IS NULL OR :estado = '' OR p.estado = :estado)   
        AND (
            :search IS NULL OR :search = '' 
            OR LOWER(p.direccion_snapshot) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(p.estado) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(p.tipo_entrega) LIKE LOWER(CONCAT('%', :search, '%'))
            OR CAST(p.id AS CHAR) LIKE CONCAT('%', :search, '%')
            OR LOWER(r.razon_social) LIKE LOWER(CONCAT('%', :search, '%'))
        )
    """,
            countQuery = """
    SELECT COUNT(*)
    FROM pedidos p
    INNER JOIN usuarios u ON p.usuario_id = u.id
    INNER JOIN restaurantes r ON p.restaurante_id = r.id
    LEFT JOIN usuarios rep ON p.repartidor_id = rep.id
    WHERE 
        p.usuario_id = :clienteId
        AND (:tipoEntrega IS NULL OR :tipoEntrega = '' OR p.tipo_entrega = :tipoEntrega)
        AND (:fechaInicio IS NULL OR p.fecha_hora >= :fechaInicio)
        AND (:fechaFin IS NULL OR p.fecha_hora <= :fechaFin)
        AND (:estado IS NULL OR :estado = '' OR p.estado = :estado)   
        AND (
            :search IS NULL OR :search = '' 
            OR LOWER(p.direccion_snapshot) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(p.estado) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(p.tipo_entrega) LIKE LOWER(CONCAT('%', :search, '%'))
            OR CAST(p.id AS CHAR) LIKE CONCAT('%', :search, '%')
            OR LOWER(r.razon_social) LIKE LOWER(CONCAT('%', :search, '%'))
        )
    """,
            nativeQuery = true)
    Page<Pedido> filtrarPedidosCliente(
            @Param("clienteId") Long clienteId,
            @Param("estado") String estado,
            @Param("search") String search,
            @Param("tipoEntrega") String tipoEntrega,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable
    );

    // Para REPARTIDOR - solo ve sus propios pedidos
    @Query(value = """
    SELECT p.*
    FROM pedidos p
    INNER JOIN usuarios u ON p.usuario_id = u.id
    INNER JOIN restaurantes r ON p.restaurante_id = r.id
    LEFT JOIN usuarios rep ON p.repartidor_id = rep.id
    WHERE 
        p.repartidor_id = :repartidorId
        AND (:estado IS NULL OR :estado = '' OR p.estado = :estado)
        AND (:fechaInicio IS NULL OR p.fecha_hora >= :fechaInicio)
        AND (:fechaFin IS NULL OR p.fecha_hora <= :fechaFin)
        AND (
            :search IS NULL OR :search = '' 
            OR LOWER(p.direccion_snapshot) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(p.estado) LIKE LOWER(CONCAT('%', :search, '%'))
            OR CAST(p.id AS CHAR) LIKE CONCAT('%', :search, '%')
            OR LOWER(r.razon_social) LIKE LOWER(CONCAT('%', :search, '%'))
        )
    """,
            countQuery = """
    SELECT COUNT(*)
    FROM pedidos p
    INNER JOIN usuarios u ON p.usuario_id = u.id
    INNER JOIN restaurantes r ON p.restaurante_id = r.id
    LEFT JOIN usuarios rep ON p.repartidor_id = rep.id
    WHERE 
        p.repartidor_id = :repartidorId
        AND (:estado IS NULL OR :estado = '' OR p.estado = :estado)
        AND (:fechaInicio IS NULL OR p.fecha_hora >= :fechaInicio)
        AND (:fechaFin IS NULL OR p.fecha_hora <= :fechaFin)
        AND (
            :search IS NULL OR :search = '' 
            OR LOWER(p.direccion_snapshot) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(p.estado) LIKE LOWER(CONCAT('%', :search, '%'))
            OR CAST(p.id AS CHAR) LIKE CONCAT('%', :search, '%')
            OR LOWER(r.razon_social) LIKE LOWER(CONCAT('%', :search, '%'))
        )
    """,
            nativeQuery = true)
    Page<Pedido> filtrarPedidosRepartidores(
            @Param("repartidorId") Long repartidorId,
            @Param("search") String search,
            @Param("estado") String estado,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable
    );

    /**
     * Cuenta el total de pedidos completados en la plataforma
     * @return Total de pedidos completados
     */
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado = 'COMPLETADO'")
    Integer countTotalPedidosCompletados();

    /**
     * Calcula las comisiones de la plataforma sobre ventas de restaurantes
     * Comisión = subtotal_productos * (100 - porcentajeGananciasRestaurante) / 100
     * @return Total de comisiones sobre restaurantes
     */
    @Query(value = """
            SELECT COALESCE(
                SUM(
                    p.subtotal_productos *
                    (100 - (SELECT cs.porcentaje_ganancias_restaurante
                            FROM configuracion_sistema cs
                            ORDER BY cs.fecha_actualizacion DESC
                            LIMIT 1)) / 100
                ), 0)
            FROM pedidos p
            WHERE p.estado = 'COMPLETADO'
            """, nativeQuery = true)
    java.math.BigDecimal calcularComisionRestaurantes();

    /**
     * Calcula las comisiones de la plataforma sobre delivery
     * Comisión = costo_delivery * (100 - porcentajeGananciasRepartidor) / 100
     * Solo para pedidos tipo DELIVERY
     * @return Total de comisiones sobre delivery
     */
    @Query(value = """
            SELECT COALESCE(
                SUM(
                    p.costo_delivery *
                    (100 - (SELECT cs.porcentaje_ganancias_repartidor
                            FROM configuracion_sistema cs
                            ORDER BY cs.fecha_actualizacion DESC
                            LIMIT 1)) / 100
                ), 0)
            FROM pedidos p
            WHERE p.estado = 'COMPLETADO'
            AND p.tipo_entrega = 'DELIVERY'
            AND p.costo_delivery IS NOT NULL
            """, nativeQuery = true)
    java.math.BigDecimal calcularComisionDelivery();

    /**
     * Calcula los ingresos totales de la plataforma (suma de comisiones de restaurantes + delivery)
     * @return Ingresos totales de la plataforma
     */
    @Query(value = """
            SELECT COALESCE(
                (SELECT SUM(
                    p.subtotal_productos *
                    (100 - (SELECT cs.porcentaje_ganancias_restaurante
                            FROM configuracion_sistema cs
                            ORDER BY cs.fecha_actualizacion DESC
                            LIMIT 1)) / 100
                )
                FROM pedidos p
                WHERE p.estado = 'COMPLETADO')
                +
                (SELECT SUM(
                    p.costo_delivery *
                    (100 - (SELECT cs.porcentaje_ganancias_repartidor
                            FROM configuracion_sistema cs
                            ORDER BY cs.fecha_actualizacion DESC
                            LIMIT 1)) / 100
                )
                FROM pedidos p
                WHERE p.estado = 'COMPLETADO'
                AND p.tipo_entrega = 'DELIVERY'
                AND p.costo_delivery IS NOT NULL)
            , 0)
            """, nativeQuery = true)
    java.math.BigDecimal calcularIngresosTotalesPlataforma();

    /**
     * Encuentra los métodos de pago más usados con estadísticas
     * Incluye todos los pedidos excepto CANCELADO (ya que el pago fue procesado)
     * @return Lista de métodos de pago con cantidad y porcentaje
     */
    @Query(value = """
            SELECT
                pago.metodo_pago,
                COUNT(*) as cantidad,
                (COUNT(*) * 100.0 / (SELECT COUNT(*) FROM pedidos WHERE estado != 'CANCELADO')) as porcentaje
            FROM pedidos p
            JOIN pagos pago ON p.id = pago.pedido_id
            WHERE p.estado != 'CANCELADO'
            GROUP BY pago.metodo_pago
            ORDER BY cantidad DESC
            """, nativeQuery = true)
    java.util.List<Object[]> findMetodosPagoMasUsados();

    /**
     * Calcula el tiempo promedio de preparación de un restaurante
     * @param restauranteId ID del restaurante
     * @return Tiempo promedio en minutos
     */
    @Query("""
            SELECT AVG(TIMESTAMPDIFF(MINUTE, p.fechaHora, 
                CASE 
                    WHEN p.tipoEntrega = 'DELIVERY' THEN p.fechaAceptacionRepartidor
                    ELSE p.fechaEntrega
                END))
            FROM Pedido p
            WHERE p.restaurante.id = :restauranteId
            AND p.estado = 'COMPLETADO'
            AND (
                (p.tipoEntrega = 'DELIVERY' AND p.fechaAceptacionRepartidor IS NOT NULL)
                OR (p.tipoEntrega = 'RETIRO_POR_LOCAL' AND p.fechaEntrega IS NOT NULL)
            )
            """)
    Double calcularTiempoPromedioPreparacion(@Param("restauranteId") Long restauranteId);

    /**
     * Calcula el tiempo promedio de entrega de un repartidor
     * @param repartidorId ID del repartidor
     * @return Tiempo promedio en minutos
     */
    @Query("""
            SELECT AVG(TIMESTAMPDIFF(MINUTE, p.fechaAceptacionRepartidor, p.fechaEntrega))
            FROM Pedido p
            WHERE p.repartidor.id = :repartidorId
            AND p.estado = 'COMPLETADO'
            AND p.fechaAceptacionRepartidor IS NOT NULL
            AND p.fechaEntrega IS NOT NULL
            """)
    Double calcularTiempoPromedioEntregaRepartidor(@Param("repartidorId") Long repartidorId);

    /**
     * Encuentra pedidos por día (últimos 30 días)
     * @param repartidorId ID del repartidor
     * @return Lista de pedidos por día
     */
    @Query(value = """
            SELECT DATE(p.fecha_hora) as periodo,
                   COUNT(*) as cantidad
            FROM pedidos p
            WHERE p.repartidor_id = :repartidorId
            AND p.estado = 'COMPLETADO'
            AND p.fecha_hora >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY)
            GROUP BY DATE(p.fecha_hora)
            ORDER BY periodo
            """, nativeQuery = true)
    java.util.List<Object[]> findPedidosPorDiaRepartidor(@Param("repartidorId") Long repartidorId);

    /**
     * Encuentra pedidos por semana (últimas 12 semanas)
     * @param repartidorId ID del repartidor
     * @return Lista de pedidos por semana
     */
    @Query(value = """
            SELECT CONCAT(YEAR(p.fecha_hora), '-W', LPAD(WEEK(p.fecha_hora, 1), 2, '0')) as periodo,
                   COUNT(*) as cantidad
            FROM pedidos p
            WHERE p.repartidor_id = :repartidorId
            AND p.estado = 'COMPLETADO'
            AND p.fecha_hora >= DATE_SUB(CURRENT_DATE, INTERVAL 12 WEEK)
            GROUP BY CONCAT(YEAR(p.fecha_hora), '-W', LPAD(WEEK(p.fecha_hora, 1), 2, '0'))
            ORDER BY periodo
            """, nativeQuery = true)
    java.util.List<Object[]> findPedidosPorSemanaRepartidor(@Param("repartidorId") Long repartidorId);

    /**
     * Encuentra pedidos por mes (últimos 12 meses)
     * @param repartidorId ID del repartidor
     * @return Lista de pedidos por mes
     */
    @Query(value = """
            SELECT DATE_FORMAT(p.fecha_hora, '%Y-%m') as periodo,
                   COUNT(*) as cantidad
            FROM pedidos p
            WHERE p.repartidor_id = :repartidorId
            AND p.estado = 'COMPLETADO'
            AND p.fecha_hora >= DATE_SUB(CURRENT_DATE, INTERVAL 12 MONTH)
            GROUP BY DATE_FORMAT(p.fecha_hora, '%Y-%m')
            ORDER BY periodo
            """, nativeQuery = true)
    java.util.List<Object[]> findPedidosPorMesRepartidor(@Param("repartidorId") Long repartidorId);

    /**
     * Encuentra un pedido por ID con sus relaciones cargadas (para eventos)
     * Carga: cliente, restaurante y usuario del restaurante
     */
    @Query("""
            SELECT p FROM Pedido p
            LEFT JOIN FETCH p.cliente
            LEFT JOIN FETCH p.restaurante r
            LEFT JOIN FETCH r.usuario
            WHERE p.id = :id
            """)
    java.util.Optional<Pedido> findByIdWithRelations(@Param("id") Long id);

}

