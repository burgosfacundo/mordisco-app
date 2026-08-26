package utn.back.mordiscoapi.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.security.PedidoSecurity;
import utn.back.mordiscoapi.security.UsuarioSecurity;
import utn.back.mordiscoapi.service.PagoService;
import utn.back.mordiscoapi.service.interf.IRepartidorService;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class RepartidorPagoControllerAuthorizationTest {

    private static final Long RESOURCE_ID = 10L;

    private AnnotationConfigApplicationContext context;
    private RepartidorController repartidorController;
    private PagoController pagoController;
    private IRepartidorService repartidorService;
    private PagoService pagoService;
    private UsuarioSecurity usuarioSecurity;
    private PedidoSecurity pedidoSecurity;

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigApplicationContext(MethodSecurityConfiguration.class);
        repartidorController = context.getBean(RepartidorController.class);
        pagoController = context.getBean(PagoController.class);
        repartidorService = context.getBean(IRepartidorService.class);
        pagoService = context.getBean(PagoService.class);
        usuarioSecurity = context.getBean(UsuarioSecurity.class);
        pedidoSecurity = context.getBean(PedidoSecurity.class);
        assertTrue(AopUtils.isAopProxy(repartidorController));
        assertTrue(AopUtils.isAopProxy(pagoController));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        context.close();
    }

    @ParameterizedTest(name = "courier without ownership cannot access {0}")
    @MethodSource("courierOrderEndpoints")
    void deniesCouriersWithoutOwnershipWithoutInvokingTheService(String ignored,
                                                                  RepartidorInvocation invocation,
                                                                  RepartidorServiceVerification verification) {
        authenticate("REPARTIDOR");
        when(usuarioSecurity.puedeAccederAUsuario(RESOURCE_ID)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> invocation.invoke(repartidorController));

        verify(usuarioSecurity).puedeAccederAUsuario(RESOURCE_ID);
        verifyNoInteractions(repartidorService);
    }

    @ParameterizedTest(name = "courier with ownership can access {0}")
    @MethodSource("courierOrderEndpoints")
    void allowsCouriersWithOwnershipAndResolvesThePathVariable(String ignored,
                                                                 RepartidorInvocation invocation,
                                                                 RepartidorServiceVerification verification) throws Exception {
        authenticate("REPARTIDOR");
        when(usuarioSecurity.puedeAccederAUsuario(RESOURCE_ID)).thenReturn(true);

        assertDoesNotThrow(() -> invocation.invoke(repartidorController));

        verify(usuarioSecurity).puedeAccederAUsuario(RESOURCE_ID);
        verification.verify(repartidorService);
        verifyNoMoreInteractions(repartidorService);
    }

    @ParameterizedTest(name = "admin can access {0}")
    @MethodSource("courierOrderEndpoints")
    void allowsAdminsToAccessCourierOrders(String ignored, RepartidorInvocation invocation,
                                             RepartidorServiceVerification verification) throws Exception {
        authenticate("ADMIN");

        assertDoesNotThrow(() -> invocation.invoke(repartidorController));

        verifyNoInteractions(usuarioSecurity);
        verification.verify(repartidorService);
        verifyNoMoreInteractions(repartidorService);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("paymentAuthorizedScenarios")
    void allowsOnlyTheAuthorizedPaymentReaders(String ignored, String role, PaymentGuard guard) {
        authenticate(role);
        allow(guard);

        assertDoesNotThrow(() -> pagoController.getPagoByPedidoId(RESOURCE_ID));

        verifyPaymentGuard(guard);
        verify(pagoService).obtenerPagoPorPedido(RESOURCE_ID);
        verifyNoMoreInteractions(pagoService);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("paymentRejectedScenarios")
    void rejectsPaymentReadersWhoseRoleSpecificGuardDoesNotPass(String ignored, String role,
                                                                 PaymentGuard guard) {
        authenticate(role);
        allow(guard);

        assertThrows(AccessDeniedException.class, () -> pagoController.getPagoByPedidoId(RESOURCE_ID));

        verifyNoInteractions(pagoService);
        if (guard == PaymentGuard.CLIENT_AND_RESTAURANT_OWNER) {
            verify(pedidoSecurity, never()).esPropietarioPedido(RESOURCE_ID);
            verify(pedidoSecurity, never()).esPropietarioRestaurantePedido(RESOURCE_ID);
            verify(pedidoSecurity).esRepartidorAsignadoPedidoDelivery(RESOURCE_ID);
        } else if ("CLIENTE".equals(role)) {
            verify(pedidoSecurity).esPropietarioPedido(RESOURCE_ID);
            verify(pedidoSecurity, never()).esRepartidorAsignadoPedidoDelivery(RESOURCE_ID);
        } else {
            verify(pedidoSecurity).esPropietarioRestaurantePedido(RESOURCE_ID);
            verify(pedidoSecurity, never()).esRepartidorAsignadoPedidoDelivery(RESOURCE_ID);
        }
    }

    private static Stream<Arguments> courierOrderEndpoints() {
        return Stream.of(
                Arguments.of("courier order history", (RepartidorInvocation) controller ->
                                controller.getAllPedidosRepartidor(0, 10, RESOURCE_ID),
                        (RepartidorServiceVerification) service ->
                                verify(service).findPedidosByRepartidor(0, 10, RESOURCE_ID)),
                Arguments.of("courier pending orders", (RepartidorInvocation) controller ->
                                controller.getPedidosAEntregarRepartidor(0, 10, RESOURCE_ID),
                        (RepartidorServiceVerification) service ->
                                verify(service).findPedidosByRepartidor_EnCamino(0, 10, RESOURCE_ID))
        );
    }

    private static Stream<Arguments> paymentAuthorizedScenarios() {
        return Stream.of(
                Arguments.of("assigned courier reads payment", "REPARTIDOR", PaymentGuard.ASSIGNED_COURIER),
                Arguments.of("client owner reads payment", "CLIENTE", PaymentGuard.CLIENT_OWNER),
                Arguments.of("restaurant owner reads payment", "RESTAURANTE", PaymentGuard.RESTAURANT_OWNER),
                Arguments.of("admin reads payment", "ADMIN", PaymentGuard.NONE)
        );
    }

    private static Stream<Arguments> paymentRejectedScenarios() {
        return Stream.of(
                Arguments.of("unassigned courier cannot read payment even when client and restaurant helpers pass",
                        "REPARTIDOR", PaymentGuard.CLIENT_AND_RESTAURANT_OWNER),
                Arguments.of("client cannot read payment only because courier helper passes",
                        "CLIENTE", PaymentGuard.ASSIGNED_COURIER),
                Arguments.of("restaurant cannot read payment only because courier helper passes",
                        "RESTAURANTE", PaymentGuard.ASSIGNED_COURIER)
        );
    }

    private void allow(PaymentGuard guard) {
        switch (guard) {
            case CLIENT_OWNER -> when(pedidoSecurity.esPropietarioPedido(RESOURCE_ID)).thenReturn(true);
            case RESTAURANT_OWNER -> when(pedidoSecurity.esPropietarioRestaurantePedido(RESOURCE_ID)).thenReturn(true);
            case ASSIGNED_COURIER -> when(pedidoSecurity.esRepartidorAsignadoPedidoDelivery(RESOURCE_ID)).thenReturn(true);
            case CLIENT_AND_RESTAURANT_OWNER -> {
                when(pedidoSecurity.esPropietarioPedido(RESOURCE_ID)).thenReturn(true);
                when(pedidoSecurity.esPropietarioRestaurantePedido(RESOURCE_ID)).thenReturn(true);
            }
            case NONE -> {
            }
        }
    }

    private void verifyPaymentGuard(PaymentGuard guard) {
        switch (guard) {
            case CLIENT_OWNER -> verify(pedidoSecurity).esPropietarioPedido(RESOURCE_ID);
            case RESTAURANT_OWNER -> verify(pedidoSecurity).esPropietarioRestaurantePedido(RESOURCE_ID);
            case ASSIGNED_COURIER -> verify(pedidoSecurity).esRepartidorAsignadoPedidoDelivery(RESOURCE_ID);
            case NONE, CLIENT_AND_RESTAURANT_OWNER -> verifyNoInteractions(pedidoSecurity);
        }
    }

    private void authenticate(String role) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                Usuario.builder().id(RESOURCE_ID).build(), "N/A", AuthorityUtils.createAuthorityList("ROLE_" + role)));
    }

    @Configuration
    @EnableMethodSecurity
    static class MethodSecurityConfiguration {

        @Bean
        IRepartidorService repartidorService() {
            return Mockito.mock(IRepartidorService.class);
        }

        @Bean
        PagoService pagoService() {
            return Mockito.mock(PagoService.class);
        }

        @Bean(name = "usuarioSecurity")
        UsuarioSecurity usuarioSecurity() {
            return Mockito.mock(UsuarioSecurity.class);
        }

        @Bean(name = "pedidoSecurity")
        PedidoSecurity pedidoSecurity() {
            return Mockito.mock(PedidoSecurity.class);
        }

        @Bean
        RepartidorController repartidorController(IRepartidorService repartidorService) {
            return new RepartidorController(repartidorService);
        }

        @Bean
        PagoController pagoController(PagoService pagoService) {
            return new PagoController(pagoService);
        }
    }

    @FunctionalInterface
    private interface RepartidorInvocation {
        void invoke(RepartidorController controller) throws Exception;
    }

    @FunctionalInterface
    private interface RepartidorServiceVerification {
        void verify(IRepartidorService service) throws Exception;
    }

    private enum PaymentGuard {
        CLIENT_OWNER,
        RESTAURANT_OWNER,
        ASSIGNED_COURIER,
        CLIENT_AND_RESTAURANT_OWNER,
        NONE
    }
}
