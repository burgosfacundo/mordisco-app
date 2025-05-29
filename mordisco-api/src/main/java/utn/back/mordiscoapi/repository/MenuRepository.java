package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.Menu;

import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu,Long> {

    @Modifying
    @Query("""
             DELETE FROM Menu m
             WHERE m.id = (SELECT r.menu.id FROM Restaurante r WHERE r.id = :restauranteId)
            """)
    void deleteByIdRestaurante(Long restauranteId);

    @Query("""
              SELECT m
              FROM Menu m
              JOIN Restaurante r ON r.menu.id = m.id
              WHERE r.id = :restauranteId
            """)
    Optional<Menu> findByRestauranteId(Long restauranteId);
}
