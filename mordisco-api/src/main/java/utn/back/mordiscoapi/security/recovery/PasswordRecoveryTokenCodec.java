package utn.back.mordiscoapi.security.recovery;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
@Component
public class PasswordRecoveryTokenCodec {
    private static final int TOKEN_BYTES = 32;
    private static final String TOKEN_PATTERN = "[A-Za-z0-9_-]{43}";
    private final SecureRandom secureRandom;
    public PasswordRecoveryTokenCodec(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }
    public String generateToken() {
        byte[] tokenBytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
    public void validateTokenShape(String token) {
        if (token == null || !token.matches(TOKEN_PATTERN)) {
            throw new IllegalArgumentException("Invalid password recovery token shape");
        }
    }
    public String digest(String token) {
        validateTokenShape(token);
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.US_ASCII));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
