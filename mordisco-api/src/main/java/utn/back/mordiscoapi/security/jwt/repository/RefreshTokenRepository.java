package utn.back.mordiscoapi.security.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import utn.back.mordiscoapi.security.jwt.model.entity.RefreshToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUsuarioIdAndRevokedAtIsNull(Long usuarioId);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revokedAt = :now WHERE rt.usuario.id = :userId")
    void revokeAllUserTokens(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revokedAt = :now WHERE rt.expiryDate < :now AND rt.revokedAt IS NULL")
    void revokeExpiredTokens(@Param("now") LocalDateTime now);

    long countByUsuarioIdAndRevokedAtIsNullAndExpiryDateAfter(
            Long usuarioId, LocalDateTime now);
}