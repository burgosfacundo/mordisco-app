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
}
