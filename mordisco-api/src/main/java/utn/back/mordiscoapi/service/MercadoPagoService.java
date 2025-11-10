package utn.back.mordiscoapi.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.config.AppProperties;
import utn.back.mordiscoapi.model.dto.pago.MercadoPagoPreferenceResponse;
import utn.back.mordiscoapi.model.entity.Pedido;
import utn.back.mordiscoapi.model.entity.ProductoPedido;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MercadoPagoService {

    private final AppProperties appProperties;

    /**
     * Crea una preferencia de pago en Mercado Pago
     */
    public MercadoPagoPreferenceResponse crearPreferenciaDePago(Pedido pedido) {
        try {
            log.info("🔄 Creando preferencia de pago para pedido #{}", pedido.getId());

            var token = appProperties.getMercadoPago().getAccessToken();
            System.out.println("Token MP: " + token);
            MercadoPagoConfig.setAccessToken(token);

            // 1. Crear items de la preferencia (uno por cada producto)
            List<PreferenceItemRequest> items = crearItems(pedido);

            log.debug("📦 Items creados: {} productos, total: ${}",
                    items.size(), pedido.getTotal());

            // 2. Configurar URLs de retorno
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(appProperties.getFrontendUrl() + "/cliente/pedidos/pago-exitoso?pedido=" + pedido.getId())
                    .failure(appProperties.getFrontendUrl() + "/cliente/pedidos/pago-fallido?pedido=" + pedido.getId())
                    .pending(appProperties.getFrontendUrl() + "/cliente/pedidos/pago-pendiente?pedido=" + pedido.getId())
                    .build();

            log.debug("🔗 Back URLs configuradas: {}", appProperties.getFrontendUrl());

            // 3. Configurar payer (información del cliente)
            PreferencePayerRequest payer = PreferencePayerRequest.builder()
                    .name(pedido.getCliente().getNombre())
                    .surname(pedido.getCliente().getApellido())
                    .email(pedido.getCliente().getEmail())
                    .build();

            // 4. Configurar preferencia
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .payer(payer)
                    .backUrls(backUrls)
                    .externalReference(pedido.getId().toString())
                    .notificationUrl(appProperties.getMercadoPago().getNotificationUrl())
                    .statementDescriptor("MORDISCO") // Aparece en el resumen de la tarjeta
                    .expires(false) // La preferencia no expira
                    .binaryMode(false) // Permitir pagos pending
                    .build();

            log.debug("📋 Preference request creado con external_reference: {}", pedido.getId());

            // 5. Crear preferencia en Mercado Pago
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            log.info("✅ Preferencia creada exitosamente");
            log.info("   - Preference ID: {}", preference.getId());
            log.info("   - Init Point: {}", preference.getInitPoint());
            log.info("   - Sandbox Init Point: {}", preference.getSandboxInitPoint());
            log.info("   - Pedido ID: {}", pedido.getId());

            // 6. Retornar respuesta
            return new MercadoPagoPreferenceResponse(
                    preference.getId(),
                    preference.getInitPoint(),
                    preference.getSandboxInitPoint(),
                    pedido.getId()
            );

        } catch (MPApiException e) {
            log.error("❌ Error de API de Mercado Pago para pedido #{}", pedido.getId());
            log.error("   - Status Code: {}", e.getStatusCode());
            log.error("   - Message: {}", e.getMessage());

            // Intentar obtener más detalles del error
            if (e.getApiResponse() != null) {
                log.error("   - API Response: {}", e.getApiResponse().getContent());
            }

            throw new RuntimeException("Error al crear preferencia de pago en Mercado Pago: " +
                    e.getMessage() + " (Status: " + e.getStatusCode() + ")", e);

        } catch (MPException e) {
            log.error("❌ Error general de Mercado Pago para pedido #{}", pedido.getId(), e);
            throw new RuntimeException("Error al crear preferencia de pago: " + e.getMessage(), e);

        } catch (Exception e) {
            log.error("❌ Error inesperado al crear preferencia para pedido #{}", pedido.getId(), e);
            throw new RuntimeException("Error inesperado al procesar el pago: " + e.getMessage(), e);
        }
    }

    /**
     * Crea la lista de items para Mercado Pago
     */
    private List<PreferenceItemRequest> crearItems(Pedido pedido) {
        List<PreferenceItemRequest> items = new ArrayList<>();

        /* Opción 1: Un solo item con el total (más simple)
        PreferenceItemRequest item = PreferenceItemRequest.builder()
                .id(pedido.getId().toString())
                .title("Pedido #" + pedido.getId() + " - " + pedido.getRestaurante().getRazonSocial())
                .description("Pedido de comida - " + pedido.getItems().size() + " productos")
                .pictureUrl(pedido.getRestaurante().getLogo() != null ?
                        pedido.getRestaurante().getLogo().getUrl() : null)
                .categoryId("food")
                .quantity(1)
                .currencyId("ARS")
                .unitPrice(pedido.getTotal())
                .build();

        items.add(item);*/

        for (ProductoPedido productoPedido : pedido.getItems()) {
            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .id(productoPedido.getProducto().getId().toString())
                    .title(productoPedido.getProducto().getNombre())
                    .description(productoPedido.getProducto().getDescripcion())
                    .pictureUrl(productoPedido.getProducto().getImagen() != null ?
                            productoPedido.getProducto().getImagen().getUrl() : null)
                    .categoryId("food")
                    .quantity(productoPedido.getCantidad())
                    .currencyId("ARS")
                    .unitPrice(productoPedido.getPrecioUnitario())
                    .build();

            items.add(item);
        }

        return items;
    }

    /**
     * Obtiene información de un pago por su ID
     */
    public Payment obtenerPago(String paymentId) {
        try {
            log.info("🔍 Consultando pago {} en Mercado Pago", paymentId);

            MercadoPagoConfig.setAccessToken(appProperties.getMercadoPago().getAccessToken());

            PaymentClient client = new PaymentClient();
            Payment payment = client.get(Long.parseLong(paymentId));

            log.info("✅ Pago {} obtenido. Estado: {}", paymentId, payment.getStatus());
            return payment;

        } catch (MPException | MPApiException e) {
            log.error("❌ Error al obtener pago {} de Mercado Pago", paymentId, e);
            throw new RuntimeException("Error al consultar pago: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica el estado de un pago
     */
    public String verificarEstadoPago(String paymentId) {
        Payment payment = obtenerPago(paymentId);
        return payment.getStatus();
    }
}