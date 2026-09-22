package utn.back.mordiscoapi.security;

import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MercadoPagoWebhookSignatureValidatorTest {
    private static final String SECRET = "synthetic-webhook-secret";
    private static final String REQUEST_ID = "synthetic-request-id";
    private static final String TIMESTAMP = "1735689600";

    private final MercadoPagoWebhookSignatureValidator validator = new MercadoPagoWebhookSignatureValidator();

    @Test
    void validatesCanonicalHeaderAndNormalizesUppercaseAlphanumericDataId() throws Exception {
        String signature = signature("ABC123", REQUEST_ID, TIMESTAMP).replace(",", ", ");

        assertTrue(validator.isValid(signature, REQUEST_ID, "ABC123", SECRET));
    }

    @Test
    void preservesNonAlphanumericDataIdInManifest() throws Exception {
        String dataId = "ABC-123";
        String signature = signature(dataId, REQUEST_ID, TIMESTAMP);

        assertTrue(validator.isValid(signature, REQUEST_ID, dataId, SECRET));
    }

    @Test
    void supportsManifestWithAbsentOptionalPairs() throws Exception {
        String signature = signature(null, null, TIMESTAMP);

        assertTrue(validator.isValid(signature, null, null, SECRET));
    }

    @Test
    void rejectsMissingMalformedAndInvalidSignatures() throws Exception {
        String valid = signature("abc123", REQUEST_ID, TIMESTAMP);
        String signatureValue = valid.substring(valid.indexOf("v1=") + 3);

        assertFalse(validator.isValid(null, REQUEST_ID, "abc123", SECRET));
        assertFalse(validator.isValid("ts=" + TIMESTAMP, REQUEST_ID, "abc123", SECRET));
        assertFalse(validator.isValid("v1=" + signatureValue, REQUEST_ID, "abc123", SECRET));
        assertFalse(validator.isValid(valid.replace("v1=", "v1=00"), REQUEST_ID, "abc123", SECRET));
        assertFalse(validator.isValid(valid, REQUEST_ID, "abc123", ""));
    }

    @Test
    void rejectsDuplicateUnknownAndEmptyHeaderParts() throws Exception {
        String valid = signature("abc123", REQUEST_ID, TIMESTAMP);
        String signatureValue = valid.substring(valid.indexOf("v1=") + 3);

        assertFalse(validator.isValid(valid + ",v1=" + signatureValue,
                REQUEST_ID, "abc123", SECRET));
        assertFalse(validator.isValid("ts=" + TIMESTAMP + ",v1=" + signatureValue + ",unknown=value",
                REQUEST_ID, "abc123", SECRET));
        assertFalse(validator.isValid("ts=" + TIMESTAMP + ",v1",
                REQUEST_ID, "abc123", SECRET));
        assertFalse(validator.isValid("ts=" + TIMESTAMP + ",",
                REQUEST_ID, "abc123", SECRET));
    }

    private String signature(String dataId, String requestId, String timestamp) throws Exception {
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
        byte[] digest = mac.doFinal(manifest.toString().getBytes(StandardCharsets.UTF_8));
        return "ts=" + timestamp + ",v1=" + toHex(digest);
    }

    private String toHex(byte[] value) {
        StringBuilder result = new StringBuilder(value.length * 2);
        for (byte item : value) {
            result.append(String.format("%02x", item));
        }
        return result.toString();
    }
}
