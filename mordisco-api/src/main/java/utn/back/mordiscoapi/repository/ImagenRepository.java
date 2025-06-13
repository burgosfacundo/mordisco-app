package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import utn.back.mordiscoapi.model.entity.Imagen;

public interface ImagenRepository extends JpaRepository<Imagen, Long> {
}
