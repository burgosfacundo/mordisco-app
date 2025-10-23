package utn.back.mordiscoapi.config;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import utn.back.mordiscoapi.security.jwt.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true,securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtRequestFilter jwtRequestFilter;

    private static final String[] AUTH_WHITELIST = {
            // Swagger v3 endpoints
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/configuration/**",
            "/webjars/**",

            // Endpoints públicos de autenticación
            "/api/usuarios/save",
            "/api/auth/login",

            // Endpoints públicos de consulta (sin datos sensibles)
            "/api/restaurantes/{id}",
            "/api/restaurantes",
            "/api/restaurantes/estado",
            "/api/restaurantes/ciudad",
            "/api/restaurantes/nombre",
            "/api/restaurantes/promociones",
            "/api/menus/{restauranteId}",
            "/api/promociones/{id}",

            // Otros endpoints públicos opcionales
            "/api/public/**"
    };


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> corsConfiguration()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfiguration corsConfiguration() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.addAllowedOriginPattern("*");
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT","OPTIONS","PATCH", "DELETE"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("Authorization"));
        return corsConfiguration;
    }
}
