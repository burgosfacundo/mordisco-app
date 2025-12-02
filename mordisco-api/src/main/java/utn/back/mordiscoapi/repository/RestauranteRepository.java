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
}
