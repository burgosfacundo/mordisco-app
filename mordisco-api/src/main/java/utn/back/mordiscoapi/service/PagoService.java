package utn.back.mordiscoapi.service;

import com.mercadopago.resources.payment.Payment;
import org.springframework.context.ApplicationEventPublisher;
import utn.back.mordiscoapi.enums.EstadoPago;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.event.order.PedidoCreatedEvent;
import utn.back.mordiscoapi.event.payment.PagoAprobadoEvent;
import utn.back.mordiscoapi.event.payment.PagoRechazadoEvent;
import utn.back.mordiscoapi.model.dto.pago.PagoResponse;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utn.back.mordiscoapi.model.entity.Pago;
import utn.back.mordiscoapi.model.entity.Pedido;
import utn.back.mordiscoapi.repository.PagoRepository;
import utn.back.mordiscoapi.repository.PedidoRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Applies an authoritative Mercado Pago response inside one database transaction.
     * The controller performs the remote PaymentClient lookup before entering this
     * method, so the transaction contains only the local idempotent state transition.
     */
    @Transactional
    public void procesarWebhook(String paymentId, Payment payment) {
        if (paymentId == null || paymentId.isBlank() || payment == null) {
            throw new IllegalArgumentException("Mercado Pago payment data is required");
        }

        if (payment.getId() != null && !paymentId.equals(payment.getId().toString())) {
            throw new IllegalStateException("Mercado Pago payment identity mismatch");
        }

        String externalReference = payment.getExternalReference();
        if (externalReference == null || externalReference.isBlank()) {
            throw new IllegalStateException("Mercado Pago payment has no external reference");
        }

        Long pedidoId;
        try {
            pedidoId = Long.valueOf(externalReference);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("Mercado Pago payment has an invalid external reference", exception);
        }

        Pago pago = pagoRepository.findByPedidoIdWithRelations(pedidoId)
                .orElseThrow(() -> new IllegalStateException("Pago no encontrado para pedido #" + pedidoId));

        if (pago.getMercadoPagoPaymentId() != null
                && !pago.getMercadoPagoPaymentId().isBlank()
                && !paymentId.equals(pago.getMercadoPagoPaymentId())) {
            throw new IllegalStateException("Mercado Pago payment identity mismatch");
        }

        EstadoPago nuevoEstadoPago = mapearEstadoMercadoPago(payment.getStatus());
        boolean stateChanged = pago.getEstado() != nuevoEstadoPago;
        boolean paymentDetailsChanged = !Objects.equals(pago.getMercadoPagoPaymentId(), paymentId)
                || !Objects.equals(pago.getMercadoPagoStatus(), payment.getStatus())
                || !Objects.equals(pago.getMercadoPagoStatusDetail(), payment.getStatusDetail())
                || !Objects.equals(pago.getMercadoPagoPaymentType(), payment.getPaymentTypeId());

        if (!stateChanged && !paymentDetailsChanged) {
            return;
        }

        pago.setMercadoPagoPaymentId(paymentId);
        pago.setMercadoPagoStatus(payment.getStatus());
        pago.setMercadoPagoStatusDetail(payment.getStatusDetail());
        pago.setMercadoPagoPaymentType(payment.getPaymentTypeId());
        pago.setEstado(nuevoEstadoPago);
        pagoRepository.save(pago);

        if (!stateChanged) {
            return;
        }

        Pedido pedido = pago.getPedido();
        if (pedido == null) {
            throw new IllegalStateException("Pago no tiene pedido asociado");
        }

        if (nuevoEstadoPago == EstadoPago.APROBADO) {
            updatePedidoState(pedido, EstadoPedido.EN_PREPARACION);
            eventPublisher.publishEvent(new PedidoCreatedEvent(pedido));
            eventPublisher.publishEvent(new PagoAprobadoEvent(pedido));
        } else if (nuevoEstadoPago == EstadoPago.RECHAZADO) {
            updatePedidoState(pedido, EstadoPedido.CANCELADO);
            eventPublisher.publishEvent(new PagoRechazadoEvent(pedido, "Pago rechazado por Mercado Pago"));
        }
    }

    private void updatePedidoState(Pedido pedido, EstadoPedido targetState) {
        EstadoPedido currentState = pedido.getEstado();
        if (currentState == targetState
                || currentState == EstadoPedido.COMPLETADO
                || (currentState == EstadoPedido.CANCELADO && targetState == EstadoPedido.EN_PREPARACION)) {
            return;
        }
        pedido.setEstado(targetState);
        pedidoRepository.save(pedido);
    }

    /**
     * Mapea el estado de Mercado Pago a nuestro enum
     */
    private EstadoPago mapearEstadoMercadoPago(String mercadoPagoStatus) {
        if (mercadoPagoStatus == null || mercadoPagoStatus.isBlank()) {
            throw new IllegalStateException("Mercado Pago payment has no status");
        }
        return switch (mercadoPagoStatus) {
            case "approved" -> EstadoPago.APROBADO;
            case "rejected", "cancelled" -> EstadoPago.RECHAZADO;
            case "refunded" -> EstadoPago.REEMBOLSADO;
            case "pending", "in_process", "in_mediation", "authorized" -> EstadoPago.PENDIENTE;
            default -> {
                log.warn("⚠️ Estado desconocido de Mercado Pago: {}", mercadoPagoStatus);
                yield EstadoPago.PENDIENTE;
            }
        };
    }

    /**
     * Obtiene el pago de un pedido
     */
    public PagoResponse obtenerPagoPorPedido(Long pedidoId) {
        Pago pago = pagoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado para pedido #" + pedidoId));

        return new PagoResponse(pago.getId(),pago.getPedido().getId(),pago.getMetodoPago(),pago.getMonto(),
                pago.getEstado(),pago.getMercadoPagoPaymentId(),pago.getMercadoPagoStatus(),pago.getFechaCreacion());
    }
}