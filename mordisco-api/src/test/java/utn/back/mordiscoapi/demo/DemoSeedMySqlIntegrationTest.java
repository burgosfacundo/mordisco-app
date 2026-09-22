package utn.back.mordiscoapi.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import utn.back.mordiscoapi.repository.DireccionRepository;
import utn.back.mordiscoapi.repository.HorarioRepository;
import utn.back.mordiscoapi.repository.ImagenRepository;
import utn.back.mordiscoapi.repository.MenuRepository;
import utn.back.mordiscoapi.repository.PagoRepository;
import utn.back.mordiscoapi.repository.PedidoRepository;
import utn.back.mordiscoapi.repository.ProductoPedidoRepository;
import utn.back.mordiscoapi.repository.ProductoRepository;
import utn.back.mordiscoapi.repository.RestauranteRepository;
import utn.back.mordiscoapi.repository.RolRepository;
import utn.back.mordiscoapi.repository.UsuarioRepository;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {
        "spring.profiles.active=test",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never",
        "spring.mail.host=127.0.0.1",
        "spring.mail.port=2525",
        "spring.mail.username=synthetic-test@example.invalid",
        "spring.mail.password=synthetic-mail-password",
        "app.mercadopago.access-token=synthetic-mercadopago-access-token",
        "app.mercadopago.public-key=synthetic-mercadopago-public-key",
        "app.mercadopago.notification-url=https://synthetic.example.invalid/api/pagos/webhook",
        "OPENWEATHERMAP_API_KEY=synthetic-openweathermap-key"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
@Import(DemoSeedMySqlIntegrationTest.SeedConfiguration.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class DemoSeedMySqlIntegrationTest {

    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0");

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
    }

    @Autowired
    private DemoSeedService seedService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Environment environment;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute(DemoSeedService.CREATE_MARKER_TABLE_SQL);
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        try {
            for (String table : List.of(
                    "productos_pedidos",
                    "pagos",
                    "pedidos",
                    "horarios_atencion",
                    "restaurantes",
                    "productos",
                    "menus",
                    "imagenes",
                    "direcciones",
                    "usuarios",
                    "roles",
                    "demo_seed_markers")) {
                jdbcTemplate.update("DELETE FROM " + table);
            }
        } finally {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @Test
    void initialSeedUsesRealSchemaConstraintsAndStoresEveryDocumentedCredential() {
        seedService.seed();

        assertEquals("never", environment.getProperty("spring.sql.init.mode"));
        assertEquals(1, count("demo_seed_markers"));
        assertEquals(3, count("roles"));
        assertEquals(3, count("usuarios"));
        assertEquals(2, count("direcciones"));
        assertEquals(7, count("horarios_atencion"));
        assertEquals(4, count("imagenes"));
        assertEquals(1, count("restaurantes"));
        assertEquals(1, count("menus"));
        assertEquals(3, count("productos"));
        assertEquals(1, count("pedidos"));
        assertEquals(3, count("productos_pedidos"));
        assertEquals(1, count("pagos"));
        assertEquals(1, count("demo_seed_markers", "seed_key = ?", DemoSeedService.SEED_KEY));
        assertEquals(1, jdbcTemplate.queryForObject("""
                select count(*)
                from information_schema.key_column_usage
                where constraint_schema = database()
                  and table_name = 'restaurantes'
                  and column_name = 'menu_id'
                  and referenced_table_name = 'menus'
                  and referenced_column_name = 'id'
                """, Integer.class));

        assertCredentialsMatchStoredBcryptHashes();
        assertEquals(1, jdbcTemplate.queryForObject("""
                select count(*)
                from information_schema.statistics
                where table_schema = database()
                  and table_name = 'demo_seed_markers'
                  and index_name = 'uk_demo_seed_markers_seed_key'
                """, Integer.class));
        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update("""
                insert into demo_seed_markers (seed_key, seed_version, created_at)
                values (?, ?, current_timestamp(6))
                """, DemoSeedService.SEED_KEY, "OTHER-VERSION"));
        assertEquals(1, count("demo_seed_markers"));
    }

    @Test
    void rerunKeepsExactCountsAndOpeningHours() {
        seedService.seed();
        Map<String, Integer> initialCounts = counts();

        seedService.seed();

        assertEquals(initialCounts, counts());
        assertEquals(7, count("horarios_atencion"));
        assertEquals(7, count("horarios_atencion", "restaurante_id = (select id from restaurantes where razon_social = ?)",
                "Preview Kitchen"));
    }

    @Test
    void partialLossRepairsMissingRowsWithoutChangingSurvivingIdentifiers() {
        seedService.seed();
        long clientId = id("usuarios", "email", DemoSeedService.CLIENT_EMAIL);
        long restaurantId = id("restaurantes", "razon_social", "Preview Kitchen");
        long menuId = id("menus", "nombre", "Preview Menu");
        long burgerId = productId("Preview Burger");
        long friesId = productId("Preview Fries");
        long orderId = id("pedidos", "pin", "PREVIEW-SEED-CASH-ORDER");

        jdbcTemplate.update("delete from productos_pedidos where pedido_id = ? and producto_id = ?", orderId, friesId);
        jdbcTemplate.update("delete from productos where id = ?", friesId);
        jdbcTemplate.update("delete from pagos where pedido_id = ?", orderId);
        jdbcTemplate.update("delete from horarios_atencion where restaurante_id = ? and dia = 'MONDAY'", restaurantId);

        seedService.seed();

        assertEquals(3, count("productos"));
        assertEquals(3, count("productos_pedidos"));
        assertEquals(1, count("pagos"));
        assertEquals(7, count("horarios_atencion"));
        assertEquals(clientId, id("usuarios", "email", DemoSeedService.CLIENT_EMAIL));
        assertEquals(restaurantId, id("restaurantes", "razon_social", "Preview Kitchen"));
        assertEquals(menuId, id("menus", "nombre", "Preview Menu"));
        assertEquals(burgerId, productId("Preview Burger"));
        assertTrue(productId("Preview Fries") > 0);
    }

    @Test
    void openingHourConflictIsRejectedWithoutRepairingTheConflictingRow() {
        seedService.seed();
        long restaurantId = id("restaurantes", "razon_social", "Preview Kitchen");
        jdbcTemplate.update("""
                update horarios_atencion
                set hora_cierre = '18:00:00'
                where restaurante_id = ? and dia = 'MONDAY'
                """, restaurantId);

        DemoSeedConflictException failure = assertThrows(
                DemoSeedConflictException.class, seedService::seed);

        assertTrue(failure.getMessage().contains("opening hour natural key 'Preview Kitchen/MONDAY'"));
        assertEquals("18:00:00", jdbcTemplate.queryForObject("""
                select time_format(hora_cierre, '%H:%i:%s')
                from horarios_atencion
                where restaurante_id = ? and dia = 'MONDAY'
                """, String.class, restaurantId));
    }

    @Test
    void conflictRollsBackMarkerAndRepairsWhenTheSeedStartedWithoutAMarker() {
        seedService.seed();
        long orderId = id("pedidos", "pin", "PREVIEW-SEED-CASH-ORDER");
        jdbcTemplate.update("delete from demo_seed_markers where seed_key = ?", DemoSeedService.SEED_KEY);
        jdbcTemplate.update("delete from pagos where pedido_id = ?", orderId);
        jdbcTemplate.update("update productos set precio = '9999.00' where nombre = 'Preview Burger'");

        assertThrows(DemoSeedConflictException.class, seedService::seed);

        assertEquals(0, count("demo_seed_markers"));
        assertEquals(0, count("pagos"));
        assertEquals(new BigDecimal("9999.00"), jdbcTemplate.queryForObject(
                "select precio from productos where nombre = 'Preview Burger'", BigDecimal.class));
    }

    @Test
    void orphanNaturalKeyMenuIsRejectedWithoutCreatingAnotherMenuOrRestaurant() {
        jdbcTemplate.update("insert into menus (nombre) values (?)", "Preview Menu");

        DemoSeedConflictException failure = assertThrows(
                DemoSeedConflictException.class, seedService::seed);

        assertTrue(failure.getMessage().contains("without a provable restaurant owner"));
        assertEquals(1, count("menus"));
        assertEquals(0, count("restaurantes"));
        assertEquals(0, count("demo_seed_markers"));
    }

    @Test
    void twoConcurrentSeedCallsCommitOneCoherentDataset() throws Exception {
        CyclicBarrier barrier = new CyclicBarrier(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<Throwable> first = executor.submit(() -> seedAfterBarrier(barrier));
            Future<Throwable> second = executor.submit(() -> seedAfterBarrier(barrier));

            assertNull(first.get(45, TimeUnit.SECONDS));
            assertNull(second.get(45, TimeUnit.SECONDS));
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
        }

        assertEquals(1, count("demo_seed_markers"));
        assertEquals(3, count("usuarios"));
        assertEquals(7, count("horarios_atencion"));
        assertEquals(1, count("restaurantes"));
        assertEquals(1, count("menus"));
        assertEquals(3, count("productos"));
        assertEquals(1, count("pedidos"));
        assertEquals(3, count("productos_pedidos"));
        assertEquals(1, count("pagos"));
    }

    private Throwable seedAfterBarrier(CyclicBarrier barrier) {
        try {
            barrier.await(30, TimeUnit.SECONDS);
            seedService.seed();
            return null;
        } catch (Throwable failure) {
            return failure;
        }
    }

    private void assertCredentialsMatchStoredBcryptHashes() {
        Map<String, String> hashes = jdbcTemplate.query("select email, password from usuarios", resultSet -> {
            Map<String, String> values = new LinkedHashMap<>();
            while (resultSet.next()) {
                values.put(resultSet.getString("email"), resultSet.getString("password"));
            }
            return values;
        });
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        assertEquals(3, hashes.size());
        assertTrue(encoder.matches(DemoSeedService.CLIENT_PASSWORD, hashes.get(DemoSeedService.CLIENT_EMAIL)));
        assertTrue(encoder.matches(DemoSeedService.OWNER_PASSWORD, hashes.get(DemoSeedService.OWNER_EMAIL)));
        assertTrue(encoder.matches(DemoSeedService.COURIER_PASSWORD, hashes.get(DemoSeedService.COURIER_EMAIL)));
        assertFalse(hashes.values().stream().anyMatch(hash -> hash == null || !hash.startsWith("$2")));
    }

    private Map<String, Integer> counts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String table : List.of(
                "roles",
                "usuarios",
                "direcciones",
                "horarios_atencion",
                "imagenes",
                "restaurantes",
                "menus",
                "productos",
                "pedidos",
                "productos_pedidos",
                "pagos",
                "demo_seed_markers")) {
            counts.put(table, count(table));
        }
        return counts;
    }

    private int count(String table) {
        return jdbcTemplate.queryForObject("select count(*) from " + table, Integer.class);
    }

    private int count(String table, String predicate, Object... arguments) {
        return jdbcTemplate.queryForObject("select count(*) from " + table + " where " + predicate,
                Integer.class, arguments);
    }

    private long id(String table, String column, String value) {
        return jdbcTemplate.queryForObject("select id from " + table + " where " + column + " = ?",
                Long.class, value);
    }

    private long productId(String name) {
        return id("productos", "nombre", name);
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class SeedConfiguration {
        @Bean
        DemoSeedService demoSeedService(
                JdbcTemplate jdbcTemplate,
                RolRepository rolRepository,
                UsuarioRepository usuarioRepository,
                DireccionRepository direccionRepository,
                HorarioRepository horarioRepository,
                ImagenRepository imagenRepository,
                RestauranteRepository restauranteRepository,
                MenuRepository menuRepository,
                ProductoRepository productoRepository,
                PedidoRepository pedidoRepository,
                ProductoPedidoRepository productoPedidoRepository,
                PagoRepository pagoRepository) {
            return new DemoSeedService(
                    jdbcTemplate,
                    new BCryptPasswordEncoder(),
                    rolRepository,
                    usuarioRepository,
                    direccionRepository,
                    horarioRepository,
                    imagenRepository,
                    restauranteRepository,
                    menuRepository,
                    productoRepository,
                    pedidoRepository,
                    productoPedidoRepository,
                    pagoRepository);
        }
    }
}
