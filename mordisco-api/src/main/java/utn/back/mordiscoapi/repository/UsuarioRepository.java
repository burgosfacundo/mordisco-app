package utn.back.mordiscoapi.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.Pedido;
import utn.back.mordiscoapi.model.entity.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Page<Usuario> findUsuarioByRol_Id(Pageable pageable,Long rolId);

    Optional<Usuario> findByEmail(String email);

    /**
     * Cuenta los pedidos activos de un usuario (como cliente)
     * Estados activos: todos excepto COMPLETADO y CANCELADO
     *
     * @param usuarioId ID del usuario
     * @return Cantidad de pedidos activos
     */
    @Query("SELECT COUNT(p) FROM Pedido p " +
            "WHERE p.cliente.id = :usuarioId " +
            "AND p.estado NOT IN ('COMPLETADO', 'CANCELADO')")
    long countPedidosActivosComoCliente(@Param("usuarioId") Long usuarioId);

    /**
     * Obtiene los pedidos activos de un usuario como cliente
     * Estados activos: todos excepto COMPLETADO y CANCELADO
     *
     * @param usuarioId ID del usuario
     * @param pageable Paginación
     * @return Lista de pedidos activos
     */
    @Query("SELECT p FROM Pedido p " +
            "WHERE p.cliente.id = :usuarioId " +
            "AND p.estado NOT IN ('COMPLETADO', 'CANCELADO') " +
            "ORDER BY p.fechaHora DESC")
    Page<Pedido> findPedidosActivosComoCliente(
            @Param("usuarioId") Long usuarioId,
            Pageable pageable
    );

    boolean existsByEmail(@NotBlank(message = "El email es obligatorio") @Email(message = "Formato de email inválido") String email);

    boolean existsByTelefono(@NotBlank(message = "El teléfono es obligatorio") @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Formato de teléfono inválido") String telefono);


    @Query(value = """
SELECT u.*
FROM usuarios u
INNER JOIN roles r ON u.rol_id = r.id
WHERE
    -- FILTRO BAJA LOGICA (BIT -> Boolean)
    (:bajaLogica IS NULL OR u.baja_logica = :bajaLogica)

    -- FILTRO ROL
    AND (:rol IS NULL OR :rol = '' OR r.nombre = :rol)

    -- BUSCADOR TEXTO LIBRE
    AND (
        :search IS NULL OR :search = ''
        OR LOWER(CONCAT(u.nombre, ' ', u.apellido)) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(CONCAT(u.apellido, ' ', u.nombre)) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(u.apellido) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
        OR CAST(u.id AS CHAR) LIKE CONCAT('%', :search, '%')
        OR CAST(u.telefono AS CHAR) LIKE CONCAT('%', :search, '%')
    )
""",
            countQuery = """
SELECT COUNT(*)
FROM usuarios u
INNER JOIN roles r ON u.rol_id = r.id
WHERE
    (:bajaLogica IS NULL OR u.baja_logica = :bajaLogica)
    AND (:rol IS NULL OR :rol = '' OR r.nombre = :rol)
    AND (
        :search IS NULL OR :search = ''
        OR LOWER(CONCAT(u.nombre, ' ', u.apellido)) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(CONCAT(u.apellido, ' ', u.nombre)) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(u.apellido) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
        OR CAST(u.id AS CHAR) LIKE CONCAT('%', :search, '%')
        OR LOWER(u.telefono) LIKE CONCAT('%', :search, '%')
    )
""",
            nativeQuery = true)
    Page<Usuario> filtrarUsuario(
            @Param("search") String search,
            @Param("bajaLogica") Boolean bajaLogica,
            @Param("rol") String rol,
            Pageable pageable
    );

    /**
     * Cuenta usuarios activos por rol (excluye usuarios con baja lógica)
     * @param rolId ID del rol
     * @return Cantidad de usuarios activos con ese rol
     */
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol.id = :rolId AND u.bajaLogica = false")
    Integer countByRolId(@Param("rolId") Long rolId);

    /**
     * Encuentra los repartidores más activos
     * @return Lista de repartidores con más entregas
     */
    @Query(value = """
            SELECT u.id, u.nombre, u.apellido,
                   COUNT(p.id) as entregas_realizadas,
                   COALESCE(SUM(gr.ganancia_repartidor), 0) as ganancia_generada
            FROM usuarios u
            LEFT JOIN pedidos p ON p.repartidor_id = u.id AND p.estado = 'COMPLETADO'
            LEFT JOIN ganancias_repartidor gr ON gr.repartidor_id = u.id
            WHERE u.rol_id = (SELECT id FROM roles WHERE nombre = 'REPARTIDOR')
              AND u.baja_logica = false
            GROUP BY u.id, u.nombre, u.apellido
            HAVING COUNT(p.id) > 0
            ORDER BY entregas_realizadas DESC
            LIMIT 10
            """, nativeQuery = true)
    List<Object[]> findRepartidoresMasActivos();

    /**
     * Cuenta los pedidos activos de un usuario (como repartidor)
     * Estados activos: todos excepto COMPLETADO y CANCELADO
     *
     * @param usuarioId ID del usuario
     * @return Cantidad de pedidos activos
     */
    @Query("SELECT COUNT(p) FROM Pedido p " +
            "WHERE p.repartidor.id = :usuarioId " +
            "AND p.estado NOT IN ('COMPLETADO', 'CANCELADO')")
    long countPedidosActivosComoRepartidor(@Param("usuarioId") Long usuarioId);

    /**
     * Cuenta los pedidos activos del restaurante de un usuario
     * Estados activos: todos excepto COMPLETADO y CANCELADO
     *
     * @param usuarioId ID del usuario (debe tener rol RESTAURANTE)
     * @return Cantidad de pedidos activos del restaurante
     */
    @Query("SELECT COUNT(p) FROM Pedido p " +
            "WHERE p.restaurante.usuario.id = :usuarioId " +
            "AND p.estado NOT IN ('COMPLETADO', 'CANCELADO')")
    long countPedidosActivosDeRestaurante(@Param("usuarioId") Long usuarioId);
}
