package utn.back.mordiscoapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Anotación para indicar que esta clase es una configuración de Spring
public class OpenApiConfig {
    @Bean // Anotación para indicar que esta función devuelve un bean de Spring
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP) // Tipo de esquema de seguridad
                                .scheme("bearer") // Esquema de seguridad
                                .bearerFormat("JWT"))) // Formato del token JWT
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .info(new Info()
                        .title("🚀 API Mordisco") // Título principal
                        .version("1.0.0")                    // Versión
                        .description("Documentación oficial de la API REST de Mordisco")); // Descripción
    }
}
