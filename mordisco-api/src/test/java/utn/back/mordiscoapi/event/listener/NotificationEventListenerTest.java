package utn.back.mordiscoapi.event.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import utn.back.mordiscoapi.event.order.PedidoCompletadoEvent;
import utn.back.mordiscoapi.event.order.PedidoCanceladoEvent;
import utn.back.mordiscoapi.event.order.PedidoCreatedEvent;
import utn.back.mordiscoapi.event.order.PedidoEnCaminoEvent;
import utn.back.mordiscoapi.event.order.PedidoEnPreparacionEvent;
import utn.back.mordiscoapi.event.order.PedidoListoParaEntregarEvent;
import utn.back.mordiscoapi.event.order.PedidoListoParaRetirarEvent;
import utn.back.mordiscoapi.event.payment.PagoAprobadoEvent;
import utn.back.mordiscoapi.event.payment.PagoRechazadoEvent;
import utn.back.mordiscoapi.model.dto.notificacion.NotificacionDTO;
import utn.back.mordiscoapi.model.entity.Pedido;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NotificationEventListenerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Test
    void sendsPrivateNotificationsToCanonicalEmailUserDestinations() {
        Pedido pedido = order("cliente@example.com", "restaurante@example.com");
        NotificationEventListener listener = new NotificationEventListener(messagingTemplate);

        listener.handlePedidoCreated(new PedidoCreatedEvent(pedido));
        listener.handlePedidoEnPreparacion(new PedidoEnPreparacionEvent(pedido));
        listener.handlePedidoListoParaRetirar(new PedidoListoParaRetirarEvent(pedido));
        listener.handlePedidoEnCamino(new PedidoEnCaminoEvent(pedido));
        listener.handlePedidoCompletado(new PedidoCompletadoEvent(pedido));
        listener.handlePedidoCancelado(new PedidoCanceladoEvent(pedido, "motivo"));
        listener.handlePagoAprobado(new PagoAprobadoEvent(pedido));
        listener.handlePagoRechazado(new PagoRechazadoEvent(pedido, "motivo"));

        ArgumentCaptor<NotificacionDTO> payload = ArgumentCaptor.forClass(NotificacionDTO.class);
        verify(messagingTemplate, times(5)).convertAndSendToUser(
                eq("restaurante@example.com"), eq("/queue/notificaciones"), payload.capture());
        verify(messagingTemplate, times(7)).convertAndSendToUser(
                eq("cliente@example.com"), eq("/queue/notificaciones"), payload.capture());
        assertEquals(12, payload.getAllValues().size());
        verifyNoMoreInteractions(messagingTemplate);
    }

    @Test
    void retainsTheCourierBroadcastAndPayload() {
        Pedido pedido = order("cliente@example.com", "restaurante@example.com");
        NotificationEventListener listener = new NotificationEventListener(messagingTemplate);

        listener.handlePedidoListoParaEntregar(new PedidoListoParaEntregarEvent(pedido));

        ArgumentCaptor<NotificacionDTO> payload = ArgumentCaptor.forClass(NotificacionDTO.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/repartidores"), payload.capture());
        assertEquals(pedido.getId(), payload.getValue().pedidoId());
        verifyNoMoreInteractions(messagingTemplate);
    }

    private Pedido order(String clientEmail, String restaurantEmail) {
        Pedido pedido = mock(Pedido.class, RETURNS_DEEP_STUBS);
        when(pedido.getId()).thenReturn(42L);
        when(pedido.getCliente().getNombre()).thenReturn("Cliente");
        when(pedido.getCliente().getUsername()).thenReturn(clientEmail);
        when(pedido.getRestaurante().getUsuario().getUsername()).thenReturn(restaurantEmail);
        when(pedido.getRestaurante().getRazonSocial()).thenReturn("Restaurante");
        when(pedido.getEstado().toString()).thenReturn("CONFIRMADO");
        return pedido;
    }
}
