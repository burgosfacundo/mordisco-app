package utn.back.mordiscoapi.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import utn.back.mordiscoapi.enums.TipoEntrega;
import utn.back.mordiscoapi.repository.PedidoRepository;
import utn.back.mordiscoapi.security.jwt.utils.AuthUtils;

@Component("pedidoSecurity")
@RequiredArgsConstructor
public class PedidoSecurity {
    private final AuthUtils authUtils;
    private final PedidoRepository pedidoRepository;

    /**
     * Verifica si el usuario autenticado es el propietario del pedido.
     *
     * @param idPedido ID del pedido a verificar
     * @return true si el usuario es el propietario, false en caso contrario
     */
    public boolean esPropietarioPedido(Long idPedido) {
        return authUtils.getUsuarioAutenticado()
                .map(usuario -> pedidoRepository.existsByIdAndCliente_Id(idPedido, usuario.getId()))
                .orElse(false);
    }

    /**
     * Verifica si el usuario autenticado es el propietario del restaurante del pedido.
     *
     * @param idPedido ID del pedido a verificar
     * @return true si el usuario es el propietario del restaurante, false en caso contrario
     */
    public boolean esPropietarioRestaurantePedido(Long idPedido) {
        return authUtils.getUsuarioAutenticado()
                .flatMap(usuario -> pedidoRepository.findById(idPedido)
                        .map(pedido -> usuario.getRestaurante() != null &&
                                pedido.getRestaurante().getUsuario().getId().equals(usuario.getId())))
                .orElse(false);
    }

    /**
     * Verifica si el usuario autenticado es el repartidor asignado a un pedido DELIVERY.
     *
     * @param idPedido ID del pedido a verificar
     * @return true si el usuario es el repartidor asignado, false en caso contrario
     */
    public boolean esRepartidorAsignadoPedidoDelivery(Long idPedido) {
        return authUtils.getUsuarioAutenticado()
                .flatMap(usuario -> pedidoRepository.findById(idPedido)
                        .map(pedido -> TipoEntrega.DELIVERY.equals(pedido.getTipoEntrega())
                                && pedido.getRepartidor() != null
                                && usuario.getId() != null
                                && pedido.getRepartidor().getId() != null
                                && usuario.getId().equals(pedido.getRepartidor().getId())))
                .orElse(false);
    }

    /**
     * Verifica si el usuario autenticado es el propietario del restaurante de un pedido para retirar.
     *
     * @param idPedido ID del pedido a verificar
     * @return true si el usuario es el propietario del restaurante, false en caso contrario
     */
    public boolean esPropietarioRestaurantePedidoRetiro(Long idPedido) {
        return authUtils.getUsuarioAutenticado()
                .flatMap(usuario -> pedidoRepository.findById(idPedido)
                        .map(pedido -> TipoEntrega.RETIRO_POR_LOCAL.equals(pedido.getTipoEntrega())
                                && pedido.getRestaurante() != null
                                && pedido.getRestaurante().getUsuario() != null
                                && usuario.getId() != null
                                && pedido.getRestaurante().getUsuario().getId() != null
                                && usuario.getId().equals(pedido.getRestaurante().getUsuario().getId())))
                .orElse(false);
    }
}
