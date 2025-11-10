package utn.back.mordiscoapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import utn.back.mordiscoapi.model.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    @Query("""
        SELECT
             p
        FROM Producto p
        INNER JOIN Imagen i ON p.imagen.id = i.id
        WHERE p.menu.id = :idMenu
        """)
    Page<Producto> findAllByIdMenu(Pageable pageable,Long idMenu);
}
