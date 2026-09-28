package utn.back.mordiscoapi.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import utn.back.mordiscoapi.security.jwt.utils.JwtRequestFilter;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class SecurityConfiguration {

    private static final String DEV_HTTP_ORIGIN = "http://localhost:4200";

    private static final Set<String> CSRF_PROTECTED_PATHS = Set.of(
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/auth/logout");
    private static final RequestMatcher CSRF_PROTECTED_REQUESTS = request ->
            "POST".equals(request.getMethod()) && CSRF_PROTECTED_PATHS.contains(requestPath(request));

    private final JwtRequestFilter jwtRequestFilter;
    private final CorsConfigurationSource httpCorsConfigurationSource;
    private final String activeProfile;
    private final boolean csrfCookieSecure;
    private final String csrfCookieSameSite;

    public SecurityConfiguration(
            JwtRequestFilter jwtRequestFilter,
            @Value("${app.http-allowed-origins:}") String configuredHttpOrigins,
            @Value("${spring.profiles.active:dev}") String activeProfile,
            @Value("${server.servlet.session.cookie.secure:false}") boolean csrfCookieSecure,
            @Value("${server.servlet.session.cookie.same-site:lax}") String csrfCookieSameSite) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.httpCorsConfigurationSource = createCorsConfigurationSource(activeProfile, configuredHttpOrigins);
        this.activeProfile = activeProfile;
        this.csrfCookieSecure = csrfCookieSecure;
        this.csrfCookieSameSite = csrfCookieSameSite;
    }

    private static final String[] AUTH_WHITELIST = {
            // Swagger v3 endpoints
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/configuration/**",
            "/webjars/**",

            // Auth endpoints
            "/api/usuarios/save",
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/auth/logout",
            "/api/usuarios/recover-password",
            "/api/usuarios/reset-password",
            "/api/ws/**",
            "/ws/**",

            // Endpoints públicos de consulta
            "/api/restaurantes/{id}",
            "/api/restaurantes",
            "/api/restaurantes/estado",
            "/api/restaurantes/ciudad",
            "/api/restaurantes/nombre",
            "/api/restaurantes/ubicacion",
            "/api/restaurantes/ubicacion/promociones",
            "/api/restaurantes/horarios/{idRestaurante}",
            "/api/menus/{restauranteId}",
            "/api/promociones/{id}",
            "/api/productos",
            "/api/productos/{id}",
            "/api/calificaciones/pedido/{pedidoId}",
            "/api/calificaciones/restaurante/{restauranteId}",
            "/api/calificaciones/repartidor/pedido/{pedidoId}",
            "/api/calificaciones/restaurante/{restauranteId}/estadisticas",



            "/api/public/**"
    };


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository())
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                        .requireCsrfProtectionMatcher(CSRF_PROTECTED_REQUESTS)
                        .withObjectPostProcessor(new ObjectPostProcessor<CsrfFilter>() {
                            @Override
                            public <O extends CsrfFilter> O postProcess(O filter) {
                                filter.setAccessDeniedHandler(csrfAccessDeniedHandler());
                                return filter;
                            }
                        }))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/auth/csrf").permitAll()
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private CookieCsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repository = new CookieCsrfTokenRepository();
        repository.setCookiePath("/api/auth");
        repository.setCookieHttpOnly(true);
        repository.setCookieCustomizer(cookie -> cookie
                .path("/api/auth")
                .secure(csrfCookieSecure)
                .httpOnly(true)
                .sameSite(normalizeSameSite(csrfCookieSameSite)));
        return repository;
    }

    private static String requestPath(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }

    private static String normalizeSameSite(String sameSite) {
        if (sameSite == null || sameSite.isBlank()) {
            return "Lax";
        }
        String normalized = sameSite.trim().toLowerCase(Locale.ROOT);
        return Character.toUpperCase(normalized.charAt(0)) + normalized.substring(1);
    }

    private static AccessDeniedHandler csrfAccessDeniedHandler() {
        return (request, response, exception) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write("{\"error\":\"forbidden\",\"message\":\"Invalid CSRF token\"}");
        };
    }

    static final class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {
        private final CsrfTokenRequestHandler xorRequestAttributeHandler =
                new XorCsrfTokenRequestAttributeHandler();

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response,
                           Supplier<CsrfToken> deferredCsrfToken) {
            this.xorRequestAttributeHandler.handle(request, response, deferredCsrfToken);
            deferredCsrfToken.get();
        }

        @Override
        public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
            return request.getHeader(csrfToken.getHeaderName());
        }
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return httpCorsConfigurationSource;
    }

    static CorsConfigurationSource createCorsConfigurationSource(String profile, String configuredOrigins) {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(parseHttpAllowedOrigins(profile, configuredOrigins));
        configuration.setAllowedMethods(List.of("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-XSRF-TOKEN"));
        configuration.setExposedHeaders(List.of());
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    static List<String> parseHttpAllowedOrigins(String profile, String configuredOrigins) {
        if (configuredOrigins == null || configuredOrigins.isBlank()) {
            if (isProfileActive(profile, "prod")) {
                throw new IllegalStateException("Exact HTTP allowed origins are required in production");
            }
            return isProfileActive(profile, "dev") ? List.of(DEV_HTTP_ORIGIN) : List.of();
        }

        List<String> origins = Arrays.stream(configuredOrigins.split(",", -1))
                .map(String::trim)
                .toList();
        if (origins.stream().anyMatch(origin -> origin.isEmpty() || !isExactHttpOrigin(origin))) {
            throw new IllegalStateException("HTTP allowed origins must be exact HTTP(S) origins");
        }
        return List.copyOf(origins);
    }

    private static boolean isProfileActive(String profiles, String expectedProfile) {
        return profiles != null && Arrays.stream(profiles.split(","))
                .map(String::trim)
                .anyMatch(expectedProfile::equals);
    }

    private static boolean isExactHttpOrigin(String origin) {
        try {
            URI uri = new URI(origin);
            String scheme = uri.getScheme();
            return !origin.contains("*")
                    && ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                    && uri.getHost() != null
                    && uri.getRawUserInfo() == null
                    && (uri.getRawPath() == null || uri.getRawPath().isEmpty())
                    && uri.getRawQuery() == null
                    && uri.getRawFragment() == null
                    && uri.getPort() <= 65535;
        } catch (URISyntaxException exception) {
            return false;
        }
    }

}
