package utn.back.mordiscoapi.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import utn.back.mordiscoapi.enums.EstadoPago;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.enums.MetodoPago;
import utn.back.mordiscoapi.enums.TipoEntrega;
import utn.back.mordiscoapi.model.entity.Direccion;
import utn.back.mordiscoapi.model.entity.HorarioAtencion;
import utn.back.mordiscoapi.model.entity.Imagen;
import utn.back.mordiscoapi.model.entity.Menu;
import utn.back.mordiscoapi.model.entity.Pago;
import utn.back.mordiscoapi.model.entity.Pedido;
import utn.back.mordiscoapi.model.entity.Producto;
import utn.back.mordiscoapi.model.entity.ProductoPedido;
import utn.back.mordiscoapi.model.entity.Restaurante;
import utn.back.mordiscoapi.model.entity.Rol;
import utn.back.mordiscoapi.model.entity.Usuario;
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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DemoSeedServiceTest {

    @Mock private JdbcTemplate jdbcTemplate;
    @Spy private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Mock private RolRepository rolRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private DireccionRepository direccionRepository;
    @Mock private HorarioRepository horarioRepository;
    @Mock private ImagenRepository imagenRepository;
    @Mock private RestauranteRepository restauranteRepository;
    @Mock private MenuRepository menuRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private PedidoRepository pedidoRepository;
    @Mock private ProductoPedidoRepository productoPedidoRepository;
    @Mock private PagoRepository pagoRepository;

    private final List<Rol> roles = new ArrayList<>();
    private final List<Usuario> users = new ArrayList<>();
    private final List<Direccion> addresses = new ArrayList<>();
    private final List<HorarioAtencion> openingHours = new ArrayList<>();
    private final List<Imagen> images = new ArrayList<>();
    private final List<Restaurante> restaurants = new ArrayList<>();
    private final List<Menu> menus = new ArrayList<>();
    private final List<Producto> products = new ArrayList<>();
    private final List<Pedido> orders = new ArrayList<>();
    private final List<ProductoPedido> orderItems = new ArrayList<>();
    private final List<Pago> payments = new ArrayList<>();

    private AtomicLong roleIds;
    private AtomicLong userIds;
    private AtomicLong addressIds;
    private AtomicLong openingHourIds;
    private AtomicLong imageIds;
    private AtomicLong restaurantIds;
    private AtomicLong menuIds;
    private AtomicLong productIds;
    private AtomicLong orderIds;
    private AtomicLong itemIds;
    private AtomicLong paymentIds;
    private DemoSeedService service;

    @BeforeEach
    void setUp() {
        roleIds = new AtomicLong(1);
        userIds = new AtomicLong(1);
        addressIds = new AtomicLong(1);
        openingHourIds = new AtomicLong(1);
        imageIds = new AtomicLong(1);
        restaurantIds = new AtomicLong(1);
        menuIds = new AtomicLong(1);
        productIds = new AtomicLong(1);
        orderIds = new AtomicLong(1);
        itemIds = new AtomicLong(1);
        paymentIds = new AtomicLong(1);

        when(rolRepository.findAll()).thenAnswer(invocation -> roles);
        when(usuarioRepository.findAll()).thenAnswer(invocation -> users);
        when(direccionRepository.findAll()).thenAnswer(invocation -> addresses);
        when(horarioRepository.findAllByRestauranteId(any(Long.class)))
                .thenAnswer(invocation -> openingHours.stream()
                        .filter(hour -> hour.getRestaurante() != null
                                && hour.getRestaurante().getId().equals(invocation.getArgument(0)))
                        .toList());
        when(imagenRepository.findAll()).thenAnswer(invocation -> images);
        when(restauranteRepository.findAll()).thenAnswer(invocation -> restaurants);
        when(menuRepository.findAll()).thenAnswer(invocation -> menus);
        when(productoRepository.findAll()).thenAnswer(invocation -> products);
        when(pedidoRepository.findAll()).thenAnswer(invocation -> orders);
        when(productoPedidoRepository.findAll()).thenAnswer(invocation -> orderItems);
        when(pagoRepository.findAll()).thenAnswer(invocation -> payments);

        when(jdbcTemplate.queryForObject(
                eq(DemoSeedService.LOCK_MARKER_SQL), eq(String.class), eq(DemoSeedService.SEED_KEY)))
                .thenReturn(DemoSeedService.SEED_VERSION);

        when(rolRepository.saveAndFlush(any(Rol.class))).thenAnswer(invocation -> {
            Rol value = invocation.getArgument(0);
            value.setId(roleIds.getAndIncrement());
            roles.add(value);
            return value;
        });
        when(usuarioRepository.saveAndFlush(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario value = invocation.getArgument(0);
            value.setId(userIds.getAndIncrement());
            users.add(value);
            return value;
        });
        when(direccionRepository.saveAndFlush(any(Direccion.class))).thenAnswer(invocation -> {
            Direccion value = invocation.getArgument(0);
            value.setId(addressIds.getAndIncrement());
            addresses.add(value);
            return value;
        });
        when(horarioRepository.saveAndFlush(any(HorarioAtencion.class))).thenAnswer(invocation -> {
            HorarioAtencion value = invocation.getArgument(0);
            value.setId(openingHourIds.getAndIncrement());
            openingHours.add(value);
            return value;
        });
        when(imagenRepository.saveAndFlush(any(Imagen.class))).thenAnswer(invocation -> {
            Imagen value = invocation.getArgument(0);
            value.setId(imageIds.getAndIncrement());
            images.add(value);
            return value;
        });
        when(restauranteRepository.saveAndFlush(any(Restaurante.class))).thenAnswer(invocation -> {
            Restaurante value = invocation.getArgument(0);
            if (value.getId() == null) {
                value.setId(restaurantIds.getAndIncrement());
                restaurants.add(value);
            }
            return value;
        });
        when(menuRepository.saveAndFlush(any(Menu.class))).thenAnswer(invocation -> {
            Menu value = invocation.getArgument(0);
            if (value.getId() == null) {
                value.setId(menuIds.getAndIncrement());
                menus.add(value);
            }
            return value;
        });
        when(productoRepository.saveAndFlush(any(Producto.class))).thenAnswer(invocation -> {
            Producto value = invocation.getArgument(0);
            value.setId(productIds.getAndIncrement());
            products.add(value);
            return value;
        });
        when(pedidoRepository.saveAndFlush(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido value = invocation.getArgument(0);
            if (value.getId() == null) {
                value.setId(orderIds.getAndIncrement());
                orders.add(value);
            }
            return value;
        });
        when(productoPedidoRepository.save(any(ProductoPedido.class))).thenAnswer(invocation -> {
            ProductoPedido value = invocation.getArgument(0);
            value.setId(itemIds.getAndIncrement());
            orderItems.add(value);
            return value;
        });
        when(pagoRepository.saveAndFlush(any(Pago.class))).thenAnswer(invocation -> {
            Pago value = invocation.getArgument(0);
            value.setId(paymentIds.getAndIncrement());
            payments.add(value);
            return value;
        });

        service = new DemoSeedService(
                jdbcTemplate,
                passwordEncoder,
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

    @Test
    void firstRunCreatesOnlyTheSyntheticCoherentScenario() {
        service.seed();

        assertEquals(List.of("ROLE_CLIENTE", "ROLE_RESTAURANTE", "ROLE_REPARTIDOR"),
                roles.stream().map(Rol::getNombre).toList());
        assertEquals(3, users.size());
        assertTrue(users.stream().allMatch(user -> user.getEmail().endsWith(".invalid")));
        assertTrue(users.stream().noneMatch(user -> "ROLE_ADMIN".equals(user.getRol().getNombre())));
        assertEquals(2, addresses.size());
        assertEquals(7, openingHours.size());
        assertEquals(DemoSeedService.PREVIEW_OPENING_HOURS.stream().map(DemoSeedService.OpeningHourSpec::day).toList(),
                openingHours.stream().map(HorarioAtencion::getDia).toList());
        assertEquals(List.of(
                        LocalTime.MIDNIGHT, LocalTime.MIDNIGHT, LocalTime.MIDNIGHT,
                        LocalTime.MIDNIGHT, LocalTime.MIDNIGHT, LocalTime.MIDNIGHT, LocalTime.MIDNIGHT),
                openingHours.stream().map(HorarioAtencion::getHoraApertura).toList());
        assertEquals(List.of(
                        LocalTime.of(0, 1), LocalTime.of(0, 1), LocalTime.of(0, 1),
                        LocalTime.of(0, 1), LocalTime.of(0, 1), LocalTime.of(0, 1), LocalTime.of(0, 1)),
                openingHours.stream().map(HorarioAtencion::getHoraCierre).toList());
        assertTrue(openingHours.stream().allMatch(hour -> Boolean.TRUE.equals(hour.getCruzaMedianoche())));
        assertEquals(1, restaurants.size());
        assertEquals("Preview Kitchen", restaurants.getFirst().getRazonSocial());
        assertEquals(1, menus.size());
        assertEquals(3, products.size());
        assertEquals(4, images.size());
        assertEquals(1, orders.size());
        assertEquals(TipoEntrega.RETIRO_POR_LOCAL, orders.getFirst().getTipoEntrega());
        assertEquals(EstadoPedido.PENDIENTE, orders.getFirst().getEstado());
        assertEquals(3, orderItems.size());
        assertEquals(1, payments.size());
        assertEquals(MetodoPago.EFECTIVO, payments.getFirst().getMetodoPago());
        assertEquals(EstadoPago.PENDIENTE, payments.getFirst().getEstado());
        assertEquals(new BigDecimal("13500.00"), payments.getFirst().getMonto());
        assertTrue(payments.getFirst().getMercadoPagoPaymentId() == null);
        assertTrue(passwordEncoder.matches(DemoSeedService.CLIENT_PASSWORD,
                user(DemoSeedService.CLIENT_EMAIL).getPassword()));
        assertTrue(passwordEncoder.matches(DemoSeedService.OWNER_PASSWORD,
                user(DemoSeedService.OWNER_EMAIL).getPassword()));
        assertTrue(passwordEncoder.matches(DemoSeedService.COURIER_PASSWORD,
                user(DemoSeedService.COURIER_EMAIL).getPassword()));
    }

    @Test
    void rerunReusesEverySeedOwnedNaturalKeyWithoutAddingRows() {
        service.seed();
        List<Integer> counts = counts();

        service.seed();

        assertEquals(counts, counts());
        assertEquals(3, users.stream()
                .filter(user -> DemoSeedService.CLIENT_EMAIL.equals(user.getEmail())
                        || DemoSeedService.OWNER_EMAIL.equals(user.getEmail())
                        || DemoSeedService.COURIER_EMAIL.equals(user.getEmail()))
                .count());
        verify(jdbcTemplate, org.mockito.Mockito.times(2))
                .update(DemoSeedService.UPSERT_MARKER_SQL,
                        DemoSeedService.SEED_KEY, DemoSeedService.SEED_VERSION);
    }

    @Test
    void partialSeedLossIsRepairedWithoutChangingSurvivingRows() {
        service.seed();
        Producto removedProduct = products.stream()
                .filter(product -> "Preview Fries".equals(product.getNombre()))
                .findFirst()
                .orElseThrow();
        products.remove(removedProduct);
        orderItems.removeIf(item -> item.getProducto() == removedProduct);
        payments.clear();
        addresses.removeIf(address -> "preview-client-home".equals(address.getAlias()));
        openingHours.removeIf(hour -> java.time.DayOfWeek.MONDAY.equals(hour.getDia()));

        service.seed();

        assertEquals(3, products.size());
        assertEquals(3, orderItems.size());
        assertEquals(1, payments.size());
        assertEquals(2, addresses.size());
        assertEquals(7, openingHours.size());
        assertTrue(products.stream().anyMatch(product -> "Preview Fries".equals(product.getNombre())));
    }

    @Test
    void openingHourConflictFailsWithoutOverwritingTheExistingRow() {
        service.seed();
        HorarioAtencion conflicting = openingHours.stream()
                .filter(hour -> java.time.DayOfWeek.MONDAY.equals(hour.getDia()))
                .findFirst()
                .orElseThrow();
        conflicting.setHoraCierre(java.time.LocalTime.of(18, 0));

        DemoSeedConflictException failure = assertThrows(
                DemoSeedConflictException.class, () -> service.seed());

        assertTrue(failure.getMessage().contains("opening hour natural key 'Preview Kitchen/MONDAY'"));
        assertEquals(java.time.LocalTime.of(18, 0), conflicting.getHoraCierre());
    }

    @Test
    void orphanPreviewMenuIsRefusedInsteadOfDuplicated() {
        Menu orphan = Menu.builder()
                .id(menuIds.getAndIncrement())
                .nombre("Preview Menu")
                .productos(new ArrayList<>())
                .restaurante(null)
                .build();
        menus.add(orphan);

        DemoSeedConflictException failure = assertThrows(
                DemoSeedConflictException.class, () -> service.seed());

        assertTrue(failure.getMessage().contains("without a provable restaurant owner"));
        assertEquals(1, menus.size());
        assertEquals(orphan, menus.getFirst());
    }

    @Test
    void conflictingNaturalKeyFailsWithoutOverwritingTheExistingRow() {
        service.seed();
        Producto conflicting = products.stream()
                .filter(product -> "Preview Burger".equals(product.getNombre()))
                .findFirst()
                .orElseThrow();
        conflicting.setPrecio(new BigDecimal("9999.00"));

        DemoSeedConflictException failure = assertThrows(
                DemoSeedConflictException.class, () -> service.seed());

        assertTrue(failure.getMessage().contains("product natural key 'Preview Burger'"));
        assertEquals(new BigDecimal("9999.00"), conflicting.getPrecio());
    }

    @Test
    void failedSeedHasTransactionalBoundaryAndDoesNotDeclareASuccessfulMarker() throws Exception {
        Method seed = DemoSeedService.class.getMethod("seed");
        assertNotNull(seed.getAnnotation(Transactional.class));

        when(jdbcTemplate.queryForObject(
                eq(DemoSeedService.LOCK_MARKER_SQL), eq(String.class), eq(DemoSeedService.SEED_KEY)))
                .thenReturn("OTHER-VERSION");

        assertThrows(DemoSeedConflictException.class, () -> service.seed());
        verify(rolRepository, org.mockito.Mockito.never()).saveAndFlush(any(Rol.class));
        verify(usuarioRepository, org.mockito.Mockito.never()).saveAndFlush(any(Usuario.class));
        verify(jdbcTemplate).update(DemoSeedService.UPSERT_MARKER_SQL,
                DemoSeedService.SEED_KEY, DemoSeedService.SEED_VERSION);
        assertTrue(DemoSeedService.UPSERT_MARKER_SQL.contains("ON DUPLICATE KEY UPDATE"));
    }

    @Test
    void duplicateMarkerUsesDatabaseUpsertAndRowLockInsteadOfJvmLock() {
        assertTrue(DemoSeedService.CREATE_MARKER_TABLE_SQL.contains("UNIQUE KEY uk_demo_seed_markers_seed_key (seed_key)"));
        assertTrue(DemoSeedService.UPSERT_MARKER_SQL.contains("demo_seed_markers (seed_key, seed_version, created_at)"));
        assertTrue(DemoSeedService.UPSERT_MARKER_SQL.contains("ON DUPLICATE KEY UPDATE"));
        assertTrue(DemoSeedService.LOCK_MARKER_SQL.contains("FOR UPDATE"));
        assertTrue(Arrays.stream(DemoSeedService.class.getDeclaredMethods())
                .noneMatch(method -> Modifier.isSynchronized(method.getModifiers())));

        service.seed();
        service.seed();

        InOrder markerCalls = inOrder(jdbcTemplate);
        markerCalls.verify(jdbcTemplate).execute(DemoSeedService.CREATE_MARKER_TABLE_SQL);
        markerCalls.verify(jdbcTemplate).update(DemoSeedService.UPSERT_MARKER_SQL,
                DemoSeedService.SEED_KEY, DemoSeedService.SEED_VERSION);
        markerCalls.verify(jdbcTemplate).queryForObject(
                eq(DemoSeedService.LOCK_MARKER_SQL), eq(String.class), eq(DemoSeedService.SEED_KEY));
    }

    private Usuario user(String email) {
        return users.stream()
                .filter(candidate -> email.equals(candidate.getEmail()))
                .findFirst()
                .orElseThrow();
    }

    private List<Integer> counts() {
        return Arrays.asList(
                roles.size(), users.size(), addresses.size(), openingHours.size(), images.size(),
                restaurants.size(), menus.size(), products.size(), orders.size(), orderItems.size(), payments.size());
    }
}
