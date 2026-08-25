package utn.back.mordiscoapi.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import utn.back.mordiscoapi.repository.MenuRepository;
import utn.back.mordiscoapi.repository.ProductoRepository;
import utn.back.mordiscoapi.security.jwt.utils.AuthUtils;

@Component("productoSecurity")
@RequiredArgsConstructor
public class ProductoSecurity {
    private final AuthUtils authUtils;
    private final MenuRepository menuRepository;
    private final ProductoRepository productoRepository;

    public boolean puedeCrearProductoEnMenu(Long idMenu) {
        return authUtils.getUsuarioAutenticado()
                .filter(usuario -> usuario.getId() != null)
                .map(usuario -> menuRepository.existsByIdAndRestaurante_Usuario_Id(idMenu, usuario.getId()))
                .orElse(false);
    }

    public boolean puedeAccederAProducto(Long idProducto) {
        return authUtils.getUsuarioAutenticado()
                .filter(usuario -> usuario.getId() != null)
                .map(usuario -> productoRepository.existsByIdAndMenu_Restaurante_Usuario_Id(idProducto, usuario.getId()))
                .orElse(false);
    }
}
