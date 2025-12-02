package utn.back.mordiscoapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.Pedido;
import utn.back.mordiscoapi.model.entity.Restaurante;

import java.util.Optional;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {
    //Buscar restaurante por id
    @Query("""
                SELECT DISTINCT r
                FROM Restaurante r
                LEFT JOIN  r.imagen
                LEFT JOIN  r.promociones
                LEFT JOIN  r.horariosAtencion
                LEFT JOIN  r.direccion
                WHERE r.id = :id
            """)
    Optional<Restaurante> findRestauranteById(@Param("id") Long id);

    //Buscar restaurante por duenio
    @Query("""
                SELECT DISTINCT r
                FROM Restaurante r
                LEFT JOIN  r.imagen
                LEFT JOIN  r.promociones
                LEFT JOIN  r.horariosAtencion
                LEFT JOIN  r.direccion
                WHERE r.usuario.id = :usuarioId
            """)
    Optional<Restaurante> findByIdUsuario(@Param("usuarioId") Long usuarioId);


    @Query("""
                SELECT DISTINCT r
                FROM Restaurante r
                LEFT JOIN  r.imagen
                LEFT JOIN  r.promociones
                LEFT JOIN  r.horariosAtencion
                LEFT JOIN  r.direccion
            """)
    Page<Restaurante> findAllRestaurante(Pageable pageable);

    @Query ("""
                SELECT DISTINCT r
                FROM Restaurante r
                LEFT JOIN  r.imagen
                LEFT JOIN  r.promociones
                LEFT JOIN  r.horariosAtencion
                LEFT JOIN  r.direccion
                WHERE r.activo = :estadoRestaurante
            """)
    Page<Restaurante> findAllByEstado(Pageable pageable,@Param("estadoRestaurante") Boolean estadoRestaurante);

    @Query("""
            SELECT DISTINCT r
            FROM Restaurante r
            LEFT JOIN  r.imagen
            LEFT JOIN  r.promociones
            LEFT JOIN  r.horariosAtencion
            LEFT JOIN  r.direccion
            WHERE r.direccion.ciudad = :ciudad
            AND r.activo = true
            """)
    Page<Restaurante> findAllByCiudad(Pageable pageable,@Param("ciudad") String ciudad);

    @Query("""
            SELECT DISTINCT r
            FROM Restaurante r
            LEFT JOIN  r.imagen
            LEFT JOIN  r.promociones
            LEFT JOIN  r.horariosAtencion
            LEFT JOIN  r.direccion
            WHERE r.razonSocial LIKE %:nombre%
            AND r.activo = true
            """)
    Page<Restaurante> findAllByNombre(Pageable pageable,@Param("nombre") String nombre);

    @Query("""
                SELECT DISTINCT r
                FROM Restaurante r
                LEFT JOIN r.imagen
                LEFT JOIN r.promociones p
                LEFT JOIN r.horariosAtencion
                LEFT JOIN r.direccion
                WHERE p.fechaInicio <= CURRENT_DATE
                AND p.fechaFin >= CURRENT_DATE
                AND r.direccion.ciudad = :ciudad
                AND r.activo = true
                """)
    Page<Restaurante> findAllWithPromocionActivaAndCiudad(Pageable pageable, @Param("ciudad") String ciudad);

    boolean existsByIdAndImagen_Id(Long id, Long imagenId);

    @Query("SELECT COUNT(p) FROM Pedido p " +
            "WHERE p.restaurante.id = :restauranteId " +
            "AND p.estado IN ('PENDIENTE', 'EN_PROCESO', 'EN_CAMINO')")
    long countPedidosActivos(@Param("restauranteId") Long restauranteId);

    @Query("SELECT p FROM Pedido p " +
            "WHERE p.restaurante.id = :restauranteId " +
            "AND p.estado IN ('PENDIENTE', 'EN_PROCESO', 'EN_CAMINO') " +
            "ORDER BY p.fechaHora DESC")
    Page<Pedido> findPedidosActivosByRestaurante(
            @Param("restauranteId") Long restauranteId,
            Pageable pageable
    );

    @Query(value = """
    SELECT r.*
    FROM restaurantes r
    LEFT JOIN direcciones d ON r.direccion_id = d.id
    LEFT JOIN menus m ON r.menu_id = m.id
    WHERE 
        -- FILTRO ACTIVO (BIT -> Boolean)
        (:activo IS NULL OR r.activo = :activo)
        
        -- BUSCADOR TEXTO LIBRE
        AND (
            :search IS NULL OR :search = '' 
            OR LOWER(r.razon_social) LIKE LOWER(CONCAT('%', :search, '%'))
            OR CAST(r.id AS CHAR) LIKE CONCAT('%', :search, '%')
            OR LOWER(d.calle) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(d.ciudad) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(d.codigo_postal) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(d.numero) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(m.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
        )
    ORDER BY r.razon_social ASC
    """,
            countQuery = """
    SELECT COUNT(*)
    FROM restaurantes r
    LEFT JOIN direcciones d ON r.direccion_id = d.id
    LEFT JOIN menus m ON r.menu_id = m.id
    WHERE 
        (:activo IS NULL OR r.activo = :activo)
        AND (
            :search IS NULL OR :search = '' 
            OR LOWER(r.razon_social) LIKE LOWER(CONCAT('%', :search, '%'))
            OR CAST(r.id AS CHAR) LIKE CONCAT('%', :search, '%')
            OR LOWER(d.calle) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(d.ciudad) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(d.codigo_postal) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(d.numero) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(m.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
        )
    """,
            nativeQuery = true)
    Page<Restaurante> filtrarRestaurantes(
            @Param("search") String search,
            @Param("activo") Boolean activo,
            Pageable pageable
    );

    /**
     * Busca restaurantes activos dentro de un radio específico desde una ubicación,
     * con búsqueda opcional por nombre, ordenados por estado abierto y distancia.
     * Usa la fórmula de Haversine para calcular distancias.
     *
     * @param latitud Latitud del punto de referencia
     * @param longitud Longitud del punto de referencia
     * @param radioKm Radio de búsqueda en kilómetros
     * @param searchTerm Término de búsqueda opcional (usar % para buscar todo)
     * @param pageable Configuración de paginación
     * @return Página de restaurantes dentro del radio
     */
    @Query(value = """
            SELECT DISTINCT r.*,
                   (6371 * acos(
                       cos(radians(:latitud)) *
                       cos(radians(d.latitud)) *
                       cos(radians(d.longitud) - radians(:longitud)) +
                       sin(radians(:latitud)) *
                       sin(radians(d.latitud))
                   )) AS distancia
            FROM restaurantes r
            LEFT JOIN direcciones d ON r.direccion_id = d.id
            LEFT JOIN imagenes i ON r.imagen_id = i.id
            LEFT JOIN horarios_atencion ha ON ha.restaurante_id = r.id
            WHERE r.activo = true
            AND d.latitud IS NOT NULL
            AND d.longitud IS NOT NULL
            AND r.razon_social LIKE :searchTerm
            AND (6371 * acos(
                cos(radians(:latitud)) *
                cos(radians(d.latitud)) *
                cos(radians(d.longitud) - radians(:longitud)) +
                sin(radians(:latitud)) *
                sin(radians(d.latitud))
            )) <= :radioKm
            ORDER BY
                CASE
                    WHEN EXISTS (
                        SELECT 1 FROM horarios_atencion ha2
                        WHERE ha2.restaurante_id = r.id
                        AND ha2.dia = DAYNAME(CURRENT_DATE)
                        AND CURRENT_TIME BETWEEN ha2.hora_apertura AND ha2.hora_cierre
                    ) THEN 0
                    ELSE 1
                END,
                distancia ASC
            """, 
            countQuery = """
            SELECT COUNT(DISTINCT r.id)
            FROM restaurantes r
            LEFT JOIN direcciones d ON r.direccion_id = d.id
            WHERE r.activo = true
            AND d.latitud IS NOT NULL
            AND d.longitud IS NOT NULL
            AND r.razon_social LIKE :searchTerm
            AND (6371 * acos(
                cos(radians(:latitud)) *
                cos(radians(d.latitud)) *
                cos(radians(d.longitud) - radians(:longitud)) +
                sin(radians(:latitud)) *
                sin(radians(d.latitud))
            )) <= :radioKm
            """,
            nativeQuery = true)
    Page<Restaurante> findByLocationWithinRadius(
            @Param("latitud") Double latitud,
            @Param("longitud") Double longitud,
            @Param("radioKm") Double radioKm,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );

    /**
     * Busca restaurantes con promociones activas dentro de un radio específico desde una ubicación,
     * ordenados por estado abierto y distancia.
     * Usa la fórmula de Haversine para calcular distancias.
     *
     * @param latitud Latitud del punto de referencia
     * @param longitud Longitud del punto de referencia
     * @param radioKm Radio de búsqueda en kilómetros
     * @param pageable Configuración de paginación
     * @return Página de restaurantes con promociones activas dentro del radio
     */
    @Query(value = """
            SELECT DISTINCT r.*,
                   (6371 * acos(
                       cos(radians(:latitud)) *
                       cos(radians(d.latitud)) *
                       cos(radians(d.longitud) - radians(:longitud)) +
                       sin(radians(:latitud)) *
                       sin(radians(d.latitud))
                   )) AS distancia
            FROM restaurantes r
            LEFT JOIN direcciones d ON r.direccion_id = d.id
            LEFT JOIN imagenes i ON r.imagen_id = i.id
            LEFT JOIN horarios_atencion ha ON ha.restaurante_id = r.id
            INNER JOIN promociones p ON p.restaurante_id = r.id
            WHERE r.activo = true
            AND d.latitud IS NOT NULL
            AND d.longitud IS NOT NULL
            AND p.fecha_inicio <= CURRENT_DATE
            AND p.fecha_fin >= CURRENT_DATE
            AND (6371 * acos(
                cos(radians(:latitud)) *
                cos(radians(d.latitud)) *
                cos(radians(d.longitud) - radians(:longitud)) +
                sin(radians(:latitud)) *
                sin(radians(d.latitud))
            )) <= :radioKm
            ORDER BY
                CASE
                    WHEN EXISTS (
                        SELECT 1 FROM horarios_atencion ha2
                        WHERE ha2.restaurante_id = r.id
                        AND ha2.dia = DAYNAME(CURRENT_DATE)
                        AND CURRENT_TIME BETWEEN ha2.hora_apertura AND ha2.hora_cierre
                    ) THEN 0
                    ELSE 1
                END,
                distancia ASC
            """, 
            countQuery = """
            SELECT COUNT(DISTINCT r.id)
            FROM restaurantes r
            LEFT JOIN direcciones d ON r.direccion_id = d.id
            INNER JOIN promociones p ON p.restaurante_id = r.id
            WHERE r.activo = true
            AND d.latitud IS NOT NULL
            AND d.longitud IS NOT NULL
            AND p.fecha_inicio <= CURRENT_DATE
            AND p.fecha_fin >= CURRENT_DATE
            AND (6371 * acos(
                cos(radians(:latitud)) *
                cos(radians(d.latitud)) *
                cos(radians(d.longitud) - radians(:longitud)) +
                sin(radians(:latitud)) *
                sin(radians(d.latitud))
            )) <= :radioKm
            """,
            nativeQuery = true)
    Page<Restaurante> findWithPromocionByLocationWithinRadius(
            @Param("latitud") Double latitud,
            @Param("longitud") Double longitud,
            @Param("radioKm") Double radioKm,
            Pageable pageable
    );

    /**
     * Encuentra los restaurantes más activos
     * @return Lista de restaurantes con más pedidos completados
     */
    @Query(value = """
            SELECT r.id, r.razon_social,
                   COUNT(p.id) as pedidos_completados,
                   COALESCE(SUM(p.subtotal_productos), 0) as ingreso_generado
            FROM restaurantes r
            LEFT JOIN pedidos p ON p.restaurante_id = r.id AND p.estado = 'COMPLETADO'
            WHERE r.activo = true
            GROUP BY r.id, r.razon_social
            ORDER BY pedidos_completados DESC
            LIMIT 10
            """, nativeQuery = true)
    List<Object[]> findRestaurantesMasActivos();

    /**
     * Calcula los ingresos totales de un restaurante
     * @param restauranteId ID del restaurante
     * @return Ingresos totales
     */
    @Query("SELECT COALESCE(SUM(p.subtotalProductos), 0) FROM Pedido p " +
           "WHERE p.restaurante.id = :restauranteId AND p.estado = 'COMPLETADO'")
    BigDecimal calcularIngresosTotalesRestaurante(@Param("restauranteId") Long restauranteId);

    /**
     * Calcula los ingresos por mes de un restaurante (últimos 12 meses)
     * @param restauranteId ID del restaurante
     * @return Lista de ingresos por mes
     */
    @Query(value = """
            SELECT DATE_FORMAT(p.fecha_hora, '%Y-%m') as periodo,
                   COALESCE(SUM(p.subtotal_productos), 0) as ingresos
            FROM pedidos p
            WHERE p.restaurante_id = :restauranteId
            AND p.estado = 'COMPLETADO'
            AND p.fecha_hora >= DATE_SUB(CURRENT_DATE, INTERVAL 12 MONTH)
            GROUP BY DATE_FORMAT(p.fecha_hora, '%Y-%m')
            ORDER BY periodo
            """, nativeQuery = true)
    List<Object[]> calcularIngresosPorMes(@Param("restauranteId") Long restauranteId);

}