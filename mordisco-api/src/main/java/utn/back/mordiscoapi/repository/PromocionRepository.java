package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.Promocion;
import utn.back.mordiscoapi.model.projection.PromocionProjection;

import java.util.List;
import java.util.Optional;

@Repository // Anotación de repositorio de Spring para indicar que esta interfaz es un repositorio
public interface PromocionRepository extends JpaRepository<Promocion,Long> {
    // Definimos findAllProject que devuelve una lista de promociones proyectadas y paginadas
    @Query("""
        SELECT
              p.id AS id,
              p.descuento AS descuento,
              p.descripcion AS descripcion,
              p.fechaInicio AS fechaInicio,
              p.fechaFin AS fechaFin
        FROM Promocion p
        """) // Anotación para realizar una query personalizada JPQL
    List<PromocionProjection> findAllProject();

    // Definimos findProjectedById que devuelve un Optional de una promoción proyectada por su id
    @Query("""
        SELECT
              p.id AS id,
              p.descuento AS descuento,
              p.descripcion AS descripcion,
              p.fechaInicio AS fechaInicio,
              p.fechaFin AS fechaFin
        FROM Promocion p
        WHERE p.id = :id
        """) // Anotación para realizar una query personalizada JPQL
    Optional<PromocionProjection> findProjectById(Long id);

    @Query("""
             SELECT p
                FROM Promocion p
                WHERE p.restaurante.id = :restauranteId
            """)
    List<Promocion> findByRestauranteId(@Param("restauranteId") Long restauranteId);
}
