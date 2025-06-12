package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.Restaurante;

import java.util.List;
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
    List<Restaurante> findAllRestaurante();

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
    List<Restaurante> findAllByEstado(@Param("estadoRestaurante") Boolean estadoRestaurante);

    @Query("""
            SELECT DISTINCT r
            FROM Restaurante r
            LEFT JOIN  r.imagen
            LEFT JOIN  r.promociones
            LEFT JOIN  r.horariosAtencion
            LEFT JOIN  r.calificaciones
            LEFT JOIN  r.direccion
            WHERE r.direccion.ciudad = :ciudad
            """)
    List<Restaurante> findAllByCiudad(@Param("ciudad") String ciudad);

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
    List<Restaurante> findAllByNombre(@Param("nombre") String nombre);

    @Query("""
                SELECT DISTINCT r
                FROM Restaurante r
                LEFT JOIN r.imagen
                LEFT JOIN r.promociones p
                LEFT JOIN r.horariosAtencion
                LEFT JOIN r.calificaciones
                LEFT JOIN r.direccion
                WHERE p.fechaInicio <= CURRENT_DATE AND p.fechaFin >= CURRENT_DATE
                """)
        List<Restaurante> findAllWithPromocionActiva();

    boolean existsByIdAndImagen_Id(Long id, Long imagenId);

    boolean existsByIdAndDireccion_Id(Long id, Long direccionId);
}
