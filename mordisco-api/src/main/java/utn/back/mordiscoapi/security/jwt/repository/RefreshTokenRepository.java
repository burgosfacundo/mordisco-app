package utn.back.mordiscoapi.security.jwt.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import utn.back.mordiscoapi.security.jwt.model.entity.RefreshToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenDigest(String tokenDigest);

    @Query("SELECT rt.usuario.id FROM RefreshToken rt WHERE rt.tokenDigest = :tokenDigest")
    Optional<Long> findUsuarioIdByTokenDigest(@Param("tokenDigest") String tokenDigest);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT rt FROM RefreshToken rt JOIN FETCH rt.usuario WHERE rt.tokenDigest = :tokenDigest")
    Optional<RefreshToken> findByTokenDigestForUpdate(@Param("tokenDigest") String tokenDigest);

    List<RefreshToken> findByUsuarioIdAndRevokedAtIsNull(Long usuarioId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE RefreshToken rt SET rt.revokedAt = :now WHERE rt.usuario.id = :userId")
    void revokeAllUserTokens(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE RefreshToken rt SET rt.revokedAt = :now WHERE rt.expiryDate < :now AND rt.revokedAt IS NULL")
    void revokeExpiredTokens(@Param("now") LocalDateTime now);

    long countByUsuarioIdAndRevokedAtIsNullAndExpiryDateAfter(Long usuarioId, LocalDateTime now);
}
