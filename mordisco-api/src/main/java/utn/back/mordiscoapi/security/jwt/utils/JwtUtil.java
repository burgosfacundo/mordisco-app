package utn.back.mordiscoapi.security.jwt.utils;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import utn.back.mordiscoapi.model.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiration:900000}") // 15 minutos
    private Long accessTokenExpiration;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    private Key secretKey;

    @PostConstruct
    public void init() {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("JWT secret debe tener al menos 256 bits");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }


    public String extractUserName(String token) {
        return extractClaim(token,Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateAccessToken(UserDetails userDetails) {
        Usuario usuario = (Usuario) userDetails;
        Map<String, Object> claims = new HashMap<>();

        claims.put("userId", usuario.getId());
        claims.put("role", usuario.getRol());
        claims.put("type", "access");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUserName(token);

            if (!isAccessToken(token)) {
                return false;
            }

            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        try {
            return "access".equals(extractClaim(token, claims -> claims.get("type")));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    public void setRefreshTokenCookie(HttpServletResponse response, String token) {
        boolean isProduction = "prod".equals(activeProfile);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", token)
                .httpOnly(true)              // No accesible desde JavaScript
                .secure(isProduction)                // Solo HTTPS
                .path("/api/auth")           // Solo en endpoints de auth
                .maxAge(30 * 24 * 60 * 60)  // 30 días
                .sameSite(isProduction ? "Strict" : "Lax")          // Protección CSRF
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        boolean isProduction = "prod".equals(activeProfile);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(isProduction)
                .path("/api/auth")
                .maxAge(0)
                .sameSite(isProduction ? "Strict" : "Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public Optional<String> extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return Optional.empty();

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    public String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        return xfHeader == null ? request.getRemoteAddr() : xfHeader.split(",")[0];
    }

}