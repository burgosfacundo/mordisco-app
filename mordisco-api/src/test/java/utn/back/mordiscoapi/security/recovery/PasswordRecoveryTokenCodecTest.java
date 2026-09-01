package utn.back.mordiscoapi.security.recovery;
import org.junit.jupiter.api.Test;
import java.security.SecureRandom;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
class PasswordRecoveryTokenCodecTest {
    @Test
    void generatesAnUnpaddedUrlSafeTokenFromExactlyThirtyTwoRandomBytes() {
        RecordingSecureRandom secureRandom = new RecordingSecureRandom();
        PasswordRecoveryTokenCodec codec = new PasswordRecoveryTokenCodec(secureRandom);
        String token = codec.generateToken();
        assertEquals(32, secureRandom.lastRequestedLength());
        assertEquals(43, token.length());
        assertTrue(token.matches("[A-Za-z0-9_-]{43}"));
    }
    @Test
    void hashesTokensAsLowercaseSha256HexWithoutRetainingTheRawValue() {
        PasswordRecoveryTokenCodec codec = new PasswordRecoveryTokenCodec(new SecureRandom());
        String token = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        String digest = codec.digest(token);
        assertEquals("0f007385b6f9d4b7eeb2748605afe1a984a0a3bfa3f014d09e2a784ce9e5cd1a", digest);
        assertFalse(token.equals(digest));
    }
    @Test
    void rejectsMalformedTokensBeforeDigestingThem() {
        PasswordRecoveryTokenCodec codec = new PasswordRecoveryTokenCodec(new SecureRandom());
        assertDoesNotThrow(() -> codec.validateTokenShape("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
        assertThrows(IllegalArgumentException.class, () -> codec.validateTokenShape("not-a-recovery-token"));
        assertThrows(IllegalArgumentException.class, () -> codec.validateTokenShape("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="));
        assertThrows(IllegalArgumentException.class, () -> codec.digest("not-a-recovery-token"));
    }
    private static final class RecordingSecureRandom extends SecureRandom {
        private int lastRequestedLength;
        @Override
        public void nextBytes(byte[] bytes) {
            lastRequestedLength = bytes.length;
            for (int index = 0; index < bytes.length; index++) {
                bytes[index] = (byte) index;
            }
        }
        int lastRequestedLength() {
            return lastRequestedLength;
        }
    }
}
