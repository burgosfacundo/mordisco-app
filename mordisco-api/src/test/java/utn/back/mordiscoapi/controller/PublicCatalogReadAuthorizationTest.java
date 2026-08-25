package utn.back.mordiscoapi.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.service.interf.IHorarioService;
import utn.back.mordiscoapi.service.interf.IMenuService;
import utn.back.mordiscoapi.service.interf.IPromocionService;
import utn.back.mordiscoapi.service.interf.IProductoService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PublicCatalogReadAuthorizationTest {
    private AnnotationConfigApplicationContext context;
    private ProductoController productoController;
    private HorarioController horarioController;
    private MenuController menuController;
    private PromocionController promocionController;
    private IProductoService productoService;
    private IHorarioService horarioService;
    private IMenuService menuService;
    private IPromocionService promocionService;

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigApplicationContext(PublicReadConfiguration.class);
        productoController = context.getBean(ProductoController.class);
        horarioController = context.getBean(HorarioController.class);
        menuController = context.getBean(MenuController.class);
        promocionController = context.getBean(PromocionController.class);
        productoService = context.getBean(IProductoService.class);
        horarioService = context.getBean(IHorarioService.class);
        menuService = context.getBean(IMenuService.class);
        promocionService = context.getBean(IPromocionService.class);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        context.close();
    }

    @Test
    void keepsCatalogReadsPublicWithoutAuthentication() throws NotFoundException {
        assertDoesNotThrow(() -> productoController.findAll(0, 10, 1L));
        assertDoesNotThrow(() -> horarioController.getAllByIdRestaurante(1L));
        assertDoesNotThrow(() -> menuController.findByRestauranteId(1L));
        assertDoesNotThrow(() -> promocionController.findById(1L));

        verify(productoService).findAllByIdMenu(0, 10, 1L);
        verify(horarioService).findAllByIdRestaurante(1L);
        verify(menuService).findByRestauranteId(1L);
        verify(promocionService).findById(1L);
    }

    @Configuration
    @EnableMethodSecurity
    static class PublicReadConfiguration {
        @Bean IProductoService productoService() { return mock(IProductoService.class); }
        @Bean IHorarioService horarioService() { return mock(IHorarioService.class); }
        @Bean IMenuService menuService() { return mock(IMenuService.class); }
        @Bean IPromocionService promocionService() { return mock(IPromocionService.class); }
        @Bean ProductoController productoController(IProductoService service) { return new ProductoController(service); }
        @Bean HorarioController horarioController(IHorarioService service) { return new HorarioController(service); }
        @Bean MenuController menuController(IMenuService service) { return new MenuController(service); }
        @Bean PromocionController promocionController(IPromocionService service) { return new PromocionController(service); }
    }
}
