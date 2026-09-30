package utn.back.mordiscoapi.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.common.exception.PaymentUnavailableException;
import utn.back.mordiscoapi.enums.EstadoPago;
import utn.back.mordiscoapi.enums.MetodoPago;
import utn.back.mordiscoapi.enums.TipoEntrega;
import utn.back.mordiscoapi.model.dto.pedido.PedidoRequestDTO;
import utn.back.mordiscoapi.repository.*;
import utn.back.mordiscoapi.security.jwt.utils.AuthUtils;
import utn.back.mordiscoapi.service.MercadoPagoService;
import utn.back.mordiscoapi.service.interf.IGananciaRepartidorService;
import utn.back.mordiscoapi.event.order.PedidoCreatedEvent;
import utn.back.mordiscoapi.model.dto.configuracion.ConfiguracionSistemaGeneralResponseDTO;
import utn.back.mordiscoapi.model.dto.productoPedido.ProductoPedidoDTO;
import utn.back.mordiscoapi.model.dto.pago.MercadoPagoPreferenceResponse;
import utn.back.mordiscoapi.model.entity.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

class PedidoServiceImplTest {
    @Test
    void cashOrdersDoNotRequireMercadoPagoAvailability() {
        MercadoPagoService mercadoPagoService = mock(MercadoPagoService.class);
        PedidoServiceImpl service = new PedidoServiceImpl(
                mock(DireccionRepository.class), mock(PedidoRepository.class), mock(RestauranteRepository.class),
                mock(ProductoRepository.class), mock(UsuarioRepository.class), mock(PagoRepository.class),
                mock(PinService.class), mercadoPagoService, mock(ConfiguracionSistemaServiceImpl.class),
                mock(AuthUtils.class), mock(IGananciaRepartidorService.class), mock(ApplicationEventPublisher.class));
        PedidoRequestDTO request = new PedidoRequestDTO(null, null, null, null,
                MetodoPago.EFECTIVO, null, null);

        assertThrows(NotFoundException.class, () -> service.save(request));
        verify(mercadoPagoService, never()).validarDisponibilidad();
    }

    @Test
    void cashOrderPersistsOrderAndPaymentAndPublishesCreatedEvent() throws Exception {
        DireccionRepository direccionRepository = mock(DireccionRepository.class);
        PedidoRepository pedidoRepository = mock(PedidoRepository.class);
        RestauranteRepository restauranteRepository = mock(RestauranteRepository.class);
        ProductoRepository productoRepository = mock(ProductoRepository.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        PagoRepository pagoRepository = mock(PagoRepository.class);
        PinService pinService = mock(PinService.class);
        MercadoPagoService mercadoPagoService = mock(MercadoPagoService.class);
        ConfiguracionSistemaServiceImpl configuracionService = mock(ConfiguracionSistemaServiceImpl.class);
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);

        Usuario cliente = Usuario.builder().id(7L).build();
        Usuario restauranteUsuario = Usuario.builder().id(8L).build();
        Direccion direccion = Direccion.builder().id(5L).build();
        Restaurante restaurante = Restaurante.builder().id(4L).activo(true).usuario(restauranteUsuario)
                .direccion(direccion).razonSocial("Mordisco").build();
        Menu menu = Menu.builder().restaurante(restaurante).build();
        Producto producto = Producto.builder().id(10L).nombre("Empanadas").descripcion("Carne")
                .disponible(true).menu(menu).build();
        when(restauranteRepository.findById(4L)).thenReturn(Optional.of(restaurante));
        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
        when(configuracionService.getConfiguracionGeneralActual()).thenReturn(
                new ConfiguracionSistemaGeneralResponseDTO(null, null, null, null, BigDecimal.ZERO, null));
        when(pinService.generarPin()).thenReturn("1234");
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido saved = invocation.getArgument(0);
            saved.setId(99L);
            return saved;
        });
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PedidoServiceImpl service = new PedidoServiceImpl(
                direccionRepository, pedidoRepository, restauranteRepository, productoRepository,
                usuarioRepository, pagoRepository, pinService, mercadoPagoService, configuracionService,
                mock(AuthUtils.class), mock(IGananciaRepartidorService.class), eventPublisher);
        PedidoRequestDTO request = new PedidoRequestDTO(7L, 4L, null, TipoEntrega.RETIRO_POR_LOCAL,
                MetodoPago.EFECTIVO, List.of(new ProductoPedidoDTO(2, 10L, new BigDecimal("1200.00"))), null);

        MercadoPagoPreferenceResponse response = service.save(request);

        assertEquals(new MercadoPagoPreferenceResponse(null, null, null, 99L), response);
        verify(pedidoRepository).save(argThat(pedido -> pedido.getTotal().equals(new BigDecimal("2400.00"))
                && pedido.getSubtotalProductos().equals(new BigDecimal("2400.00"))));
        verify(pagoRepository).save(argThat(pago -> pago.getMetodoPago() == MetodoPago.EFECTIVO
                && pago.getEstado() == EstadoPago.PENDIENTE
                && pago.getMonto().equals(new BigDecimal("2400.00"))));
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertTrue(eventCaptor.getValue() instanceof PedidoCreatedEvent);
        assertEquals(99L, ((PedidoCreatedEvent) eventCaptor.getValue()).getPedido().getId());
        verify(mercadoPagoService, never()).validarDisponibilidad();
        verify(mercadoPagoService, never()).crearPreferenciaDePago(any());
    }

    @Test
    void unavailableMercadoPagoIsRejectedBeforeAnyOrderOrPaymentPersistence() {
        DireccionRepository direccionRepository = mock(DireccionRepository.class);
        PedidoRepository pedidoRepository = mock(PedidoRepository.class);
        RestauranteRepository restauranteRepository = mock(RestauranteRepository.class);
        ProductoRepository productoRepository = mock(ProductoRepository.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        PagoRepository pagoRepository = mock(PagoRepository.class);
        PinService pinService = mock(PinService.class);
        MercadoPagoService mercadoPagoService = mock(MercadoPagoService.class);
        ConfiguracionSistemaServiceImpl configuracionService = mock(ConfiguracionSistemaServiceImpl.class);
        AuthUtils authUtils = mock(AuthUtils.class);
        IGananciaRepartidorService gananciaService = mock(IGananciaRepartidorService.class);
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);

        doThrow(new PaymentUnavailableException()).when(mercadoPagoService).validarDisponibilidad();
        PedidoServiceImpl service = new PedidoServiceImpl(
                direccionRepository, pedidoRepository, restauranteRepository, productoRepository,
                usuarioRepository, pagoRepository, pinService, mercadoPagoService, configuracionService,
                authUtils, gananciaService, eventPublisher);
        PedidoRequestDTO request = new PedidoRequestDTO(null, null, null, null,
                MetodoPago.MERCADO_PAGO, null, null);

        assertThrows(PaymentUnavailableException.class, () -> service.save(request));
        verifyNoInteractions(pedidoRepository, pagoRepository);
    }
}
