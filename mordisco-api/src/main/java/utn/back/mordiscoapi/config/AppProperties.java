package utn.back.mordiscoapi.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String frontendUrl;
    private JwtProperties jwt = new JwtProperties();
    private MercadoPagoProperties mercadoPago = new MercadoPagoProperties();
    private JasyptEncryptorProperties jasypt = new JasyptEncryptorProperties();

    @Getter
    @Setter
    public static class JwtProperties {
        private long refreshExpiration;
        private long accessExpiration;
        private String secret;
        private long recoveryPasswordExpiration;
        private long maxSessions;
    }

    @Getter
    @Setter
    public static class MercadoPagoProperties {
        private String accessToken;
        private String publicKey;
        private String notificationUrl;
    }

    @Getter
    @Setter
    public static class JasyptEncryptorProperties{
        private String password;
        private String algorithm;
    }
}

