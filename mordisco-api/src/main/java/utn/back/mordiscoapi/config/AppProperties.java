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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String frontendUrl;
    private String maintenanceSecret;
    private List<String> websocketAllowedOrigins = new ArrayList<>();
    private JwtProperties jwt = new JwtProperties();
    @Valid
    private PasswordRecoveryProperties passwordRecovery = new PasswordRecoveryProperties();
    private MercadoPagoProperties mercadoPago = new MercadoPagoProperties();
    private JasyptEncryptorProperties jasypt = new JasyptEncryptorProperties();

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @PostConstruct
    void validateConfiguration() {
        validateWebSocketOrigins(activeProfile);
        validateMercadoPagoConfiguration(activeProfile);
        validateMaintenanceSecret(activeProfile);
    }

    void validateWebSocketOrigins(String profile) {
        boolean missing = websocketAllowedOrigins == null
                || websocketAllowedOrigins.isEmpty()
                || websocketAllowedOrigins.stream().anyMatch(origin -> origin == null || origin.isBlank());
        boolean wildcard = websocketAllowedOrigins != null
                && websocketAllowedOrigins.stream().anyMatch(origin -> origin.contains("*"));
        if (wildcard || (isProductionProfile(profile) && missing)) {
            throw new IllegalStateException("Exact WebSocket allowed origins are required");
        }
    }

    void validateMaintenanceSecret(String profile) {
        if (isProductionProfile(profile)
                && (maintenanceSecret == null || maintenanceSecret.isBlank())) {
            throw new IllegalStateException("MAINTENANCE_SECRET must be provided for the prod profile");
        }
    }

    void validateMercadoPagoConfiguration(String profile) {
        String environment = mercadoPago.getEnvironment();
        if (environment == null
                || (!"sandbox".equalsIgnoreCase(environment)
                && !"production".equalsIgnoreCase(environment))) {
            throw new IllegalStateException("Mercado Pago environment must be sandbox or production");
        }

        if (!isProductionProfile(profile)) {
            return;
        }

        if (mercadoPago.getWebhookSecret() == null || mercadoPago.getWebhookSecret().isBlank()) {
            throw new IllegalStateException("Mercado Pago webhook secret is required in production");
        }
        String notificationUrl = mercadoPago.getNotificationUrl();
        if (notificationUrl == null || notificationUrl.isBlank()) {
            throw new IllegalStateException("Mercado Pago notification URL is required in production");
        }

        try {
            URI uri = URI.create(notificationUrl);
            String host = uri.getHost();
            if (!"https".equalsIgnoreCase(uri.getScheme())
                    || host == null
                    || host.isBlank()
                    || "localhost".equalsIgnoreCase(host)
                    || "127.0.0.1".equals(host)
                    || !"/api/pagos/webhook".equals(uri.getPath())) {
                throw new IllegalStateException("Mercado Pago notification URL must be a public HTTPS webhook URL");
            }
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("Mercado Pago notification URL must be a public HTTPS webhook URL");
        }
    }

    private boolean isProductionProfile(String profile) {
        if (profile == null) {
            return false;
        }
        for (String configuredProfile : profile.split(",")) {
            if ("prod".equalsIgnoreCase(configuredProfile.trim())) {
                return true;
            }
        }
        return false;
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
        private String webhookSecret;
        private String environment = "sandbox";

        public boolean isSandbox() {
            return "sandbox".equalsIgnoreCase(environment);
        }
    }

    @Getter
    @Setter
    public static class JasyptEncryptorProperties{
        private String password;
        private String algorithm;
    }
}

