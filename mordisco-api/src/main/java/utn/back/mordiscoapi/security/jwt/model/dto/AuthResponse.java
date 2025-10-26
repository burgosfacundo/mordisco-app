package utn.back.mordiscoapi.security.jwt.model.dto;

public record AuthResponse(
        String accessToken,
        Long userId,
        String email,
        String role,
        Long expiresIn) {}