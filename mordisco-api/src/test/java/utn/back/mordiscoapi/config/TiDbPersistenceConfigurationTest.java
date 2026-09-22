package utn.back.mordiscoapi.config;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TiDbPersistenceConfigurationTest {

    private static final String JDBC_URL =
            "jdbc:mysql://synthetic.tidb.invalid:4000/portfolio"
                    + "?sslMode=VERIFY_IDENTITY&characterEncoding=UTF-8&connectionAttributes=portfolio-test";

    @Test
    void bindsProductionDatabaseAndHikariConfigurationWithoutOpeningAConnection() {
        runner("prod")
                .withPropertyValues(
                        "DB_POOL_MIN_IDLE=0",
                        "DB_POOL_MAX_SIZE=7",
                        "DB_POOL_CONNECTION_TIMEOUT_MS=12345",
                        "DB_POOL_VALIDATION_TIMEOUT_MS=4321",
                        "DB_POOL_IDLE_TIMEOUT_MS=65000",
                        "DB_POOL_MAX_LIFETIME_MS=123456",
                        "JPA_DDL_AUTO=update")
                .run(context -> {
                    assertNull(context.getStartupFailure(),
                            () -> "Unexpected startup failure: " + failureText(context.getStartupFailure()));

                    DataSourceProperties database = context.getBean(DataSourceProperties.class);
                    assertEquals(JDBC_URL, database.getUrl());
                    assertEquals("synthetic-user", database.getUsername());
                    assertEquals("synthetic-password", database.getPassword());

                    HikariDataSource hikari = context.getBean(HikariDataSource.class);
                    assertEquals(JDBC_URL, hikari.getJdbcUrl());
                    assertEquals("synthetic-user", hikari.getUsername());
                    assertEquals("synthetic-password", hikari.getPassword());
                    assertEquals(0, hikari.getMinimumIdle());
                    assertEquals(7, hikari.getMaximumPoolSize());
                    assertEquals(12345, hikari.getConnectionTimeout());
                    assertEquals(4321, hikari.getValidationTimeout());
                    assertEquals(65000, hikari.getIdleTimeout());
                    assertEquals(123456, hikari.getMaxLifetime());
                    assertEquals(
                            "VERIFY_IDENTITY",
                            hikari.getDataSourceProperties().getProperty("sslMode")
                    );

                    assertEquals("validate", context.getBean(HibernateDdlProperties.class).getDdlAuto());
                    assertEquals("never", context.getBean(SqlInitProperties.class).getMode());
                });
    }

    @Test
    void bindsProductionHikariDefaultsWithoutEnvironmentOverrides() {
        runner("prod")
                .run(context -> {
                    assertNull(context.getStartupFailure(),
                            () -> "Unexpected startup failure: " + failureText(context.getStartupFailure()));

                    HikariDataSource hikari = context.getBean(HikariDataSource.class);
                    assertEquals(0, hikari.getMinimumIdle());
                    assertEquals(4, hikari.getMaximumPoolSize());
                    assertEquals(10000, hikari.getConnectionTimeout());
                    assertEquals(5000, hikari.getValidationTimeout());
                    assertEquals(60000, hikari.getIdleTimeout());
                    assertEquals(300000, hikari.getMaxLifetime());
                });
    }

    @Test
    void failsWhenDatabaseUrlIsMissingOrBlank() {
        assertMissingOrBlankCredential("DATABASE_URL");
    }

    @Test
    void failsWhenDatabaseUsernameIsMissingOrBlank() {
        assertMissingOrBlankCredential("DATABASE_USERNAME");
    }

    @Test
    void failsWhenDatabasePasswordIsMissingOrBlank() {
        assertMissingOrBlankCredential("DATABASE_PASSWORD");
    }

    @Test
    void ordinaryProductionAlwaysValidatesAndIgnoresArbitraryDdlEnvironmentValues() {
        runner("prod")
                .withPropertyValues("JPA_DDL_AUTO=create-drop")
                .run(context -> {
                    assertNull(context.getStartupFailure(),
                            () -> "Unexpected startup failure: " + failureText(context.getStartupFailure()));
                    assertEquals("validate", context.getBean(HibernateDdlProperties.class).getDdlAuto());
                    assertEquals("never", context.getBean(SqlInitProperties.class).getMode());
                });
    }

    @Test
    void explicitSchemaBootstrapProfileSelectsOnlyUpdateOnTopOfProduction() {
        runner("prod,schema-bootstrap")
                .withPropertyValues("JPA_DDL_AUTO=create-drop")
                .run(context -> {
                    assertNull(context.getStartupFailure(),
                            () -> "Unexpected startup failure: " + failureText(context.getStartupFailure()));
                    assertEquals("update", context.getBean(HibernateDdlProperties.class).getDdlAuto());
                    assertEquals("never", context.getBean(SqlInitProperties.class).getMode());
                });
    }

    @Test
    void productionCredentialValidationDoesNotApplyToDevelopmentProfile() {
        validationRunner("dev")
                .run(context -> assertNull(
                        context.getStartupFailure(),
                        () -> "Unexpected development startup failure: " + failureText(context.getStartupFailure())));
    }

    private void assertMissingOrBlankCredential(String missingCredential) {
        for (boolean blank : List.of(false, true)) {
            List<String> databaseProperties = new ArrayList<>();
            if (!missingCredential.equals("DATABASE_URL")) {
                databaseProperties.add("DATABASE_URL=" + JDBC_URL);
            }
            if (!missingCredential.equals("DATABASE_USERNAME")) {
                databaseProperties.add("DATABASE_USERNAME=synthetic-user");
            }
            if (!missingCredential.equals("DATABASE_PASSWORD")) {
                databaseProperties.add("DATABASE_PASSWORD=synthetic-password");
            }
            if (blank) {
                databaseProperties.add(missingCredential + "=");
            }

            validationRunner("prod")
                    .withPropertyValues(databaseProperties.toArray(String[]::new))
                    .run(context -> {
                        Throwable failure = context.getStartupFailure();
                        assertNotNull(failure,
                                "Missing/blank " + missingCredential + " must fail production binding");
                        String diagnostics = failureText(failure);
                        assertTrue(diagnostics.contains(missingCredential),
                                () -> "Diagnostic must name " + missingCredential + ": " + diagnostics);
                        assertFalse(diagnostics.contains("synthetic-password"),
                                "Diagnostics must not echo credential values");
                    });
        }
    }

    private ApplicationContextRunner runner(String activeProfiles) {
        return validationRunner(activeProfiles)
                .withUserConfiguration(BindingConfiguration.class)
                .withPropertyValues(
                        "DATABASE_URL=" + JDBC_URL,
                        "DATABASE_USERNAME=synthetic-user",
                        "DATABASE_PASSWORD=synthetic-password");
    }

    private ApplicationContextRunner validationRunner(String activeProfiles) {
        return new ApplicationContextRunner()
                .withInitializer(new ConfigDataApplicationContextInitializer())
                .withUserConfiguration(ValidationOnlyConfiguration.class)
                .withPropertyValues(
                        "spring.config.name=application",
                        "spring.profiles.active=" + activeProfiles,
                        "SPRING_PROFILES_ACTIVE=" + activeProfiles);
    }

    private String failureText(Throwable failure) {
        StringBuilder text = new StringBuilder();
        while (failure != null) {
            text.append(failure).append('\n');
            failure = failure.getCause();
        }
        return text.toString();
    }

    @TestConfiguration(proxyBeanMethods = false)
    @EnableConfigurationProperties(TiDbProductionConfiguration.class)
    static class ValidationOnlyConfiguration {
    }

    @TestConfiguration(proxyBeanMethods = false)
    @EnableConfigurationProperties(DataSourceProperties.class)
    static class BindingConfiguration {
        @Bean
        @ConfigurationProperties("spring.datasource.hikari")
        HikariDataSource hikariDataSource(DataSourceProperties properties) {
            return properties.initializeDataSourceBuilder()
                    .type(HikariDataSource.class)
                    .build();
        }

        @Bean
        @ConfigurationProperties("spring.jpa.hibernate")
        HibernateDdlProperties hibernateDdlProperties() {
            return new HibernateDdlProperties();
        }

        @Bean
        @ConfigurationProperties("spring.sql.init")
        SqlInitProperties sqlInitProperties() {
            return new SqlInitProperties();
        }
    }

    static class HibernateDdlProperties {
        private String ddlAuto;

        String getDdlAuto() {
            return ddlAuto;
        }

        void setDdlAuto(String ddlAuto) {
            this.ddlAuto = ddlAuto;
        }
    }

    static class SqlInitProperties {
        private String mode;

        String getMode() {
            return mode;
        }

        void setMode(String mode) {
            this.mode = mode;
        }
    }
}
