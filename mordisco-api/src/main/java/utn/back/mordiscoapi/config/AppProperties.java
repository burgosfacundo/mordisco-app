package utn.back.mordiscoapi.config;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String frontendUrl;
    private List<String> websocketAllowedOrigins = new ArrayList<>();
    private JwtProperties jwt = new JwtProperties();
    @Valid
    private PasswordRecoveryProperties passwordRecovery = new PasswordRecoveryProperties();
    private MercadoPagoProperties mercadoPago = new MercadoPagoProperties();
    private JasyptEncryptorProperties jasypt = new JasyptEncryptorProperties();

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @PostConstruct
    void validateWebSocketOrigins() {
        validateWebSocketOrigins(activeProfile);
    }

    void validateWebSocketOrigins(String profile) {
        boolean missing = websocketAllowedOrigins == null
                || websocketAllowedOrigins.isEmpty()
                || websocketAllowedOrigins.stream().anyMatch(origin -> origin == null || origin.isBlank());
        boolean wildcard = websocketAllowedOrigins != null
                && websocketAllowedOrigins.stream().anyMatch(origin -> origin.contains("*"));
        if (wildcard || ("prod".equals(profile) && missing)) {
            throw new IllegalStateException("Exact WebSocket allowed origins are required");
        }
    }

    @Getter
    @Setter
    public static class JwtProperties {
        private long refreshExpiration;
        private long accessExpiration;
        private String secret;
        private long maxSessions;
    }

    @Getter
    @Setter
    public static class PasswordRecoveryProperties {
        @Min(300)
        @Max(86400)
        private long expirationSeconds = 3600;

        @Min(60)
        @Max(86400)
        private long cooldownSeconds = 300;
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

