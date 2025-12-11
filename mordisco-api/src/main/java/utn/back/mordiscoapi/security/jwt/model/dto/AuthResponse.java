package utn.back.mordiscoapi.security.jwt.model.dto;

public record AuthResponse(
        String accessToken,
        Long userId,
        String email,
        String nombre,
        String role,
        Long expiresIn,
        Boolean bajaLogica,
        String motivoBaja) {}