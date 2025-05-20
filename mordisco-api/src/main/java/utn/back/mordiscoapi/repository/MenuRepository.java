package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.Menu;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu,Long> {
    @Query("""
               SELECT
                    m.id AS id,
                    m.nombre AS nombre,
                    m.productos AS productos
               FROM Menu m
            """)
    List<Menu> findAllProject();

    @Query("""
               SELECT
                    m.id AS id,
                    m.nombre AS nombre,
                    m.productos AS productos
               FROM Menu m
               WHERE m.id = :id
            """)
    Optional<Menu> findProjectById(Long id);
}
