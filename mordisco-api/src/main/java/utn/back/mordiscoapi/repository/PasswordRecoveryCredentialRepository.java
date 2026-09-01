package utn.back.mordiscoapi.repository;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import utn.back.mordiscoapi.model.entity.PasswordRecoveryCredential;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
public interface PasswordRecoveryCredentialRepository extends JpaRepository<PasswordRecoveryCredential, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PasswordRecoveryCredential> findByUsuarioId(Long usuarioId);
    @Query("select credential.usuario.id from PasswordRecoveryCredential credential where credential.tokenDigest = :digest")
    Optional<Long> findUsuarioIdByTokenDigest(@Param("digest") String digest);
    @Modifying
    @Query("""
            update PasswordRecoveryCredential credential
            set credential.consumedAt = :consumedAt
            where credential.usuario.id = :usuarioId
              and credential.tokenDigest = :digest
              and credential.consumedAt is null
              and credential.expiresAt > :consumedAt
            """)
    int consumeIfCurrentAndUnexpired(
            @Param("usuarioId") Long usuarioId,
            @Param("digest") String digest,
            @Param("consumedAt") LocalDateTime consumedAt
    );
    @Query("""
            select credential.id from PasswordRecoveryCredential credential
            where credential.cooldownUntil <= :now
              and (credential.consumedAt is not null or credential.expiresAt <= :now)
            order by credential.id
            """)
    List<Long> findCleanupCandidateIds(@Param("now") LocalDateTime now, Pageable pageable);

    @Modifying
    @Query("""
            delete from PasswordRecoveryCredential credential
            where credential.id in :credentialIds
              and credential.cooldownUntil <= :now
              and (credential.consumedAt is not null or credential.expiresAt <= :now)
            """)
    int deleteExpiredOrConsumedAfterCooldown(
            @Param("credentialIds") List<Long> credentialIds,
            @Param("now") LocalDateTime now
    );
}
