package utn.back.mordiscoapi.model.dto.pago;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MercadoPagoPreferenceResponseTest {

    @Test
    void acceptsNonblankCheckoutUrl() {
        MercadoPagoPreferenceResponse response = new MercadoPagoPreferenceResponse(
                "preference-id",
                "https://production.example/checkout",
                "https://sandbox.example/checkout",
                42L,
                "https://production.example/checkout"
        );

        assertEquals("https://production.example/checkout", response.checkoutUrl());
    }

    @Test
    void rejectsNullCheckoutUrl() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new MercadoPagoPreferenceResponse(
                        "preference-id",
                        "https://production.example/checkout",
                        "https://sandbox.example/checkout",
                        42L,
                        null
                )
        );
    }

    @Test
    void rejectsBlankCheckoutUrl() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new MercadoPagoPreferenceResponse(
                        "preference-id",
                        "https://production.example/checkout",
                        "https://sandbox.example/checkout",
                        42L,
                        " \t\n"
                )
        );
    }

    @Test
    void doesNotExposeCashResponseConstructor() {
        assertThrows(
                NoSuchMethodException.class,
                () -> MercadoPagoPreferenceResponse.class.getDeclaredConstructor(
                        String.class,
                        String.class,
                        String.class,
                        Long.class
                )
        );
    }
}
