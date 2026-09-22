package utn.back.mordiscoapi.controller;

import com.mercadopago.resources.payment.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utn.back.mordiscoapi.config.AppProperties;
import utn.back.mordiscoapi.security.MercadoPagoWebhookSignatureValidator;
import utn.back.mordiscoapi.service.MercadoPagoService;
import utn.back.mordiscoapi.service.PagoService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class MercadoPagoWebhookControllerTest {
    private static final String SECRET = "synthetic-webhook-secret";
    private static final String REQUEST_ID = "synthetic-request-id";
    private static final String DATA_ID = "123456";
    private static final String TIMESTAMP = "1735689600";

    private PagoService pagoService;
    private MercadoPagoService mercadoPagoService;
    private PagoController controller;
    private Payment payment;

    @BeforeEach
    void setUp() {
        pagoService = mock(PagoService.class);
        mercadoPagoService = mock(MercadoPagoService.class);
        AppProperties appProperties = new AppProperties();
        appProperties.getMercadoPago().setWebhookSecret(SECRET);
        controller = new PagoController(
                pagoService,
                mercadoPagoService,
                new MercadoPagoWebhookSignatureValidator(),
                appProperties
        );
        payment = mock(Payment.class);
    }

    @Test
    void validatesOriginBeforeLookingUpAndProcessingAuthoritativePayment() {
        when(mercadoPagoService.obtenerPago(DATA_ID)).thenReturn(payment);
        String signature = signature(DATA_ID, REQUEST_ID, TIMESTAMP);

        var response = controller.procesarWebhook("payment", DATA_ID, signature, REQUEST_ID);

        assertEquals(200, response.getStatusCode().value());
        verify(mercadoPagoService).obtenerPago(DATA_ID);
        verify(pagoService).procesarWebhook(DATA_ID, payment);
    }

    @Test
    void rejectsInvalidOriginBeforeAnyPaymentSideEffect() {
        var response = controller.procesarWebhook(
                "payment", DATA_ID, "ts=" + TIMESTAMP + ",v1=00", REQUEST_ID);

        assertEquals(401, response.getStatusCode().value());
        verifyNoInteractions(mercadoPagoService, pagoService);
    }

    @Test
    void acknowledgesSignedNonPaymentNotificationWithoutMutatingPaymentState() {
        String signature = signature(null, REQUEST_ID, TIMESTAMP);

        var response = controller.procesarWebhook("merchant_order", null, signature, REQUEST_ID);

        assertEquals(200, response.getStatusCode().value());
        verifyNoInteractions(mercadoPagoService, pagoService);
    }

    @Test
    void requiresPaymentDataIdOnlyForPaymentNotifications() {
        String signature = signature(null, REQUEST_ID, TIMESTAMP);

        var response = controller.procesarWebhook("payment", null, signature, REQUEST_ID);

        assertEquals(400, response.getStatusCode().value());
        verifyNoInteractions(mercadoPagoService, pagoService);
    }

    private String signature(String dataId, String requestId, String timestamp) {
        try {
            StringBuilder manifest = new StringBuilder();
            if (dataId != null) {
                String normalizedDataId = dataId.matches("[A-Za-z0-9]+")
                        ? dataId.toLowerCase(Locale.ROOT)
                        : dataId;
                manifest.append("id:").append(normalizedDataId).append(';');
            }
            if (requestId != null) {
                manifest.append("request-id:").append(requestId).append(';');
            }
            manifest.append("ts:").append(timestamp).append(';');

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return "ts=" + timestamp + ",v1="
                    + HexFormat.of().formatHex(mac.doFinal(manifest.toString().getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }
}
