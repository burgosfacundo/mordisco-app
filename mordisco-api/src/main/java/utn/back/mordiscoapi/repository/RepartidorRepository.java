package utn.back.mordiscoapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.Pedido;
import utn.back.mordiscoapi.model.entity.Usuario;

import java.util.List;

@Repository
public interface RepartidorRepository extends JpaRepository<Usuario, Long> {

    @Query(value =
            "SELECT u.* FROM usuarios u " +
                    "INNER JOIN roles r ON u.rol_id = r.id " +
                    "WHERE r.nombre = 'ROLE_REPARTIDOR' " +
                    "AND u.disponible = TRUE " +
                    "AND u.latitud_actual IS NOT NULL " +
                    "AND u.longitud_actual IS NOT NULL " +
                    "AND ( " +
                    "  6371 * acos( " +
                    "    cos(radians(:latitud)) * cos(radians(u.latitud_actual)) * " +
                    "    cos(radians(u.longitud_actual) - radians(:longitud)) + " +
                    "    sin(radians(:latitud)) * sin(radians(u.latitud_actual)) " +
                    "  ) " +
                    ") <= :radioKm " +
                    "ORDER BY ( " +
                    "  6371 * acos( " +
                    "    cos(radians(:latitud)) * cos(radians(u.latitud_actual)) * " +
                    "    cos(radians(u.longitud_actual) - radians(:longitud)) + " +
                    "    sin(radians(:latitud)) * sin(radians(u.latitud_actual)) " +
                    "  ) " +
                    ")",
            nativeQuery = true
    )
    List<Usuario> findRepartidoresDisponiblesCercanos(
            @Param("latitud") Double latitud,
            @Param("longitud") Double longitud,
            @Param("radioKm") Double radioKm
    );

    @Query(
            value = "SELECT * FROM pedidos p WHERE p.repartidor_id = :idR",
            nativeQuery = true)
    Page<Pedido> findPedidosByRepartidor(@Param("idR") Long id, Pageable pageable);

    @Query(
            value = "SELECT * FROM pedidos p WHERE p.repartidor_id = :idR AND p.estado = 'EN_CAMINO'",
            nativeQuery = true)
    Page<Pedido> findPedidosAEntregarByRepartidor(@Param("idR") Long id, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Pedido p " +
            "WHERE p.repartidor.id = :repartidorId " +
            "AND p.estado = 'RECIBIDO'")
    Long countEntregasCompletadas(@Param("repartidorId") Long repartidorId);

    @Query("SELECT COALESCE(SUM(p.costoDelivery * " +
            "  (SELECT c.porcentajeGananciasRepartidor FROM ConfiguracionSistema c ORDER BY c.id DESC LIMIT 1) " +
            "  / 100), 0) " +
            "FROM Pedido p " +
            "WHERE p.repartidor.id = :repartidorId " +
            "AND p.estado = 'RECIBIDO'")
    Double calcularTotalGanado(@Param("repartidorId") Long repartidorId);
}