package utn.back.mordiscoapi.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.CalificacionProducto;

@Repository
public interface CalificacionProductoRepository extends JpaRepository<CalificacionProducto,Long> {
}
