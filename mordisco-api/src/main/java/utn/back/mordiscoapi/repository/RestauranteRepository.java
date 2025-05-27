package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.Restaurante;

import java.util.Optional;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {
    //Buscar restaurante por id
    @Query("""
                SELECT DISTINCT r
                FROM Restaurante r
                LEFT JOIN FETCH r.imagen
                LEFT JOIN FETCH r.promociones
                LEFT JOIN FETCH r.horariosAtencion
                LEFT JOIN FETCH r.calificaciones
                LEFT JOIN FETCH r.direccion
                WHERE r.id = :id
            """)
    Optional<Restaurante> findRestauranteById(@Param("id") Long id);

    //Buscar restaurante por duenio
    @Query("""
                SELECT DISTINCT r
                FROM Restaurante r
                LEFT JOIN FETCH r.imagen
                LEFT JOIN FETCH r.promociones
                LEFT JOIN FETCH r.horariosAtencion
                LEFT JOIN FETCH r.calificaciones
                LEFT JOIN FETCH r.direccion
                WHERE r.usuario.id = :usuarioId
            """)
    Optional<Restaurante> findByDuenio(@Param("usuarioId") Long usuarioId);


}
