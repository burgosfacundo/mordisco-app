package utn.back.mordiscoapi.demo;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
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

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Seeds the small, synthetic Preview scenario without going through application
 * services. Application services publish events and may call external providers;
 * this service deliberately only uses the database repositories and marker SQL.
 */
@Service
@Profile("demo-seed")
public class DemoSeedService {

    static final String SEED_KEY = "mordisco.vercel.preview.seed";
    static final String SEED_VERSION = "SEED-2";

    static final String CREATE_MARKER_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS demo_seed_markers (
                seed_key VARCHAR(128) NOT NULL,
                seed_version VARCHAR(32) NOT NULL,
                created_at DATETIME(6) NOT NULL,
                UNIQUE KEY uk_demo_seed_markers_seed_key (seed_key)
            )
            """;

    static final String UPSERT_MARKER_SQL = """
            INSERT INTO demo_seed_markers (seed_key, seed_version, created_at)
            VALUES (?, ?, CURRENT_TIMESTAMP(6))
            ON DUPLICATE KEY UPDATE seed_key = VALUES(seed_key)
            """;

    static final String LOCK_MARKER_SQL = """
            SELECT seed_version
            FROM demo_seed_markers
            WHERE seed_key = ?
            FOR UPDATE
            """;

    public static final String CLIENT_EMAIL = "preview.client@mordisco.invalid";
    public static final String CLIENT_PASSWORD = "PreviewOnly-Client-2025!";
    public static final String OWNER_EMAIL = "preview.owner@mordisco.invalid";
    public static final String OWNER_PASSWORD = "PreviewOnly-Owner-2025!";
    public static final String COURIER_EMAIL = "preview.courier@mordisco.invalid";
    public static final String COURIER_PASSWORD = "PreviewOnly-Courier-2025!";

    private static final String CLIENT_PHONE = "+5491100000201";
    private static final String OWNER_PHONE = "+5491100000202";
    private static final String COURIER_PHONE = "+5491100000203";

    private static final LocalDateTime ORDER_DATE = LocalDateTime.of(2025, 1, 15, 12, 0);
    private static final String ORDER_PIN = "PREVIEW-SEED-CASH-ORDER";
    private static final BigDecimal BURGER_PRICE = new BigDecimal("8500.00");
    private static final BigDecimal FRIES_PRICE = new BigDecimal("3200.00");
    private static final BigDecimal LEMONADE_PRICE = new BigDecimal("1800.00");
    private static final BigDecimal ORDER_TOTAL = new BigDecimal("13500.00");

    private static final UserSpec CLIENT = new UserSpec(
            "Preview", "Client", CLIENT_PHONE, CLIENT_EMAIL, CLIENT_PASSWORD,
            "ROLE_CLIENTE", -37.9971, -57.5484);
    private static final UserSpec OWNER = new UserSpec(
            "Preview", "Restaurant Owner", OWNER_PHONE, OWNER_EMAIL, OWNER_PASSWORD,
            "ROLE_RESTAURANTE", -37.9982, -57.5491);
    private static final UserSpec COURIER = new UserSpec(
            "Preview", "Courier", COURIER_PHONE, COURIER_EMAIL, COURIER_PASSWORD,
            "ROLE_REPARTIDOR", -37.9964, -57.5477);

    private static final AddressSpec CLIENT_ADDRESS = new AddressSpec(
            "preview-client-home", "Preview Avenida", "100", null, null,
            "7600", "Synthetic Preview client address", -37.9971, -57.5484, "Mar del Plata");
    private static final AddressSpec RESTAURANT_ADDRESS = new AddressSpec(
            "preview-restaurant-location", "Preview Calle", "200", null, null,
            "7600", "Synthetic Preview restaurant address", -37.9982, -57.5491, "Mar del Plata");

    /**
     * Keep the recruiter preview open on every calendar day without consulting a
     * clock-backed or external availability service. The frontend's cross-midnight
     * check treats midnight through one minute after midnight as open all day.
     */
    static final List<OpeningHourSpec> PREVIEW_OPENING_HOURS = List.of(
            new OpeningHourSpec(DayOfWeek.MONDAY, LocalTime.MIDNIGHT, LocalTime.of(0, 1), true),
            new OpeningHourSpec(DayOfWeek.TUESDAY, LocalTime.MIDNIGHT, LocalTime.of(0, 1), true),
            new OpeningHourSpec(DayOfWeek.WEDNESDAY, LocalTime.MIDNIGHT, LocalTime.of(0, 1), true),
            new OpeningHourSpec(DayOfWeek.THURSDAY, LocalTime.MIDNIGHT, LocalTime.of(0, 1), true),
            new OpeningHourSpec(DayOfWeek.FRIDAY, LocalTime.MIDNIGHT, LocalTime.of(0, 1), true),
            new OpeningHourSpec(DayOfWeek.SATURDAY, LocalTime.MIDNIGHT, LocalTime.of(0, 1), true),
            new OpeningHourSpec(DayOfWeek.SUNDAY, LocalTime.MIDNIGHT, LocalTime.of(0, 1), true));

    private static final ProductSpec BURGER = new ProductSpec(
            "Preview Burger", "Synthetic preview burger", BURGER_PRICE,
            "/assets/demo-seed/preview-burger.svg");
    private static final ProductSpec FRIES = new ProductSpec(
            "Preview Fries", "Synthetic preview fries", FRIES_PRICE,
            "/assets/demo-seed/preview-fries.svg");
    private static final ProductSpec LEMONADE = new ProductSpec(
            "Preview Lemonade", "Synthetic preview lemonade", LEMONADE_PRICE,
            "/assets/demo-seed/preview-lemonade.svg");

    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final DireccionRepository direccionRepository;
    private final HorarioRepository horarioRepository;
    private final ImagenRepository imagenRepository;
    private final RestauranteRepository restauranteRepository;
    private final MenuRepository menuRepository;
    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;
    private final ProductoPedidoRepository productoPedidoRepository;
    private final PagoRepository pagoRepository;

    public DemoSeedService(
            JdbcTemplate jdbcTemplate,
            BCryptPasswordEncoder passwordEncoder,
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
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.direccionRepository = direccionRepository;
        this.horarioRepository = horarioRepository;
        this.imagenRepository = imagenRepository;
        this.restauranteRepository = restauranteRepository;
        this.menuRepository = menuRepository;
        this.productoRepository = productoRepository;
        this.pedidoRepository = pedidoRepository;
        this.productoPedidoRepository = productoPedidoRepository;
        this.pagoRepository = pagoRepository;
    }

    /**
     * The marker upsert and every dataset mutation share this transaction. The
     * marker is inserted before any dataset row, so a rollback removes it too.
     * The database primary key and FOR UPDATE, not a JVM lock, serialize starts.
     */
    @Transactional
    public void seed() {
        lockSeedMarker();

        Map<String, Rol> roles = ensureRoles();
        Usuario client = ensureUser(CLIENT, roles.get(CLIENT.role()));
        Usuario owner = ensureUser(OWNER, roles.get(OWNER.role()));
        // The courier is useful for Preview login, but the cash pickup order deliberately
        // has no courier assignment or delivery transition to avoid synthetic dispatch state.
        ensureUser(COURIER, roles.get(COURIER.role()));

        ensureAddress(client, CLIENT_ADDRESS);
        Direccion restaurantAddress = ensureAddress(owner, RESTAURANT_ADDRESS);
        Imagen restaurantImage = ensureImage("preview-restaurant", "/assets/demo-seed/preview-restaurant.svg");
        Restaurante restaurant = ensureRestaurant(owner, restaurantAddress, restaurantImage);
        ensureOpeningHours(restaurant);
        Menu menu = ensureMenu(restaurant);

        List<Producto> products = List.of(
                ensureProduct(menu, BURGER),
                ensureProduct(menu, FRIES),
                ensureProduct(menu, LEMONADE));

        ensureCashOrder(client, restaurant, restaurantAddress, products);
    }

    private void lockSeedMarker() {
        // This DDL is intentionally the first database operation. MySQL/TiDB DDL
        // may commit its own metadata transaction, but no seed mutation exists yet.
        jdbcTemplate.execute(CREATE_MARKER_TABLE_SQL);
        jdbcTemplate.update(UPSERT_MARKER_SQL, SEED_KEY, SEED_VERSION);
        String version = jdbcTemplate.queryForObject(LOCK_MARKER_SQL, String.class, SEED_KEY);
        if (!SEED_VERSION.equals(version)) {
            throw new DemoSeedConflictException(
                    "marker '" + SEED_KEY + "' has version '" + version
                            + "' instead of '" + SEED_VERSION + "'");
        }
    }

    private Map<String, Rol> ensureRoles() {
        Map<String, Rol> roles = new HashMap<>();
        for (String roleName : List.of("ROLE_CLIENTE", "ROLE_RESTAURANTE", "ROLE_REPARTIDOR")) {
            roles.put(roleName, ensureRole(roleName));
        }
        return roles;
    }

    private Rol ensureRole(String roleName) {
        List<Rol> matches = rolRepository.findAll().stream()
                .filter(role -> roleName.equals(role.getNombre()))
                .toList();
        if (matches.size() > 1) {
            throw conflict("role", roleName, "more than one row matches");
        }
        if (matches.size() == 1) {
            return matches.getFirst();
        }
        return rolRepository.saveAndFlush(Rol.builder().nombre(roleName).build());
    }

    private Usuario ensureUser(UserSpec spec, Rol role) {
        List<Usuario> users = usuarioRepository.findAll();
        List<Usuario> emailMatches = users.stream()
                .filter(user -> spec.email().equals(user.getEmail()))
                .toList();
        if (emailMatches.size() > 1) {
            throw conflict("user", spec.email(), "more than one row matches the email");
        }
        if (emailMatches.size() == 1) {
            Usuario existing = emailMatches.getFirst();
            if (!matchesUser(existing, spec, role)) {
                throw conflict("user", spec.email(), "the existing row does not match the seed-owned values");
            }
            users.stream()
                    .filter(user -> spec.phone().equals(user.getTelefono()) && !sameId(user, existing))
                    .findAny()
                    .ifPresent(user -> { throw conflict("user", spec.phone(), "the phone belongs to another row"); });
            return existing;
        }

        List<Usuario> phoneMatches = users.stream()
                .filter(user -> spec.phone().equals(user.getTelefono()))
                .toList();
        if (!phoneMatches.isEmpty()) {
            throw conflict("user", spec.phone(), "the phone is already owned by a different natural key");
        }

        return usuarioRepository.saveAndFlush(Usuario.builder()
                .nombre(spec.firstName())
                .apellido(spec.lastName())
                .telefono(spec.phone())
                .email(spec.email())
                .password(passwordEncoder.encode(spec.password()))
                .latitudActual(spec.latitude())
                .longitudActual(spec.longitude())
                .bajaLogica(false)
                .direcciones(new ArrayList<>())
                .rol(role)
                .build());
    }

    private boolean matchesUser(Usuario user, UserSpec spec, Rol role) {
        return Objects.equals(spec.firstName(), user.getNombre())
                && Objects.equals(spec.lastName(), user.getApellido())
                && Objects.equals(spec.phone(), user.getTelefono())
                && Objects.equals(spec.email(), user.getEmail())
                && Objects.equals(spec.latitude(), user.getLatitudActual())
                && Objects.equals(spec.longitude(), user.getLongitudActual())
                && Boolean.FALSE.equals(user.getBajaLogica())
                && user.getMotivoBaja() == null
                && user.getFechaBaja() == null
                && user.getRol() != null
                && sameId(user.getRol(), role)
                && user.getPassword() != null
                && passwordEncoder.matches(spec.password(), user.getPassword());
    }

    private Direccion ensureAddress(Usuario user, AddressSpec spec) {
        List<Direccion> matches = direccionRepository.findAll().stream()
                .filter(address -> sameId(address.getUsuario(), user)
                        && spec.alias().equals(address.getAlias()))
                .toList();
        if (matches.size() > 1) {
            throw conflict("address", spec.alias(), "more than one row matches the owner and alias");
        }
        if (matches.size() == 1) {
            Direccion existing = matches.getFirst();
            if (!matchesAddress(existing, spec, user)) {
                throw conflict("address", spec.alias(), "the existing row does not match the seed-owned values");
            }
            return existing;
        }

        return direccionRepository.saveAndFlush(Direccion.builder()
                .calle(spec.street())
                .numero(spec.number())
                .piso(spec.floor())
                .depto(spec.apartment())
                .codigoPostal(spec.postalCode())
                .referencias(spec.references())
                .alias(spec.alias())
                .latitud(spec.latitude())
                .longitud(spec.longitude())
                .ciudad(spec.city())
                .usuario(user)
                .build());
    }

    private boolean matchesAddress(Direccion address, AddressSpec spec, Usuario user) {
        return sameId(address.getUsuario(), user)
                && Objects.equals(spec.street(), address.getCalle())
                && Objects.equals(spec.number(), address.getNumero())
                && Objects.equals(spec.floor(), address.getPiso())
                && Objects.equals(spec.apartment(), address.getDepto())
                && Objects.equals(spec.postalCode(), address.getCodigoPostal())
                && Objects.equals(spec.references(), address.getReferencias())
                && Objects.equals(spec.alias(), address.getAlias())
                && Objects.equals(spec.latitude(), address.getLatitud())
                && Objects.equals(spec.longitude(), address.getLongitud())
                && Objects.equals(spec.city(), address.getCiudad());
    }

    private Imagen ensureImage(String name, String url) {
        List<Imagen> matches = imagenRepository.findAll().stream()
                .filter(image -> url.equals(image.getUrl()))
                .toList();
        if (matches.size() > 1) {
            throw conflict("image", url, "more than one row matches the URL");
        }
        if (matches.size() == 1) {
            Imagen existing = matches.getFirst();
            if (!name.equals(existing.getNombre())) {
                throw conflict("image", url, "the existing row has a different name");
            }
            return existing;
        }
        return imagenRepository.saveAndFlush(Imagen.builder().nombre(name).url(url).build());
    }

    private Restaurante ensureRestaurant(Usuario owner, Direccion address, Imagen image) {
        String naturalKey = "Preview Kitchen";
        List<Restaurante> byName = restauranteRepository.findAll().stream()
                .filter(restaurant -> naturalKey.equals(restaurant.getRazonSocial()))
                .toList();
        if (byName.size() > 1) {
            throw conflict("restaurant", naturalKey, "more than one row matches the business name");
        }

        List<Restaurante> byOwner = restauranteRepository.findAll().stream()
                .filter(restaurant -> sameId(restaurant.getUsuario(), owner))
                .toList();
        if (byOwner.size() > 1) {
            throw conflict("restaurant", OWNER_EMAIL, "more than one row matches the owner");
        }

        Restaurante restaurant;
        boolean changed = false;
        if (byName.size() == 1) {
            restaurant = byName.getFirst();
            if (!sameId(restaurant.getUsuario(), owner)) {
                throw conflict("restaurant", naturalKey, "the business name belongs to another owner");
            }
            if (!Boolean.TRUE.equals(restaurant.getActivo())) {
                throw conflict("restaurant", naturalKey, "the existing restaurant is inactive");
            }
        } else {
            if (!byOwner.isEmpty()) {
                throw conflict("restaurant", OWNER_EMAIL, "the owner already has another restaurant");
            }
            restaurant = restauranteRepository.saveAndFlush(Restaurante.builder()
                    .razonSocial(naturalKey)
                    .activo(true)
                    .usuario(owner)
                    .direccion(address)
                    .imagen(image)
                    .build());
        }

        if (restaurant.getImagen() == null) {
            restaurant.setImagen(image);
            changed = true;
        } else if (!matchesImage(restaurant.getImagen(), image)) {
            throw conflict("restaurant", naturalKey, "the image is different from the seed-owned image");
        }
        if (restaurant.getDireccion() == null) {
            restaurant.setDireccion(address);
            changed = true;
        } else if (!matchesAddress(restaurant.getDireccion(), RESTAURANT_ADDRESS, owner)) {
            throw conflict("restaurant", naturalKey, "the address is different from the seed-owned address");
        }
        if (changed) {
            restaurant = restauranteRepository.saveAndFlush(restaurant);
        }
        return restaurant;
    }

    private void ensureOpeningHours(Restaurante restaurant) {
        List<HorarioAtencion> existing = horarioRepository.findAllByRestauranteId(restaurant.getId());
        for (OpeningHourSpec spec : PREVIEW_OPENING_HOURS) {
            List<HorarioAtencion> matches = existing.stream()
                    .filter(hour -> spec.day() == hour.getDia())
                    .toList();
            String naturalKey = restaurant.getRazonSocial() + "/" + spec.day();
            if (matches.size() > 1) {
                throw conflict("opening hour", naturalKey, "more than one row matches the restaurant and day");
            }
            if (matches.size() == 1) {
                if (!matchesOpeningHour(matches.getFirst(), spec, restaurant)) {
                    throw conflict("opening hour", naturalKey, "the existing row does not match the seed-owned values");
                }
                continue;
            }
            horarioRepository.saveAndFlush(HorarioAtencion.builder()
                    .dia(spec.day())
                    .horaApertura(spec.open())
                    .horaCierre(spec.close())
                    .cruzaMedianoche(spec.crossesMidnight())
                    .restaurante(restaurant)
                    .build());
        }
    }

    private boolean matchesOpeningHour(
            HorarioAtencion hour,
            OpeningHourSpec spec,
            Restaurante restaurant) {
        return sameId(hour.getRestaurante(), restaurant)
                && spec.day() == hour.getDia()
                && Objects.equals(spec.open(), hour.getHoraApertura())
                && Objects.equals(spec.close(), hour.getHoraCierre())
                && Objects.equals(spec.crossesMidnight(), hour.getCruzaMedianoche());
    }

    private Menu ensureMenu(Restaurante restaurant) {
        String naturalKey = "Preview Menu";
        List<Menu> allMenus = menuRepository.findAll();
        List<Menu> namedMenus = allMenus.stream()
                .filter(menu -> naturalKey.equals(menu.getNombre()))
                .toList();

        if (restaurant.getMenu() != null) {
            Menu linkedMenu = restaurant.getMenu();
            if (!naturalKey.equals(linkedMenu.getNombre())) {
                throw conflict("menu", naturalKey, "the restaurant already has a different menu");
            }
            if (linkedMenu.getRestaurante() != null
                    && !sameId(linkedMenu.getRestaurante(), restaurant)) {
                throw conflict("menu", naturalKey, "the linked row belongs to another restaurant");
            }
            if (namedMenus.stream().anyMatch(menu -> !sameId(menu, linkedMenu))) {
                throw conflict("menu", naturalKey, "more than one row matches the natural key");
            }
            return linkedMenu;
        }

        List<Menu> ownedMenus = allMenus.stream()
                .filter(menu -> menu.getRestaurante() != null
                        && sameId(menu.getRestaurante(), restaurant))
                .toList();
        if (ownedMenus.size() > 1) {
            throw conflict("menu", naturalKey, "more than one row matches the restaurant");
        }
        if (ownedMenus.size() == 1) {
            Menu menu = ownedMenus.getFirst();
            if (!naturalKey.equals(menu.getNombre())) {
                throw conflict("menu", naturalKey, "the existing row has a different name");
            }
            if (namedMenus.stream().anyMatch(candidate -> !sameId(candidate, menu))) {
                throw conflict("menu", naturalKey, "another natural-key row has no provable ownership");
            }
            restaurant.setMenu(menu);
            restauranteRepository.saveAndFlush(restaurant);
            return menu;
        }

        if (!namedMenus.isEmpty()) {
            throw conflict("menu", naturalKey, "a natural-key row exists without a provable restaurant owner");
        }

        Menu menu = menuRepository.saveAndFlush(Menu.builder()
                .nombre(naturalKey)
                .productos(new ArrayList<>())
                .restaurante(restaurant)
                .build());
        restaurant.setMenu(menu);
        menu.setRestaurante(restaurant);
        restauranteRepository.saveAndFlush(restaurant);
        return menu;
    }

    private Producto ensureProduct(Menu menu, ProductSpec spec) {
        List<Producto> matches = productoRepository.findAll().stream()
                .filter(product -> sameId(product.getMenu(), menu)
                        && spec.name().equals(product.getNombre()))
                .toList();
        if (matches.size() > 1) {
            throw conflict("product", spec.name(), "more than one row matches the menu and name");
        }
        Imagen image = ensureImage(spec.name().toLowerCase().replace(' ', '-'), spec.imageUrl());
        if (matches.size() == 1) {
            Producto existing = matches.getFirst();
            if (!matchesProduct(existing, spec, menu, image)) {
                throw conflict("product", spec.name(), "the existing row does not match the seed-owned values");
            }
            return existing;
        }

        return productoRepository.saveAndFlush(Producto.builder()
                .nombre(spec.name())
                .descripcion(spec.description())
                .precio(spec.price())
                .disponible(true)
                .imagen(image)
                .menu(menu)
                .productoPedido(new ArrayList<>())
                .build());
    }

    private boolean matchesProduct(Producto product, ProductSpec spec, Menu menu, Imagen image) {
        return sameId(product.getMenu(), menu)
                && Objects.equals(spec.name(), product.getNombre())
                && Objects.equals(spec.description(), product.getDescripcion())
                && Objects.equals(spec.price(), product.getPrecio())
                && Boolean.TRUE.equals(product.getDisponible())
                && matchesImage(product.getImagen(), image);
    }

    private boolean matchesImage(Imagen actual, Imagen expected) {
        return actual != null
                && expected != null
                && Objects.equals(actual.getUrl(), expected.getUrl())
                && Objects.equals(actual.getNombre(), expected.getNombre());
    }

    private void ensureCashOrder(
            Usuario client,
            Restaurante restaurant,
            Direccion restaurantAddress,
            List<Producto> products) {
        List<Pedido> matches = pedidoRepository.findAll().stream()
                .filter(order -> ORDER_PIN.equals(order.getPin()))
                .toList();
        if (matches.size() > 1) {
            throw conflict("order", ORDER_PIN, "more than one row matches the synthetic order pin");
        }

        Pedido order;
        boolean changed = false;
        if (matches.isEmpty()) {
            order = pedidoRepository.saveAndFlush(Pedido.builder()
                    .tipoEntrega(TipoEntrega.RETIRO_POR_LOCAL)
                    .fechaHora(ORDER_DATE)
                    .estado(EstadoPedido.PENDIENTE)
                    .total(ORDER_TOTAL)
                    .subtotalProductos(ORDER_TOTAL)
                    .costoDelivery(null)
                    .distanciaKm(null)
                    .bajaLogica(false)
                    .cliente(client)
                    .restaurante(restaurant)
                    .repartidor(null)
                    .pin(ORDER_PIN)
                    .direccionEntrega(restaurantAddress)
                    .direccionSnapshot(restaurantAddress.toSnapshot())
                    .items(new ArrayList<>())
                    .build());
        } else {
            order = matches.getFirst();
            validateOrderIdentity(order, client, restaurant);
            if (order.getDireccionEntrega() == null) {
                order.setDireccionEntrega(restaurantAddress);
                changed = true;
            } else if (!matchesAddress(order.getDireccionEntrega(), RESTAURANT_ADDRESS, restaurant.getUsuario())) {
                throw conflict("order", ORDER_PIN, "the pickup address is different from the seed-owned address");
            }
            if (order.getDireccionSnapshot() == null) {
                order.setDireccionSnapshot(restaurantAddress.toSnapshot());
                changed = true;
            } else if (!Objects.equals(order.getDireccionSnapshot(), restaurantAddress.toSnapshot())) {
                throw conflict("order", ORDER_PIN, "the address snapshot is different from the seed-owned snapshot");
            }
            if (changed) {
                order = pedidoRepository.saveAndFlush(order);
            }
        }

        ensureOrderItems(order, products);
        ensureCashPayment(order);
    }

    private void validateOrderIdentity(Pedido order, Usuario client, Restaurante restaurant) {
        if (!Objects.equals(TipoEntrega.RETIRO_POR_LOCAL, order.getTipoEntrega())
                || !Objects.equals(ORDER_DATE, order.getFechaHora())
                || !Objects.equals(EstadoPedido.PENDIENTE, order.getEstado())
                || !Objects.equals(ORDER_TOTAL, order.getTotal())
                || !Objects.equals(ORDER_TOTAL, order.getSubtotalProductos())
                || order.getCostoDelivery() != null
                || order.getDistanciaKm() != null
                || !Boolean.FALSE.equals(order.getBajaLogica())
                || order.getMotivoBaja() != null
                || order.getFechaBaja() != null
                || order.getEstadoAntesDeCancelado() != null
                || !sameId(order.getCliente(), client)
                || !sameId(order.getRestaurante(), restaurant)
                || order.getRepartidor() != null) {
            throw conflict("order", ORDER_PIN, "the existing row does not match the seed-owned values");
        }
    }

    private void ensureOrderItems(Pedido order, List<Producto> products) {
        List<ProductoPedido> existingItems = new ArrayList<>();
        productoPedidoRepository.findAll().forEach(item -> {
            if (item.getPedido() != null && sameId(item.getPedido(), order)) {
                existingItems.add(item);
            }
        });

        Map<Long, ProductoPedido> byProductId = new HashMap<>();
        Set<Long> expectedProductIds = new HashSet<>();
        for (Producto product : products) {
            if (product.getId() == null) {
                throw new IllegalStateException("Demo seed product has no database identifier: " + product.getNombre());
            }
            expectedProductIds.add(product.getId());
        }

        for (ProductoPedido item : existingItems) {
            if (item.getProducto() == null || item.getProducto().getId() == null
                    || !expectedProductIds.contains(item.getProducto().getId())) {
                throw conflict("order", ORDER_PIN, "the order contains an unexpected product item");
            }
            if (byProductId.put(item.getProducto().getId(), item) != null) {
                throw conflict("order", ORDER_PIN, "the order contains duplicate product items");
            }
            Producto product = products.stream()
                    .filter(candidate -> sameId(candidate, item.getProducto()))
                    .findFirst()
                    .orElseThrow();
            if (!matchesOrderItem(item, product)) {
                throw conflict("order", ORDER_PIN, "an existing product item has different snapshot values");
            }
        }

        for (Producto product : products) {
            if (!byProductId.containsKey(product.getId())) {
                productoPedidoRepository.save(ProductoPedido.builder()
                        .cantidad(1)
                        .precioUnitario(product.getPrecio())
                        .nombreProducto(product.getNombre())
                        .descripcionProducto(product.getDescripcion())
                        .imagenUrl(product.getImagen().getUrl())
                        .producto(product)
                        .pedido(order)
                        .build());
            }
        }
    }

    private boolean matchesOrderItem(ProductoPedido item, Producto product) {
        return Integer.valueOf(1).equals(item.getCantidad())
                && Objects.equals(product.getPrecio(), item.getPrecioUnitario())
                && Objects.equals(product.getNombre(), item.getNombreProducto())
                && Objects.equals(product.getDescripcion(), item.getDescripcionProducto())
                && product.getImagen() != null
                && Objects.equals(product.getImagen().getUrl(), item.getImagenUrl());
    }

    private void ensureCashPayment(Pedido order) {
        List<Pago> matches = pagoRepository.findAll().stream()
                .filter(payment -> payment.getPedido() != null && sameId(payment.getPedido(), order))
                .toList();
        if (matches.size() > 1) {
            throw conflict("payment", ORDER_PIN, "more than one payment belongs to the synthetic order");
        }
        if (matches.size() == 1) {
            Pago payment = matches.getFirst();
            if (!matchesCashPayment(payment, order)) {
                throw conflict("payment", ORDER_PIN, "the existing payment is not the synthetic cash payment");
            }
            return;
        }

        pagoRepository.saveAndFlush(Pago.builder()
                .pedido(order)
                .metodoPago(MetodoPago.EFECTIVO)
                .monto(ORDER_TOTAL)
                .estado(EstadoPago.PENDIENTE)
                .mercadoPagoPaymentId(null)
                .mercadoPagoPreferenceId(null)
                .mercadoPagoStatus(null)
                .mercadoPagoStatusDetail(null)
                .mercadoPagoPaymentType(null)
                .build());
    }

    private boolean matchesCashPayment(Pago payment, Pedido order) {
        return sameId(payment.getPedido(), order)
                && Objects.equals(MetodoPago.EFECTIVO, payment.getMetodoPago())
                && Objects.equals(ORDER_TOTAL, payment.getMonto())
                && Objects.equals(EstadoPago.PENDIENTE, payment.getEstado())
                && payment.getMercadoPagoPaymentId() == null
                && payment.getMercadoPagoPreferenceId() == null
                && payment.getMercadoPagoStatus() == null
                && payment.getMercadoPagoStatusDetail() == null
                && payment.getMercadoPagoPaymentType() == null;
    }

    private DemoSeedConflictException conflict(String entity, String naturalKey, String reason) {
        return new DemoSeedConflictException(entity + " natural key '" + naturalKey + "': " + reason);
    }

    private boolean sameId(Object left, Object right) {
        if (left == right) {
            return left != null;
        }
        if (left == null || right == null) {
            return false;
        }
        Long leftId = idOf(left);
        Long rightId = idOf(right);
        return leftId != null && leftId.equals(rightId);
    }

    private Long idOf(Object entity) {
        if (entity instanceof Rol role) {
            return role.getId();
        }
        if (entity instanceof Usuario user) {
            return user.getId();
        }
        if (entity instanceof Direccion address) {
            return address.getId();
        }
        if (entity instanceof Imagen image) {
            return image.getId();
        }
        if (entity instanceof Restaurante restaurant) {
            return restaurant.getId();
        }
        if (entity instanceof Menu menu) {
            return menu.getId();
        }
        if (entity instanceof Producto product) {
            return product.getId();
        }
        if (entity instanceof Pedido order) {
            return order.getId();
        }
        return null;
    }

    private record UserSpec(
            String firstName,
            String lastName,
            String phone,
            String email,
            String password,
            String role,
            Double latitude,
            Double longitude) {
    }

    private record AddressSpec(
            String alias,
            String street,
            String number,
            String floor,
            String apartment,
            String postalCode,
            String references,
            Double latitude,
            Double longitude,
            String city) {
    }

    record OpeningHourSpec(
            DayOfWeek day,
            LocalTime open,
            LocalTime close,
            Boolean crossesMidnight) {
    }

    private record ProductSpec(String name, String description, BigDecimal price, String imageUrl) {
    }
}
