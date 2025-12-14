package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.ConfiguracionSistema;

import java.util.Optional;

@Repository
public interface ConfiguracionSistemaRepository extends JpaRepository<ConfiguracionSistema, Long> {

    /**
     * Obtiene la configuración activa del sistema (siempre debe haber solo una)
     *
     * @return Configuración del sistema
     */
    @Query("SELECT c FROM ConfiguracionSistema c ORDER BY c.id DESC LIMIT 1")
    Optional<ConfiguracionSistema> findConfiguracionActual();

    /**
     * Verifica si existe una configuración en el sistema
     *
     * @return true si existe al menos una configuración
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM ConfiguracionSistema c")
    boolean existeConfiguracion();
}