package utn.back.mordiscoapi.security.jwt.model;

public record AuthenticationRequest(String email,String password) {
}