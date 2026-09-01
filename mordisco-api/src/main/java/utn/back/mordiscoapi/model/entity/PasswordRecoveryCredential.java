package utn.back.mordiscoapi.model.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
@Entity
@Table(
        name = "password_recovery_credentials",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_password_recovery_credential_usuario", columnNames = "usuario_id"),
                @UniqueConstraint(name = "UK_password_recovery_credential_digest", columnNames = "token_digest")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class PasswordRecoveryCredential {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "usuario_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_password_recovery_credential_usuario")
    )
    private Usuario usuario;
    @Column(name = "token_digest", nullable = false, length = 64)
    private String tokenDigest;
    @Column(name = "issued_at", nullable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime issuedAt;
    @Column(name = "expires_at", nullable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime expiresAt;
    @Column(name = "cooldown_until", nullable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime cooldownUntil;
    @Column(name = "consumed_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime consumedAt;
}
