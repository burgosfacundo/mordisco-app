package utn.back.mordiscoapi.service;

import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import utn.back.mordiscoapi.common.exception.PaymentUnavailableException;
import utn.back.mordiscoapi.config.AppProperties;
import utn.back.mordiscoapi.model.dto.pago.MercadoPagoPreferenceResponse;
import utn.back.mordiscoapi.model.entity.Imagen;
import utn.back.mordiscoapi.model.entity.Pedido;
import utn.back.mordiscoapi.model.entity.Restaurante;
import utn.back.mordiscoapi.model.entity.Usuario;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MercadoPagoServiceTest {
    @Test
    void missingAndTemplateAccessTokensAreRejectedBeforeProviderCalls() {
        for (String token : new String[]{null, "", "   ", "${MERCADOPAGO_ACCESS_TOKEN}", "YOUR_ACCESS_TOKEN", "replace-with-token", "changeme"}) {
            AppProperties properties = new AppProperties();
            properties.getMercadoPago().setAccessToken(token);
            MercadoPagoService service = new MercadoPagoService(properties);

            assertThrows(PaymentUnavailableException.class,
                    () -> service.crearPreferenciaDePago(new Pedido()),
                    "Expected an unavailable result for an unset or template token");
            assertThrows(PaymentUnavailableException.class,
                    () -> service.obtenerPago("123"),
                    "Webhook lookups must be rejected before SDK access too");
        }
    }

    @Test
    void configuredTokenMapsProviderPreferenceResponseWithoutNetworkCalls() throws Exception {
        AppProperties properties = new AppProperties();
        properties.getMercadoPago().setAccessToken("APP_USR_configured-looking-token");
        properties.getMercadoPago().setNotificationUrl("https://demo.example/webhook");
        properties.setFrontendUrl("https://demo.example");
        MercadoPagoService service = new MercadoPagoService(properties);

        Pedido pedido = Pedido.builder()
                .id(42L)
                .total(new BigDecimal("1250.50"))
                .cliente(Usuario.builder().nombre("Ada").apellido("Lovelace").email("ada@example.test").build())
                .restaurante(Restaurante.builder().razonSocial("Mordisco").imagen(Imagen.builder().url("https://demo.example/image.png").build()).build())
                .items(List.of())
                .build();
        Preference providerPreference = mock(Preference.class);
        when(providerPreference.getId()).thenReturn("pref-123");
        when(providerPreference.getInitPoint()).thenReturn("https://pay.example/checkout");
        when(providerPreference.getSandboxInitPoint()).thenReturn("https://sandbox.example/checkout");

        try (MockedConstruction<PreferenceClient> clients = mockConstruction(PreferenceClient.class,
                (client, context) -> when(client.create(any(PreferenceRequest.class))).thenReturn(providerPreference))) {
            MercadoPagoPreferenceResponse response = service.crearPreferenciaDePago(pedido);

            assertEquals(new MercadoPagoPreferenceResponse("pref-123", "https://pay.example/checkout",
                    "https://sandbox.example/checkout", 42L), response);
            assertEquals(1, clients.constructed().size());
            verify(clients.constructed().get(0)).create(any(PreferenceRequest.class));
        }
    }

    @Test
    void configuredLookingTokenIsNotClaimedValidButIsNotRejectedAsMissing() {
        AppProperties properties = new AppProperties();
        properties.getMercadoPago().setAccessToken("APP_USR_configured-looking-token");
        MercadoPagoService service = new MercadoPagoService(properties);

        // Configuration presence is syntactic only; provider validity is determined by provider responses.
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(service::validarDisponibilidad);
    }
}
