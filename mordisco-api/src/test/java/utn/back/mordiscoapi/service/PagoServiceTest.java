package utn.back.mordiscoapi.service;

import com.mercadopago.resources.payment.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import utn.back.mordiscoapi.enums.EstadoPago;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.model.entity.Pago;
import utn.back.mordiscoapi.model.entity.Pedido;
import utn.back.mordiscoapi.model.entity.Restaurante;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.repository.PagoRepository;
import utn.back.mordiscoapi.repository.PedidoRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {
    private static final String PAYMENT_ID = "123456";

    @Mock private PagoRepository pagoRepository;
    @Mock private PedidoRepository pedidoRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private Payment payment;

    private Pago pago;
    private Pedido pedido;
    private PagoService service;

    @BeforeEach
    void setUp() {
        Usuario cliente = Usuario.builder().id(7L).email("client@example.test").nombre("Client").build();
        Usuario restauranteUsuario = Usuario.builder().id(8L).email("restaurant@example.test").nombre("Restaurant").build();
        Restaurante restaurante = Restaurante.builder()
                .id(9L)
                .razonSocial("Synthetic Restaurant")
                .usuario(restauranteUsuario)
                .build();
        pedido = Pedido.builder()
                .id(42L)
                .estado(EstadoPedido.PENDIENTE)
                .cliente(cliente)
                .restaurante(restaurante)
                .build();
        pago = Pago.builder()
                .id(99L)
                .pedido(pedido)
                .estado(EstadoPago.PENDIENTE)
                .build();

        service = new PagoService(pagoRepository, pedidoRepository, eventPublisher);
        when(pagoRepository.findByPedidoIdWithRelations(42L)).thenReturn(Optional.of(pago));
        when(payment.getId()).thenReturn(Long.valueOf(PAYMENT_ID));
        when(payment.getExternalReference()).thenReturn("42");
        when(payment.getStatus()).thenReturn("approved");
        when(payment.getStatusDetail()).thenReturn("accredited");
        when(payment.getPaymentTypeId()).thenReturn("credit_card");
    }

    @Test
    void repeatedApprovedNotificationsPersistAndPublishOnlyOnce() {
        service.procesarWebhook(PAYMENT_ID, payment);
        service.procesarWebhook(PAYMENT_ID, payment);

        verify(pagoRepository, times(2)).findByPedidoIdWithRelations(42L);
        verify(pagoRepository, times(1)).save(pago);
        verify(pedidoRepository, times(1)).save(pedido);
        verify(eventPublisher, times(2)).publishEvent(any(Object.class));
    }
}
