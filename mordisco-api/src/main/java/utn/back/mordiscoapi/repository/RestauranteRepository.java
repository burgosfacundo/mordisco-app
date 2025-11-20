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
                LEFT JOIN  r.calificaciones
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
                LEFT JOIN  r.calificaciones
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
                LEFT JOIN  r.calificaciones
                LEFT JOIN  r.direccion
            """)
    Page<Restaurante> findAllRestaurante(Pageable pageable);

    @Query ("""
                SELECT DISTINCT r
                FROM Restaurante r
                LEFT JOIN  r.imagen
                LEFT JOIN  r.promociones
                LEFT JOIN  r.horariosAtencion
                LEFT JOIN  r.calificaciones
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
            LEFT JOIN  r.calificaciones
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
            LEFT JOIN  r.calificaciones
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
                LEFT JOIN r.calificaciones
                LEFT JOIN r.direccion
                WHERE p.fechaInicio <= CURRENT_DATE AND p.fechaFin >= CURRENT_DATE
                AND r.direccion.ciudad = :ciudad
                """)
    Page<Restaurante> findAllWithPromocionActivaAndCiudad(Pageable pageable, @Param("ciudad") String ciudad);

    boolean existsByIdAndImagen_Id(Long id, Long imagenId);

    boolean existsByIdAndDireccion_Id(Long id, Long direccionId);

    boolean existsByIdAndMenu_Id(Long id, Long menuId);

    /**
     * Cuenta los pedidos activos (PENDIENTE, EN_PROCESO, EN_CAMINO) de un restaurante
     *
     * @param restauranteId ID del restaurante
     * @return Cantidad de pedidos activos
     */
    @Query("SELECT COUNT(p) FROM Pedido p " +
            "WHERE p.restaurante.id = :restauranteId " +
            "AND p.estado IN ('PENDIENTE', 'EN_PROCESO', 'EN_CAMINO')")
    long countPedidosActivos(@Param("restauranteId") Long restauranteId);

    /**
     * Obtiene los pedidos activos de un restaurante (para mostrar al usuario)
     *
     * @param restauranteId ID del restaurante
     * @param pageable Paginación
     * @return Lista de pedidos activos
     */
    @Query("SELECT p FROM Pedido p " +
            "WHERE p.restaurante.id = :restauranteId " +
            "AND p.estado IN ('PENDIENTE', 'EN_PROCESO', 'EN_CAMINO') " +
            "ORDER BY p.fechaHora DESC")
    Page<Pedido> findPedidosActivosByRestaurante(
            @Param("restauranteId") Long restauranteId,
            Pageable pageable
    );
}
