package utn.back.mordiscoapi.controller;

import com.mercadopago.resources.payment.Payment;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.config.AppProperties;
import utn.back.mordiscoapi.model.dto.pago.PagoResponse;
import utn.back.mordiscoapi.security.MercadoPagoWebhookSignatureValidator;
import utn.back.mordiscoapi.service.MercadoPagoService;
import utn.back.mordiscoapi.service.PagoService;

@Tag(name = "Pagos")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PagoController {
    private final PagoService pagoService;
    private final MercadoPagoService mercadoPagoService;
    private final MercadoPagoWebhookSignatureValidator webhookSignatureValidator;
    private final AppProperties appProperties;

    /**
     * Receives Mercado Pago notifications after authenticating their signed query data.
     * The request body is intentionally ignored: payment status and order identity are
     * read from Mercado Pago's authoritative PaymentClient response instead.
     */
    @PostMapping("/pagos/webhook")
    public ResponseEntity<Void> procesarWebhook(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "data.id", required = false) String dataId,
            @RequestHeader(name = "x-signature", required = false) String signature,
            @RequestHeader(name = "x-request-id", required = false) String requestId) {
        boolean validSignature = webhookSignatureValidator.isValid(
                signature,
                requestId,
                dataId,
                appProperties.getMercadoPago().getWebhookSecret()
        );
        if (!validSignature) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Valid, signed non-payment notifications are acknowledged without mutating payment state.
        if (!"payment".equals(type)) {
            return ResponseEntity.ok().build();
        }

        if (dataId == null || dataId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Payment payment = mercadoPagoService.obtenerPago(dataId);
        pagoService.procesarWebhook(dataId, payment);
        return ResponseEntity.ok().build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and @pedidoSecurity.esPropietarioPedido(#idPedido)) or (hasRole('RESTAURANTE') and @pedidoSecurity.esPropietarioRestaurantePedido(#idPedido)) or (hasRole('REPARTIDOR') and @pedidoSecurity.esRepartidorAsignadoPedidoDelivery(#idPedido))")
    @GetMapping("/pedidos/pagos/{idPedido}")
    public ResponseEntity<PagoResponse> getPagoByPedidoId(@PathVariable Long idPedido)
            throws NotFoundException {
        return ResponseEntity.ok(pagoService.obtenerPagoPorPedido(idPedido));
    }
}
