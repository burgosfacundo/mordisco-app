package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.Menu;

import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu,Long> {
    @Query("""
         select distinct m
         from Restaurante r
           join r.menu m
           left join fetch m.productos p
           left join fetch p.imagen
         where r.id = :restauranteId
         """)
    Optional<Menu> findByRestauranteId(Long restauranteId);
}
