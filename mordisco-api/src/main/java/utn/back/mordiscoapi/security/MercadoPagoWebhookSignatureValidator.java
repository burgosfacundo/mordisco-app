package utn.back.mordiscoapi.security;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.HexFormat;
import java.util.regex.Pattern;

/**
 * Validates Mercado Pago's v1 webhook signature manifest.
 *
 * <p>The pinned Mercado Pago SDK does not expose the official webhook signature
 * validator, so this class follows the provider's documented manifest exactly.
 * It deliberately has no logging so a rejected request cannot disclose a
 * signature or webhook secret.</p>
 */
@Component
public class MercadoPagoWebhookSignatureValidator {
    private static final String SIGNATURE_ALGORITHM = "HmacSHA256";
    private static final Pattern DATA_ID_PATTERN = Pattern.compile("[A-Za-z0-9]+");
    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile("[0-9]+");
    private static final Pattern HEX_SIGNATURE_PATTERN = Pattern.compile("[0-9a-fA-F]{64}");

    public boolean isValid(String signatureHeader, String requestId, String dataId, String secret) {
        if (signatureHeader == null || signatureHeader.isBlank()
                || secret == null || secret.isBlank()) {
            return false;
        }

        Map<String, String> signatureValues = parseSignatureHeader(signatureHeader);
        String timestamp = signatureValues.get("ts");
        String providedSignature = signatureValues.get("v1");
        if (timestamp == null || !TIMESTAMP_PATTERN.matcher(timestamp).matches()
                || providedSignature == null
                || !HEX_SIGNATURE_PATTERN.matcher(providedSignature).matches()) {
            return false;
        }

        String normalizedDataId = null;
        if (dataId != null && !dataId.isBlank()) {
            normalizedDataId = DATA_ID_PATTERN.matcher(dataId).matches()
                    ? dataId.toLowerCase(Locale.ROOT)
                    : dataId;
        }

        String normalizedRequestId = requestId == null || requestId.isBlank() ? null : requestId;
        String manifest = buildManifest(normalizedDataId, normalizedRequestId, timestamp);

        try {
            Mac mac = Mac.getInstance(SIGNATURE_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SIGNATURE_ALGORITHM));
            byte[] expectedSignature = mac.doFinal(manifest.getBytes(StandardCharsets.UTF_8));
            byte[] receivedSignature = HexFormat.of().parseHex(providedSignature);
            return MessageDigest.isEqual(expectedSignature, receivedSignature);
        } catch (GeneralSecurityException | IllegalArgumentException exception) {
            return false;
        }
    }

    private Map<String, String> parseSignatureHeader(String signatureHeader) {
        Map<String, String> values = new HashMap<>();
        String[] parts = signatureHeader.split(",", -1);
        for (String rawPart : parts) {
            String part = rawPart.trim();
            if (part.isEmpty()) {
                return Map.of();
            }

            int separator = part.indexOf('=');
            if (separator <= 0 || separator != part.lastIndexOf('=')) {
                return Map.of();
            }

            String key = part.substring(0, separator).trim();
            String value = part.substring(separator + 1).trim();
            if ((!"ts".equals(key) && !"v1".equals(key))
                    || value.isBlank() || values.putIfAbsent(key, value) != null) {
                return Map.of();
            }
        }
        return values;
    }

    private String buildManifest(String dataId, String requestId, String timestamp) {
        StringBuilder manifest = new StringBuilder();
        if (dataId != null) {
            manifest.append("id:").append(dataId).append(';');
        }
        if (requestId != null) {
            manifest.append("request-id:").append(requestId).append(';');
        }
        manifest.append("ts:").append(timestamp).append(';');
        return manifest.toString();
    }
}
