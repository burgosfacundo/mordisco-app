package utn.back.mordiscoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utn.back.mordiscoapi.security.jwt.JwtUtil;
import utn.back.mordiscoapi.security.jwt.model.AuthenticationRequest;
import utn.back.mordiscoapi.security.jwt.model.AuthenticationResponse;

@Tag(name = "Usuarios", description = "Operaciones relacionadas a la autenticación y autorización de usuarios")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * Endpoint para autenticar un usuario y generar un token JWT.
     * @param authenticationRequest Contiene las credenciales del usuario (email y contraseña).
     * @return Un objeto AuthenticationResponse que contiene el token JWT.
     */
    @Operation(summary = "Autenticar usuario", description = "Permite a un usuario autenticarse y obtener un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token JWT generado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.email(), authenticationRequest.password()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.email());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse((jwt)));
    }
}
