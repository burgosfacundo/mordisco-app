package utn.back.mordiscoapi.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import utn.back.mordiscoapi.model.dto.producto.ProductoRequestDTO;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.security.HorarioSecurity;
import utn.back.mordiscoapi.security.ProductoSecurity;
import utn.back.mordiscoapi.service.interf.IHorarioService;
import utn.back.mordiscoapi.service.interf.IProductoService;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ProductoHorarioControllerAuthorizationTest {
    private static final Long RESOURCE_ID = 10L;
    private static final Long USER_ID = 20L;

    private AnnotationConfigApplicationContext context;
    private ProductoController productoController;
    private HorarioController horarioController;
    private IProductoService productoService;
    private IHorarioService horarioService;
    private ProductoSecurity productoSecurity;
    private HorarioSecurity horarioSecurity;

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigApplicationContext(MethodSecurityConfiguration.class);
        productoController = context.getBean(ProductoController.class);
        horarioController = context.getBean(HorarioController.class);
        productoService = context.getBean(IProductoService.class);
        horarioService = context.getBean(IHorarioService.class);
        productoSecurity = context.getBean(ProductoSecurity.class);
        horarioSecurity = context.getBean(HorarioSecurity.class);
        assertTrue(AopUtils.isAopProxy(productoController));
        assertTrue(AopUtils.isAopProxy(horarioController));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        context.close();
    }

    @ParameterizedTest(name = "restaurant owner can invoke {0}")
    @MethodSource("protectedInvocations")
    void allowsRestaurantOwnersOnlyWhenTheSecurityCheckPasses(String ignored, Guard guard, Invocation invocation) {
        authenticate("RESTAURANTE");
        allow(guard);

        assertDoesNotThrow(() -> invocation.invoke(productoController, horarioController));
    }

    @ParameterizedTest(name = "admin can invoke {0}")
    @MethodSource("protectedInvocations")
    void allowsAdminsWithoutOwnershipChecks(String ignored, Guard guard, Invocation invocation) {
        authenticate("ADMIN");

        assertDoesNotThrow(() -> invocation.invoke(productoController, horarioController));
    }

    @Test
    void deniesNonOwnersWithoutInvokingServices() {
        authenticate("RESTAURANTE");

        for (Arguments arguments : protectedInvocations().toList()) {
            Invocation invocation = (Invocation) arguments.get()[2];
            assertThrows(AccessDeniedException.class, () -> invocation.invoke(productoController, horarioController));
        }
        verifyNoInteractions(productoService, horarioService);
    }

    private static Stream<Arguments> protectedInvocations() {
        return Stream.of(
                Arguments.of("product creation", Guard.PRODUCT_MENU, (Invocation) (producto, horario) -> producto.save(productRequest())),
                Arguments.of("active product orders", Guard.PRODUCT, (Invocation) (producto, horario) -> producto.getPedidosActivos(RESOURCE_ID, 0, 10)),
                Arguments.of("product deletion", Guard.PRODUCT, (Invocation) (producto, horario) -> producto.delete(RESOURCE_ID)),
                Arguments.of("product update", Guard.PRODUCT, (Invocation) (producto, horario) -> producto.update(RESOURCE_ID, null)),
                Arguments.of("schedule update", Guard.SCHEDULE, (Invocation) (producto, horario) -> horario.update(RESOURCE_ID, null)),
                Arguments.of("schedule deletion", Guard.SCHEDULE, (Invocation) (producto, horario) -> horario.delete(RESOURCE_ID))
        );
    }

    private void allow(Guard guard) {
        switch (guard) {
            case PRODUCT_MENU -> when(productoSecurity.puedeCrearProductoEnMenu(RESOURCE_ID)).thenReturn(true);
            case PRODUCT -> when(productoSecurity.puedeAccederAProducto(RESOURCE_ID)).thenReturn(true);
            case SCHEDULE -> when(horarioSecurity.puedeAccederAHorario(RESOURCE_ID)).thenReturn(true);
        }
    }

    private static ProductoRequestDTO productRequest() {
        return new ProductoRequestDTO(RESOURCE_ID, "Product", "Description", BigDecimal.ONE, true, null);
    }

    private void authenticate(String role) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                Usuario.builder().id(USER_ID).build(), "N/A", AuthorityUtils.createAuthorityList("ROLE_" + role)));
    }

    @Configuration
    @EnableMethodSecurity
    static class MethodSecurityConfiguration {
        @Bean IProductoService productoService() { return mock(IProductoService.class); }
        @Bean IHorarioService horarioService() { return mock(IHorarioService.class); }
        @Bean(name = "productoSecurity") ProductoSecurity productoSecurity() { return mock(ProductoSecurity.class); }
        @Bean(name = "horarioSecurity") HorarioSecurity horarioSecurity() { return mock(HorarioSecurity.class); }
        @Bean ProductoController productoController(IProductoService service) { return new ProductoController(service); }
        @Bean HorarioController horarioController(IHorarioService service) { return new HorarioController(service); }
    }

    @FunctionalInterface
    private interface Invocation {
        void invoke(ProductoController producto, HorarioController horario) throws Exception;
    }

    private enum Guard {
        PRODUCT_MENU,
        PRODUCT,
        SCHEDULE
    }
}
