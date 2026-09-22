package utn.back.mordiscoapi.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Profile("prod")
@Validated
@ConfigurationProperties(prefix = "spring.datasource")
public class TiDbProductionConfiguration {

    @NotBlank(message = "DATABASE_URL must be provided for the prod profile")
    private String url;

    @NotBlank(message = "DATABASE_USERNAME must be provided for the prod profile")
    private String username;

    @NotBlank(message = "DATABASE_PASSWORD must be provided for the prod profile")
    private String password;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
