package utn.back.mordiscoapi.service;

import com.mercadopago.resources.payment.Payment;
import org.springframework.context.ApplicationEventPublisher;
import utn.back.mordiscoapi.enums.EstadoPago;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.event.order.PedidoCreatedEvent;
import utn.back.mordiscoapi.event.payment.PagoAprobadoEvent;
import utn.back.mordiscoapi.event.payment.PagoRechazadoEvent;
import utn.back.mordiscoapi.model.dto.pago.PagoResponse;
import utn.back.mordiscoapi.model.dto.pago.WebhookMercadoPagoRequest;
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
    private final MercadoPagoService mercadoPagoService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Procesa el webhook de Mercado Pago
     */
    @Transactional
    public void procesarWebhook(WebhookMercadoPagoRequest webhook) {
        log.info("🔔 Procesando webhook de Mercado Pago: {}", webhook);

        // Validar que sea un webhook de pago
        if (!"payment".equals(webhook.getType())) {
            log.warn("⚠️ Webhook no es de tipo 'payment'. Tipo: {}", webhook.getType());
            return;
        }

        // Obtener el ID del pago
        String paymentId = webhook.getData().getId();

        if (paymentId == null || paymentId.isEmpty()) {
            log.error("❌ Webhook sin payment ID");
            return;
        }

        try {
            // Consultar el pago en Mercado Pago
            Payment payment = mercadoPagoService.obtenerPago(paymentId);

            // Obtener la referencia externa (ID del pedido)
            String externalReference = payment.getExternalReference();

            if (externalReference == null) {
                log.error("❌ Payment {} sin external_reference", paymentId);
                return;
            }

            Long pedidoId = Long.parseLong(externalReference);

            // Buscar el pago en la BD
            Pago pago = pagoRepository.findByPedidoId(pedidoId)
                    .orElseThrow(() -> new RuntimeException("Pago no encontrado para pedido #" + pedidoId));

            // Actualizar información del pago
            pago.setMercadoPagoPaymentId(paymentId);
            pago.setMercadoPagoStatus(payment.getStatus());
            pago.setMercadoPagoStatusDetail(payment.getStatusDetail());
            pago.setMercadoPagoPaymentType(payment.getPaymentTypeId());

            // Actualizar estado según el status de Mercado Pago
            EstadoPago nuevoEstadoPago = mapearEstadoMercadoPago(payment.getStatus());
            pago.setEstado(nuevoEstadoPago);

            pagoRepository.save(pago);
            log.info("✅ Pago actualizado: {} - Estado: {}", paymentId, nuevoEstadoPago);

            // Actualizar estado del pedido
            Pedido pedido = pago.getPedido();

            if (nuevoEstadoPago == EstadoPago.APROBADO) {
                // Pago aprobado -> Pedido EN_PROCESO
                pedido.setEstado(EstadoPedido.EN_PREPARACION);
                pedidoRepository.save(pedido);

                // Publicar eventos
                eventPublisher.publishEvent(new PedidoCreatedEvent(pedido)); // Notificar al restaurante
                eventPublisher.publishEvent(new PagoAprobadoEvent(pedido)); // Notificar al cliente

                log.info("✅ Pedido #{} confirmado y eventos publicados", pedidoId);

            } else if (nuevoEstadoPago == EstadoPago.RECHAZADO) {
                // Pago rechazado -> Pedido CANCELADO
                pedido.setEstado(EstadoPedido.CANCELADO);
                pedidoRepository.save(pedido);

                // Publicar evento de pago rechazado
                eventPublisher.publishEvent(new PagoRechazadoEvent(pedido, "Pago rechazado por Mercado Pago"));

                log.info("❌ Pedido #{} cancelado por pago rechazado", pedidoId);
            }

        } catch (Exception e) {
            log.error("❌ Error procesando webhook para payment {}", paymentId, e);
            throw new RuntimeException("Error procesando webhook", e);
        }
    }

    /**
     * Mapea el estado de Mercado Pago a nuestro enum
     */
    private EstadoPago mapearEstadoMercadoPago(String mercadoPagoStatus) {
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