package utn.back.mordiscoapi.security.jwt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import utn.back.mordiscoapi.common.exception.AccountDeactivatedException;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.security.jwt.utils.JwtUtil;
import utn.back.mordiscoapi.security.jwt.model.dto.AuthRequest;
import utn.back.mordiscoapi.security.jwt.model.dto.AuthResponse;
import utn.back.mordiscoapi.security.jwt.model.dto.SessionInfo;
import utn.back.mordiscoapi.security.jwt.model.entity.RefreshToken;
import utn.back.mordiscoapi.security.jwt.service.RefreshTokenService;

import java.nio.file.AccessDeniedException;
import java.util.List;


@Tag(name = "Usuarios", description = "Operaciones relacionadas a la autenticación y autorización de usuarios")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.access.expiration:900000}")
    private Long accessTokenExpiration;

    /**
     * Endpoint para autenticar un usuario y generar un token JWT.
     * @return Un objeto AuthenticationResponse que contiene el token JWT.
     */
    @Operation(summary = "Autenticar usuario", description = "Permite a un usuario autenticarse y obtener un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token JWT generado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody @Valid AuthRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) throws NotFoundException, AccountDeactivatedException {
        UserDetails userDetails;

        try {
            userDetails = userDetailsService.loadUserByUsername(request.email());
        }catch (UsernameNotFoundException e){
            throw new BadCredentialsException("Credenciales inválidas");
        }

        Usuario usuario = (Usuario) userDetails;

        // Verificar si el usuario está dado de baja
        if (usuario.getBajaLogica() != null && usuario.getBajaLogica()) {
            throw new AccountDeactivatedException(
                    usuario.getMotivoBaja() != null ? usuario.getMotivoBaja() : "Cuenta desactivada"
            );
        }

        // Autenticar
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(), request.password()));

        // Generar access token (15 min)
        String accessToken = jwtUtil.generateAccessToken(userDetails);

        // Generar refresh token (30 días) y guardarlo en BD
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(
                usuario.getId(),
                httpRequest.getHeader("User-Agent"),
                jwtUtil.getClientIP(httpRequest)
        );

        // Enviar refresh token en httpOnly cookie (SEGURO)
        jwtUtil.setRefreshTokenCookie(httpResponse, refreshToken.getToken());

        return ResponseEntity.ok(new AuthResponse(
                accessToken,
                usuario.getId(),
                usuario.getEmail(),
                usuario.getRol().getNombre(),
                accessTokenExpiration,
                false, // bajaLogica siempre false si pasa la validación
                null   // motivoBaja siempre null si pasa la validación
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws NotFoundException, AccessDeniedException {

        // Leer refresh token de la cookie
        String refreshTokenValue = jwtUtil.extractRefreshTokenFromCookie(request)
                .orElseThrow(() -> new AccessDeniedException("No se encontró refresh token"));

        try {
            // Verificar y rotar el refresh token
            RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(
                    refreshTokenValue,
                    request.getHeader("User-Agent"),
                    jwtUtil.getClientIP(request)
            );

            Usuario usuario = newRefreshToken.getUsuario();

            // Generar nuevo access token
            String newAccessToken = jwtUtil.generateAccessToken(usuario);

            // Actualizar cookie con nuevo refresh token
            jwtUtil.setRefreshTokenCookie(response, newRefreshToken.getToken());

            return ResponseEntity.ok(new AuthResponse(
                    newAccessToken,
                    usuario.getId(),
                    usuario.getEmail(),
                    usuario.getRol().getNombre(),
                    accessTokenExpiration,
                    false, // bajaLogica siempre false en refresh
                    null   // motivoBaja siempre null en refresh
            ));

        } catch (SecurityException | NotFoundException e) {
            // Token comprometido - limpiar cookies
            jwtUtil.clearRefreshTokenCookie(response);
            throw e;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        jwtUtil.extractRefreshTokenFromCookie(request).ifPresent(token -> {
            refreshTokenService.revokeToken(token);
            jwtUtil.clearRefreshTokenCookie(response);
        });

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logoutAllDevices(
            HttpServletResponse response,
            Authentication authentication) {

        Usuario usuario = (Usuario) authentication.getPrincipal();
        refreshTokenService.revokeAllUserSessions(usuario.getId());
        jwtUtil.clearRefreshTokenCookie(response);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sessions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SessionInfo>> getActiveSessions(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        // Implementar endpoint para mostrar sesiones activas al usuario
        return ResponseEntity.ok(List.of());
    }
}
