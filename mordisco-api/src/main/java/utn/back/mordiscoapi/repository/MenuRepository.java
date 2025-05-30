package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.Menu;

import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu,Long> {
    @Query("""
              SELECT m
              FROM Menu m
              JOIN Restaurante r ON r.menu.id = m.id
              JOIN FETCH Producto p ON p.menu.id = m.id
              JOIN FETCH Imagen i ON i.id = p.imagen.id
              WHERE r.id = :restauranteId
            """)
    Optional<Menu> findByRestauranteId(Long restauranteId);
}
