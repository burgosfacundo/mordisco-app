package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import utn.back.mordiscoapi.model.entity.Direccion;
import utn.back.mordiscoapi.model.projection.DireccionProjection;

import java.util.List;

public interface DireccionRepository extends JpaRepository<Direccion,Long> {
    @Query("""
        SELECT d FROM Direccion as d
    """)
    List<DireccionProjection> findAllProject();

    @Query("""
        SELECT d FROM Direccion as d WHERE d.id = :id
    """)
    DireccionProjection findProjectyById(Long id);
}
