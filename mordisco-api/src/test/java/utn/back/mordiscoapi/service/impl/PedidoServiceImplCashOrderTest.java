package utn.back.mordiscoapi.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.enums.MetodoPago;
import utn.back.mordiscoapi.enums.TipoEntrega;
import utn.back.mordiscoapi.event.order.PedidoCreatedEvent;
import utn.back.mordiscoapi.model.dto.configuracion.ConfiguracionSistemaGeneralResponseDTO;
import utn.back.mordiscoapi.model.dto.pedido.PedidoRequestDTO;
import utn.back.mordiscoapi.model.dto.productoPedido.ProductoPedidoDTO;
import utn.back.mordiscoapi.model.entity.Direccion;
import utn.back.mordiscoapi.model.entity.Menu;
import utn.back.mordiscoapi.model.entity.Pedido;
import utn.back.mordiscoapi.model.entity.Producto;
import utn.back.mordiscoapi.model.entity.Restaurante;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.repository.DireccionRepository;
import utn.back.mordiscoapi.repository.PagoRepository;
import utn.back.mordiscoapi.repository.PedidoRepository;
import utn.back.mordiscoapi.repository.ProductoRepository;
import utn.back.mordiscoapi.repository.RestauranteRepository;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.security.jwt.utils.AuthUtils;
import utn.back.mordiscoapi.service.MercadoPagoService;
import utn.back.mordiscoapi.service.interf.IGananciaRepartidorService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplCashOrderTest {

    @Mock
    private DireccionRepository direccionRepository;
    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private RestauranteRepository restauranteRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PagoRepository pagoRepository;
    @Mock
    private PinService pinService;
    @Mock
    private MercadoPagoService mercadoPagoService;
    @Mock
    private ConfiguracionSistemaServiceImpl configuracionService;
    @Mock
    private AuthUtils authUtils;
    @Mock
    private IGananciaRepartidorService gananciaRepartidorService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    @Test
    void cashOrderPublishesEventWithoutCreatingPaymentResponse() throws Exception {
        Long restauranteId = 1L;
        Long clienteId = 2L;
        Long productoId = 3L;

        Usuario restauranteUsuario = Usuario.builder().id(4L).email("restaurant@example.com").build();
        Direccion restauranteDireccion = Direccion.builder()
                .calle("Calle 1")
                .numero("100")
                .codigoPostal("1000")
                .ciudad("Ciudad")
                .build();
        Restaurante restaurante = Restaurante.builder()
                .id(restauranteId)
                .razonSocial("Restaurante")
                .activo(true)
                .usuario(restauranteUsuario)
                .direccion(restauranteDireccion)
                .build();
        Usuario cliente = Usuario.builder().id(clienteId).build();
        Menu menu = Menu.builder().restaurante(restaurante).build();
        Producto producto = Producto.builder()
                .id(productoId)
                .nombre("Producto")
                .descripcion("Descripcion")
                .disponible(true)
                .menu(menu)
                .build();
        PedidoRequestDTO request = new PedidoRequestDTO(
                clienteId,
                restauranteId,
                null,
                TipoEntrega.RETIRO_POR_LOCAL,
                MetodoPago.EFECTIVO,
                List.of(new ProductoPedidoDTO(1, productoId, BigDecimal.valueOf(1000))),
                null
        );

        when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.of(restaurante));
        when(usuarioRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
        when(pinService.generarPin()).thenReturn("1234");
        when(configuracionService.getConfiguracionGeneralActual())
                .thenReturn(new ConfiguracionSistemaGeneralResponseDTO(
                        BigDecimal.valueOf(80),
                        BigDecimal.TEN,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.valueOf(1000),
                        BigDecimal.valueOf(80)
                ));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);
            pedido.setId(42L);
            pedido.setEstado(EstadoPedido.PENDIENTE);
            return pedido;
        });

        assertNull(pedidoService.save(request));

        verify(eventPublisher).publishEvent(any(PedidoCreatedEvent.class));
        verify(mercadoPagoService, never()).crearPreferenciaDePago(any(Pedido.class));
    }
}
