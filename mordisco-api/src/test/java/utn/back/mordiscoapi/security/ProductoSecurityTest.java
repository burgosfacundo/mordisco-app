package utn.back.mordiscoapi.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.repository.MenuRepository;
import utn.back.mordiscoapi.repository.ProductoRepository;
import utn.back.mordiscoapi.security.jwt.utils.AuthUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoSecurityTest {
    private static final Long USER_ID = 10L;
    private static final Long MENU_ID = 20L;
    private static final Long PRODUCT_ID = 30L;

    @Mock private AuthUtils authUtils;
    @Mock private MenuRepository menuRepository;
    @Mock private ProductoRepository productoRepository;

    private ProductoSecurity productoSecurity;

    @BeforeEach
    void setUp() {
        productoSecurity = new ProductoSecurity(authUtils, menuRepository, productoRepository);
    }

    @Test
    void allowsOnlyTheOwnerToCreateInTheMenu() {
        authenticate();
        when(menuRepository.existsByIdAndRestaurante_Usuario_Id(MENU_ID, USER_ID)).thenReturn(true);

        assertTrue(productoSecurity.puedeCrearProductoEnMenu(MENU_ID));

        when(menuRepository.existsByIdAndRestaurante_Usuario_Id(MENU_ID, USER_ID)).thenReturn(false);
        assertFalse(productoSecurity.puedeCrearProductoEnMenu(MENU_ID));
    }

    @Test
    void allowsOnlyTheOwnerToAccessThePersistedProduct() {
        authenticate();
        when(productoRepository.existsByIdAndMenu_Restaurante_Usuario_Id(PRODUCT_ID, USER_ID)).thenReturn(true);

        assertTrue(productoSecurity.puedeAccederAProducto(PRODUCT_ID));

        when(productoRepository.existsByIdAndMenu_Restaurante_Usuario_Id(PRODUCT_ID, USER_ID)).thenReturn(false);
        assertFalse(productoSecurity.puedeAccederAProducto(PRODUCT_ID));
    }

    @Test
    void deniesMissingResourcesAndUnauthenticatedRequests() {
        authenticate();
        assertFalse(productoSecurity.puedeCrearProductoEnMenu(MENU_ID));
        assertFalse(productoSecurity.puedeAccederAProducto(PRODUCT_ID));

        clearInvocations(menuRepository, productoRepository);
        when(authUtils.getUsuarioAutenticado()).thenReturn(Optional.empty());
        assertFalse(productoSecurity.puedeCrearProductoEnMenu(MENU_ID));
        assertFalse(productoSecurity.puedeAccederAProducto(PRODUCT_ID));
        verify(menuRepository, never()).existsByIdAndRestaurante_Usuario_Id(MENU_ID, USER_ID);
        verify(productoRepository, never()).existsByIdAndMenu_Restaurante_Usuario_Id(PRODUCT_ID, USER_ID);
    }

    private void authenticate() {
        when(authUtils.getUsuarioAutenticado()).thenReturn(Optional.of(Usuario.builder().id(USER_ID).build()));
    }
}
