package utn.back.mordiscoapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import utn.back.mordiscoapi.model.dto.pago.MercadoPagoPreferenceResponse;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MercadoPagoServiceTest {

    @Test
    void sandboxSelectsSandboxInitPoint() {
        assertEquals(
                "https://sandbox.example/checkout",
                MercadoPagoService.selectCheckoutUrl(
                        true,
                        "https://production.example/checkout",
                        "https://sandbox.example/checkout"
                )
        );
    }

    @Test
    void productionSelectsInitPoint() {
        assertEquals(
                "https://production.example/checkout",
                MercadoPagoService.selectCheckoutUrl(
                        false,
                        "https://production.example/checkout",
                        "https://sandbox.example/checkout"
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("missingSelectedCheckoutUrls")
    void missingSelectedCheckoutUrlFailsBeforeResponseCreation(
            String caseName,
            boolean sandbox,
            String initPoint,
            String sandboxInitPoint
    ) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new MercadoPagoPreferenceResponse(
                        "preference-id",
                        initPoint,
                        sandboxInitPoint,
                        42L,
                        MercadoPagoService.selectCheckoutUrl(sandbox, initPoint, sandboxInitPoint)
                )
        );
    }

    private static Stream<Arguments> missingSelectedCheckoutUrls() {
        return Stream.of(
                Arguments.of(
                        "sandbox rejects null sandboxInitPoint",
                        true,
                        "https://production.example/checkout",
                        null
                ),
                Arguments.of(
                        "sandbox rejects blank sandboxInitPoint",
                        true,
                        "https://production.example/checkout",
                        " \t"
                ),
                Arguments.of(
                        "production rejects null initPoint",
                        false,
                        null,
                        "https://sandbox.example/checkout"
                ),
                Arguments.of(
                        "production rejects blank initPoint",
                        false,
                        "\n ",
                        "https://sandbox.example/checkout"
                )
        );
    }
}
