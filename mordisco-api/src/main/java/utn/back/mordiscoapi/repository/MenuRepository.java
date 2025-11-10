package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.Menu;


@Repository
public interface MenuRepository extends JpaRepository<Menu,Long> {
}
