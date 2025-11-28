package utn.back.mordiscoapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.GananciaRepartidor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface GananciaRepartidorRepository extends JpaRepository<GananciaRepartidor, Long> {

    /**
     * Encuentra todas las ganancias de un repartidor
     */
    Page<GananciaRepartidor> findByRepartidor_IdOrderByFechaRegistroDesc(
            Long repartidorId, 
            Pageable pageable
    );

    /**
     * Encuentra ganancias de un repartidor en un rango de fechas
     */
    @Query("SELECT g FROM GananciaRepartidor g WHERE g.repartidor.id = :repartidorId " +
           "AND g.fechaRegistro BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY g.fechaRegistro DESC")
    Page<GananciaRepartidor> findByRepartidorAndFechaRange(
            @Param("repartidorId") Long repartidorId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable
    );

    /**
     * Calcula el total de ganancias de un repartidor
     */
    @Query("SELECT COALESCE(SUM(g.gananciaRepartidor), 0) FROM GananciaRepartidor g " +
           "WHERE g.repartidor.id = :repartidorId")
    BigDecimal calcularTotalGanancias(@Param("repartidorId") Long repartidorId);

    /**
     * Calcula ganancias en un rango de fechas
     */
    @Query("SELECT COALESCE(SUM(g.gananciaRepartidor), 0) FROM GananciaRepartidor g " +
           "WHERE g.repartidor.id = :repartidorId " +
           "AND g.fechaRegistro BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal calcularGananciasEnRango(
            @Param("repartidorId") Long repartidorId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    /**
     * Cuenta total de entregas de un repartidor
     */
    @Query("SELECT COUNT(g) FROM GananciaRepartidor g WHERE g.repartidor.id = :repartidorId")
    Long contarEntregas(@Param("repartidorId") Long repartidorId);

    /**
     * Encuentra ganancia por pedido
     */
    @Query("SELECT g FROM GananciaRepartidor g WHERE g.pedido.id = :pedidoId")
    GananciaRepartidor findByPedidoId(@Param("pedidoId") Long pedidoId);
}
