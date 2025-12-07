package utn.back.mordiscoapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import utn.back.mordiscoapi.model.entity.Promocion;
import utn.back.mordiscoapi.model.projection.PromocionProjection;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository // Anotación de repositorio de Spring para indicar que esta interfaz es un repositorio
public interface PromocionRepository extends JpaRepository<Promocion,Long> {
    // Definimos findAllProject que devuelve una lista de promociones proyectadas y paginadas
@Query("""
       SELECT
               p.id AS id,
               p.descripcion AS descripcion,
               p.descuento AS descuento,
               p.fechaInicio AS fechaInicio,
               p.fechaFin AS fechaFin,
               p.restaurante.id AS restaurante_Id
       FROM Promocion p
       """)
    Page<PromocionProjection> findAllProject(Pageable pageable);

    // Definimos findProjectedById que devuelve un Optional de una promoción proyectada por su id
    @Query("""
        SELECT
                p.id AS id,
                p.descripcion AS descripcion,
                p.descuento AS descuento,
                p.fechaInicio AS fechaInicio,
                p.fechaFin AS fechaFin,
                p.restaurante.id AS restaurante_Id
        FROM Promocion p
        WHERE p.id = :id
        """) // Anotación para realizar una query personalizada JPQL
    Optional<PromocionProjection> findProjectById(Long id);

    @Query("""
             SELECT p
             FROM Promocion p
             WHERE p.restaurante.id = :restauranteId
            """)
    Page<Promocion> findByRestauranteId(Pageable pageable,@Param("restauranteId") Long restauranteId);

    /**
     * Busca promociones activas de un restaurante en una fecha específica
     * @param restauranteId ID del restaurante
     * @param fecha Fecha para validar vigencia
     * @return Lista de promociones activas
     */
    @Query("""
            SELECT p
            FROM Promocion p
            LEFT JOIN FETCH p.productosAplicables
            WHERE p.restaurante.id = :restauranteId
            AND p.activa = true
            AND p.fechaInicio <= :fecha
            AND p.fechaFin >= :fecha
            """)
    List<Promocion> findPromocionesActivasByRestauranteAndFecha(
            @Param("restauranteId") Long restauranteId,
            @Param("fecha") java.time.LocalDate fecha
    );

    /**
     * Busca promociones vencidas de un restaurante específico
     */
    @Query("""
            SELECT p
            FROM Promocion p
            WHERE p.restaurante.id = :restauranteId
            AND p.activa = true
            AND p.fechaFin < :fecha
            """)
    List<Promocion> findByRestauranteIdAndActivaTrueAndFechaFinBefore(
            @Param("restauranteId") Long restauranteId,
            @Param("fecha") LocalDate fecha
    );

    @Modifying
    @Transactional
    @Query("""
    UPDATE Promocion p
    SET p.activa = false
    WHERE p.activa = true
    AND p.fechaFin < :fecha
    """)
    void desactivarPromocionesVencidas(@Param("fecha") LocalDate fecha);
}
