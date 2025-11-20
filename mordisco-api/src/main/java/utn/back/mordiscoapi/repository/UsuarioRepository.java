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

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Page<Usuario> findUsuarioByRol_Id(Pageable pageable,Long rolId);

    Optional<Usuario> findByEmail(String email);

    /**
     * Cuenta los pedidos activos de un usuario (como cliente)
     *
     * @param usuarioId ID del usuario
     * @return Cantidad de pedidos activos
     */
    @Query("SELECT COUNT(p) FROM Pedido p " +
            "WHERE p.cliente.id = :usuarioId " +
            "AND p.estado IN ('PENDIENTE', 'EN_PROCESO', 'EN_CAMINO')")
    long countPedidosActivosComoCliente(@Param("usuarioId") Long usuarioId);

    /**
     * Obtiene los pedidos activos de un usuario como cliente
     *
     * @param usuarioId ID del usuario
     * @param pageable Paginación
     * @return Lista de pedidos activos
     */
    @Query("SELECT p FROM Pedido p " +
            "WHERE p.cliente.id = :usuarioId " +
            "AND p.estado IN ('PENDIENTE', 'EN_PROCESO', 'EN_CAMINO') " +
            "ORDER BY p.fechaHora DESC")
    Page<Pedido> findPedidosActivosComoCliente(
            @Param("usuarioId") Long usuarioId,
            Pageable pageable
    );

    boolean existsByEmail(@NotBlank(message = "El email es obligatorio") @Email(message = "Formato de email inválido") String email);

    boolean existsByTelefono(@NotBlank(message = "El teléfono es obligatorio") @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Formato de teléfono inválido") String telefono);
}
