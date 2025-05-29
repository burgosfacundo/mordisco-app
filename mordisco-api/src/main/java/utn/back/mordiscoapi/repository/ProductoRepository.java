package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import utn.back.mordiscoapi.model.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
