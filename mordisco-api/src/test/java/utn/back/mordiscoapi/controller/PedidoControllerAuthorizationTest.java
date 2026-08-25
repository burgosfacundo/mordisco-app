package utn.back.mordiscoapi.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.security.PedidoSecurity;
import utn.back.mordiscoapi.security.RestauranteSecurity;
import utn.back.mordiscoapi.security.UsuarioSecurity;
import utn.back.mordiscoapi.service.interf.IPedidoService;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PedidoControllerAuthorizationTest {

    private static final Long ORDER_ID = 10L;
    private static final Long OWNER_ID = 20L;

    private AnnotationConfigApplicationContext context;
    private PedidoController controller;
    private IPedidoService pedidoService;
    private PedidoSecurity pedidoSecurity;
    private RestauranteSecurity restauranteSecurity;
    private UsuarioSecurity usuarioSecurity;

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigApplicationContext(MethodSecurityConfiguration.class);
        controller = context.getBean(PedidoController.class);
        pedidoService = context.getBean(IPedidoService.class);
        pedidoSecurity = context.getBean(PedidoSecurity.class);
        restauranteSecurity = context.getBean(RestauranteSecurity.class);
        usuarioSecurity = context.getBean(UsuarioSecurity.class);
        assertTrue(AopUtils.isAopProxy(controller));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        context.close();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("authorizedScenarios")
    void allowsOnlyTheSpecifiedOwnersAndExistingAdmins(String ignored, String role,
                                                        boolean orderOwner, boolean restaurantOwner,
                                                        boolean assignedCourier, boolean userOwner,
                                                        ThrowingConsumer<PedidoController> invocation) {
        authenticate(role);
        when(pedidoSecurity.esPropietarioPedido(ORDER_ID)).thenReturn(orderOwner);
        when(pedidoSecurity.esPropietarioRestaurantePedido(ORDER_ID)).thenReturn(restaurantOwner);
        when(pedidoSecurity.esRepartidorAsignadoPedidoDelivery(ORDER_ID)).thenReturn(assignedCourier);
        when(pedidoSecurity.esPropietarioRestaurantePedidoRetiro(ORDER_ID)).thenReturn(restaurantOwner);
        when(restauranteSecurity.puedeAccederAPropioRestaurante(OWNER_ID)).thenReturn(restaurantOwner);
        when(usuarioSecurity.puedeAccederAUsuario(OWNER_ID)).thenReturn(userOwner);

        assertDoesNotThrow(() -> invocation.accept(controller));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("rejectedScenarios")
    void rejectsUnauthorizedAccessWithoutInvokingTheService(String ignored, String role,
                                                             ThrowingConsumer<PedidoController> invocation) {
        authenticate(role);

        assertThrows(AccessDeniedException.class, () -> invocation.accept(controller));
        verifyNoInteractions(pedidoService);
    }

    private static Stream<Arguments> authorizedScenarios() {
        return Stream.of(
                allow("admin reads an order", "ADMIN", false, false, false, false, c -> c.findById(ORDER_ID)),
                allow("client reads own order", "CLIENTE", true, false, false, false, c -> c.findById(ORDER_ID)),
                allow("restaurant reads its order", "RESTAURANTE", false, true, false, false, c -> c.findById(ORDER_ID)),
                allow("assigned courier reads delivery order", "REPARTIDOR", false, false, true, false, c -> c.findById(ORDER_ID)),
                allow("restaurant changes its order state", "RESTAURANTE", false, true, false, false, c -> c.changeState(ORDER_ID, EstadoPedido.EN_PREPARACION)),
                allow("client cancels own order", "CLIENTE", true, false, false, false, c -> c.cancelarPedido(ORDER_ID)),
                allow("restaurant cancels its order", "RESTAURANTE", false, true, false, false, c -> c.cancelarPedido(ORDER_ID)),
                allow("assigned courier delivers delivery order", "REPARTIDOR", false, false, true, false, c -> c.marcarComoEntregado(ORDER_ID, SecurityContextHolder.getContext().getAuthentication())),
                allow("restaurant completes pickup order", "RESTAURANTE", false, true, false, false, c -> c.marcarComoEntregado(ORDER_ID, SecurityContextHolder.getContext().getAuthentication())),
                allow("admin searches restaurant orders", "ADMIN", false, false, false, false, PedidoControllerAuthorizationTest::searchRestaurantOrders),
                allow("restaurant searches own orders", "RESTAURANTE", false, true, false, false, PedidoControllerAuthorizationTest::searchRestaurantOrders),
                allow("admin searches client orders", "ADMIN", false, false, false, false, PedidoControllerAuthorizationTest::searchClientOrders),
                allow("client searches own orders", "CLIENTE", false, false, false, true, PedidoControllerAuthorizationTest::searchClientOrders),
                allow("admin searches courier orders", "ADMIN", false, false, false, false, PedidoControllerAuthorizationTest::searchCourierOrders),
                allow("courier searches own orders", "REPARTIDOR", false, false, false, true, PedidoControllerAuthorizationTest::searchCourierOrders)
        );
    }

    private static Stream<Arguments> rejectedScenarios() {
        return Stream.of(
                deny("unassigned courier cannot read an order", "REPARTIDOR", c -> c.findById(ORDER_ID)),
                deny("non-owner restaurant cannot change order state", "RESTAURANTE", c -> c.changeState(ORDER_ID, EstadoPedido.EN_PREPARACION)),
                deny("admin cannot change order state", "ADMIN", c -> c.changeState(ORDER_ID, EstadoPedido.EN_PREPARACION)),
                deny("non-owner client cannot cancel an order", "CLIENTE", c -> c.cancelarPedido(ORDER_ID)),
                deny("non-owner restaurant cannot cancel an order", "RESTAURANTE", c -> c.cancelarPedido(ORDER_ID)),
                deny("admin cannot cancel an order", "ADMIN", c -> c.cancelarPedido(ORDER_ID)),
                deny("unassigned courier cannot deliver an order", "REPARTIDOR", c -> c.marcarComoEntregado(ORDER_ID, SecurityContextHolder.getContext().getAuthentication())),
                deny("non-owner restaurant cannot complete pickup order", "RESTAURANTE", c -> c.marcarComoEntregado(ORDER_ID, SecurityContextHolder.getContext().getAuthentication())),
                deny("admin cannot complete an order", "ADMIN", c -> c.marcarComoEntregado(ORDER_ID, SecurityContextHolder.getContext().getAuthentication())),
                deny("non-owner restaurant cannot search restaurant orders", "RESTAURANTE", PedidoControllerAuthorizationTest::searchRestaurantOrders),
                deny("non-owner client cannot search client orders", "CLIENTE", PedidoControllerAuthorizationTest::searchClientOrders),
                deny("non-owner courier cannot search courier orders", "REPARTIDOR", PedidoControllerAuthorizationTest::searchCourierOrders)
        );
    }

    private static Arguments allow(String name, String role, boolean orderOwner, boolean restaurantOwner,
                                   boolean assignedCourier, boolean userOwner, ThrowingConsumer<PedidoController> invocation) {
        return Arguments.of(name, role, orderOwner, restaurantOwner, assignedCourier, userOwner, invocation);
    }

    private static Arguments deny(String name, String role, ThrowingConsumer<PedidoController> invocation) {
        return Arguments.of(name, role, invocation);
    }

    private void authenticate(String role) {
        Usuario user = Usuario.builder().id(OWNER_ID).build();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                user,
                "N/A",
                AuthorityUtils.createAuthorityList("ROLE_" + role)
        ));
    }

    private static void searchRestaurantOrders(PedidoController controller) throws Exception {
        controller.searchPedidosRestaurante(OWNER_ID, null, null, null, null, null, 0, 10);
    }

    private static void searchClientOrders(PedidoController controller) throws Exception {
        controller.searchPedidosCliente(OWNER_ID, null, null, null, null, null, 0, 10);
    }

    private static void searchCourierOrders(PedidoController controller) throws Exception {
        controller.searchPedidosRepartidor(OWNER_ID, null, null, null, null, 0, 10);
    }

    @Configuration
    @EnableMethodSecurity
    static class MethodSecurityConfiguration {

        @Bean
        IPedidoService pedidoService() {
            return Mockito.mock(IPedidoService.class);
        }

        @Bean(name = "pedidoSecurity")
        PedidoSecurity pedidoSecurity() {
            return Mockito.mock(PedidoSecurity.class);
        }

        @Bean(name = "restauranteSecurity")
        RestauranteSecurity restauranteSecurity() {
            return Mockito.mock(RestauranteSecurity.class);
        }

        @Bean(name = "usuarioSecurity")
        UsuarioSecurity usuarioSecurity() {
            return Mockito.mock(UsuarioSecurity.class);
        }

        @Bean
        PedidoController pedidoController(IPedidoService pedidoService) {
            return new PedidoController(pedidoService);
        }
    }

    @FunctionalInterface
    private interface ThrowingConsumer<T> {
        void accept(T value) throws Exception;
    }
}
