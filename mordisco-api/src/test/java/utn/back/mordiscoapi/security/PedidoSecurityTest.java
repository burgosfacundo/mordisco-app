package utn.back.mordiscoapi.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utn.back.mordiscoapi.enums.TipoEntrega;
import utn.back.mordiscoapi.model.entity.Pedido;
import utn.back.mordiscoapi.model.entity.Restaurante;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.repository.PedidoRepository;
import utn.back.mordiscoapi.security.jwt.utils.AuthUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoSecurityTest {

    private static final Long ORDER_ID = 10L;

    @Mock
    private AuthUtils authUtils;

    @Mock
    private PedidoRepository pedidoRepository;

    private PedidoSecurity pedidoSecurity;

    @BeforeEach
    void setUp() {
        pedidoSecurity = new PedidoSecurity(authUtils, pedidoRepository);
    }

    @Test
    void assignedCourierCanAccessDeliveryOrder() {
        Usuario assignedCourier = user(20L);
        Pedido pedido = Pedido.builder()
                .tipoEntrega(TipoEntrega.DELIVERY)
                .repartidor(user(20L))
                .build();
        authenticatedAs(assignedCourier);
        when(pedidoRepository.findById(ORDER_ID)).thenReturn(Optional.of(pedido));

        assertTrue(pedidoSecurity.esRepartidorAsignadoPedidoDelivery(ORDER_ID));
    }

    @Test
    void rejectsAnotherCourierNoCourierAndNonDeliveryOrders() {
        authenticatedAs(user(20L));

        when(pedidoRepository.findById(ORDER_ID)).thenReturn(Optional.of(Pedido.builder()
                .tipoEntrega(TipoEntrega.DELIVERY)
                .repartidor(user(21L))
                .build()));
        assertFalse(pedidoSecurity.esRepartidorAsignadoPedidoDelivery(ORDER_ID));

        when(pedidoRepository.findById(ORDER_ID)).thenReturn(Optional.of(Pedido.builder()
                .tipoEntrega(TipoEntrega.DELIVERY)
                .build()));
        assertFalse(pedidoSecurity.esRepartidorAsignadoPedidoDelivery(ORDER_ID));

        when(pedidoRepository.findById(ORDER_ID)).thenReturn(Optional.of(Pedido.builder()
                .tipoEntrega(TipoEntrega.RETIRO_POR_LOCAL)
                .repartidor(user(20L))
                .build()));
        assertFalse(pedidoSecurity.esRepartidorAsignadoPedidoDelivery(ORDER_ID));
    }

    @Test
    void pickupRestaurantOwnerCanAccessPickupOrder() {
        Usuario restaurantOwner = user(30L);
        Pedido pedido = Pedido.builder()
                .tipoEntrega(TipoEntrega.RETIRO_POR_LOCAL)
                .restaurante(Restaurante.builder().usuario(user(30L)).build())
                .build();
        authenticatedAs(restaurantOwner);
        when(pedidoRepository.findById(ORDER_ID)).thenReturn(Optional.of(pedido));

        assertTrue(pedidoSecurity.esPropietarioRestaurantePedidoRetiro(ORDER_ID));
    }

    @Test
    void rejectsAnotherRestaurantDeliveryAndMissingRestaurant() {
        authenticatedAs(user(30L));

        when(pedidoRepository.findById(ORDER_ID)).thenReturn(Optional.of(Pedido.builder()
                .tipoEntrega(TipoEntrega.RETIRO_POR_LOCAL)
                .restaurante(Restaurante.builder().usuario(user(31L)).build())
                .build()));
        assertFalse(pedidoSecurity.esPropietarioRestaurantePedidoRetiro(ORDER_ID));

        when(pedidoRepository.findById(ORDER_ID)).thenReturn(Optional.of(Pedido.builder()
                .tipoEntrega(TipoEntrega.DELIVERY)
                .restaurante(Restaurante.builder().usuario(user(30L)).build())
                .build()));
        assertFalse(pedidoSecurity.esPropietarioRestaurantePedidoRetiro(ORDER_ID));

        when(pedidoRepository.findById(ORDER_ID)).thenReturn(Optional.of(Pedido.builder()
                .tipoEntrega(TipoEntrega.RETIRO_POR_LOCAL)
                .build()));
        assertFalse(pedidoSecurity.esPropietarioRestaurantePedidoRetiro(ORDER_ID));
    }

    @Test
    void rejectsMissingOrderAndAuthentication() {
        when(authUtils.getUsuarioAutenticado()).thenReturn(Optional.empty());
        assertFalse(pedidoSecurity.esRepartidorAsignadoPedidoDelivery(ORDER_ID));
        assertFalse(pedidoSecurity.esPropietarioRestaurantePedidoRetiro(ORDER_ID));

        authenticatedAs(user(20L));
        when(pedidoRepository.findById(ORDER_ID)).thenReturn(Optional.empty());
        assertFalse(pedidoSecurity.esRepartidorAsignadoPedidoDelivery(ORDER_ID));
        assertFalse(pedidoSecurity.esPropietarioRestaurantePedidoRetiro(ORDER_ID));
    }

    private void authenticatedAs(Usuario user) {
        when(authUtils.getUsuarioAutenticado()).thenReturn(Optional.of(user));
    }

    private Usuario user(Long id) {
        return Usuario.builder().id(id).build();
    }
}
