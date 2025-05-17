package utn.back.mordiscoapi.repository;

import jakarta.persistence.Column;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import utn.back.mordiscoapi.model.entity.Direccion;
import utn.back.mordiscoapi.model.projection.DireccionProjection;

import java.util.List;

public interface DireccionRepository extends JpaRepository<Direccion,Long> {
    @Query("""
        SELECT
            d.id=id,
            d.calle=calle,
            d.numero=numero;
            d.piso=piso;
            d.codigoPostal=codigoPostal,
            d.referencias=referencias,
            d.latitud=latitud,
            d.longitud=longitud,
            d.ciudad=ciudad
        FROM
            direcciones as d
    """)
    List<DireccionProjection> findAllProject();

    @Query("""
        SELECT
            d.id=id,
            d.calle=calle,
            d.numero=numero;
            d.piso=piso;
            d.codigoPostal=codigoPostal,
            d.referencias=referencias,
            d.latitud=latitud,
            d.longitud=longitud,
            d.ciudad=ciudad
        FROM
            direcciones as d
        WHERE
            d.id= :id; 
            """)
    DireccionProjection findProjectyById(Long id);
}
