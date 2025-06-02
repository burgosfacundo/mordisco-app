package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.Menu;

import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu,Long> {
    @Query("""
               SELECT
                    m.id AS id,
                    m.nombre AS nombre,
                    p AS producto
               FROM Menu m
               JOIN Producto p ON p.menu.id = m.id
               WHERE m.id = :id
            """)
    Optional<Menu> findWithProductosById(Long id);
}
