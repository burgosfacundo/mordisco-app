package utn.back.mordiscoapi.repository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class PasswordRecoveryCredentialSchemaIntegrationTest {
    @Container
    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Test
    void createsTheOneRowPerUserCredentialSchemaWithMySqlConstraints() {
        List<String> timestampColumns = jdbcTemplate.queryForList("""
                select column_name
                from information_schema.columns
                where table_schema = database()
                  and table_name = 'password_recovery_credentials'
                  and data_type = 'datetime'
                  and datetime_precision = 6
                order by column_name
                """, String.class);
        List<String> uniqueConstraints = jdbcTemplate.queryForList("""
                select constraint_name
                from information_schema.table_constraints
                where table_schema = database()
                  and table_name = 'password_recovery_credentials'
                  and constraint_type = 'UNIQUE'
                order by constraint_name
                """, String.class);
        List<String> foreignKeys = jdbcTemplate.queryForList("""
                select constraint_name
                from information_schema.table_constraints
                where table_schema = database()
                  and table_name = 'password_recovery_credentials'
                  and constraint_type = 'FOREIGN KEY'
                """, String.class);
        assertEquals(List.of("consumed_at", "cooldown_until", "expires_at", "issued_at"), timestampColumns);
        assertTrue(uniqueConstraints.contains("UK_password_recovery_credential_usuario"));
        assertTrue(uniqueConstraints.contains("UK_password_recovery_credential_digest"));
        assertTrue(foreignKeys.contains("FK_password_recovery_credential_usuario"));
    }
}
