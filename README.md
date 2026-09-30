# Mordisco - Plataforma de Delivery de Comida

<div align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Angular-20-DD0031?style=for-the-badge&logo=angular&logoColor=white" alt="Angular"/>
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL"/>
  <img src="https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
  <img src="https://img.shields.io/badge/Tailwind%20CSS-4.0-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white" alt="Tailwind CSS"/>
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21"/>
</div>

<br/>

<div align="center">
  <strong>Plataforma web integral que conecta restaurantes, clientes, repartidores y administradores para optimizar el proceso de pedidos de comida online.</strong>
</div>

<br/>

> **Proyecto de Tesis** - Universidad Tecnologica Nacional (UTN) - 2025

---

## Videos de demostración

- [Flujo del pedido - Cliente](https://drive.google.com/file/d/1cHdIQmJoE-6BG5IxZBqy4qj9T2btGmeX/view): navegación, selección de productos y confirmación de compra dentro de la plataforma.
- [Gestión del pedido - Multirol](https://drive.google.com/file/d/1y84EctAzGetQWCfFAtkFp6WMjqGqzdGY/view): restaurante, repartidor y cliente interactúan en tiempo real hasta la entrega final con validación mediante código.

## 🚀 Demo local con Docker

### Inicio rápido

Necesitás Bash, Docker Engine o Docker Desktop con Docker Compose v2, y conexión a Internet durante el primer build para descargar imágenes y dependencias. Para esta demo no hace falta instalar Java, Node.js, Maven, MySQL, `curl` ni `openssl` en el host.

```bash
git clone https://github.com/burgosfacundo/mordisco-app.git
cd mordisco-app
./start.sh
```

`./start.sh` construye y levanta la demo, espera a MySQL y comprueba una ruta de API que consulta la base, el frontend y Mailpit mediante pruebas HTTP ejecutadas en contenedores. Después ejecuta el seed. En una base vacía, `data.sql` y el comprobante de seed se aplican en una transacción bajo un lock de MySQL; los inicios siguientes detectan el comprobante y omiten el seed. Si encuentra usuarios sin comprobante, se detiene en vez de volver a insertar datos y sugiere evaluar un reset. Las tablas del seed deben usar InnoDB para permitir rollback.

| Servicio | Dirección | Uso |
|---|---|---|
| Aplicación | http://localhost:4200 | Frontend de la demo |
| API | http://localhost:8080 | Backend |
| MySQL | `localhost:3306` | Base local de la demo |
| Mailpit | http://localhost:8025 | Bandeja local para inspeccionar correo SMTP |

Los puertos publicados se enlazan a `127.0.0.1`: la demo es local, no de producción, y no queda expuesta a la red. Compose usa credenciales de base de datos exclusivas para esta demo. No hacen falta claves privadas de proveedores ni credenciales de Gmail para iniciar.

Mailpit está configurado como receptor SMTP local. Que su interfaz HTTP responda —o que una prueba SMTP sintética llegue a la bandeja— no demuestra que un flujo de la aplicación haya emitido un correo, ni que se entregue a destinatarios reales.

Por defecto, cada inicio con `./start.sh` genera una clave JWT aleatoria de 32 bytes dentro de un contenedor temporal y se la pasa al contenedor backend como variable de entorno. El script no la imprime ni la escribe en `.env`, pero Docker la mantiene en la configuración/entorno del contenedor mientras este exista. Al iniciar de nuevo se genera otra clave y las sesiones existentes dejan de servir: vas a tener que iniciar sesión otra vez. Si definís `JWT_SECRET` en el entorno antes de ejecutar el script, se reutiliza ese valor. No reutilices esta configuración de demo en producción.

**Usuarios de prueba:**
- Admin: `mordiscoapp@gmail.com` / `Admin123!`
- Restaurante: `restaurante1@gmail.com` / `Mordisco123!`
- Cliente: `usuario1@gmail.com` / `Mordisco123!`
- Repartidor: `repartidor1@gmail.com` / `Mordisco123!`

### Detener y reiniciar

- `./stop.sh` detiene los contenedores y **conserva** los datos persistidos. Volvé a ejecutar `./start.sh` para continuar; se generará otra clave JWT y vas a tener que iniciar sesión de nuevo.
- `./reset-demo.sh` solicita confirmación escribiendo `mordisco-demo`. Solo entonces elimina los contenedores de este proyecto Compose, su red y su volumen de datos; esta acción borra permanentemente los datos de la demo.
- El reset no elimina imágenes Docker, archivos del repositorio ni recursos de otros proyectos, y no hace limpieza global de Docker. No se ejecuta ningún reset al iniciar o detener.

### Configuración local opcional

Compose toma automáticamente las variables del archivo `.env` en la raíz del repositorio cuando ejecutás `./start.sh`. No necesitás crear ese archivo para usar los valores predeterminados de la demo. `demo.env.example` es una plantilla segura con ajustes locales opcionales; si querés personalizarlos, copiála solo cuando `.env` todavía no exista:

```bash
if [ ! -e .env ]; then cp demo.env.example .env; else printf '.env ya existe; no se sobrescribió.\n'; fi
```

Editá tu `.env` sin compartirlo ni confirmar su contenido. Si ya existe, conservá sus valores y agregá solo las variables que necesites.

### Pagos opcionales: Mercado Pago

La demo inicia y permite pagar en efectivo sin credenciales de proveedores. Mercado Pago requiere credenciales de prueba propias; sin ellas, el checkout informa que no está disponible, conserva el carrito y permite elegir efectivo. Que una credencial esté configurada no prueba que sea válida: Mercado Pago debe aceptarla. No se simulan pagos aprobados ni se verifica la validez de credenciales reales.

1. Iniciá sesión en [Mercado Pago Developers](https://www.mercadopago.com.ar/developers/es/docs/your-integrations/credentials), abrí **Tus integraciones**, elegí tu aplicación y entrá en **Pruebas > Credenciales de prueba**.
2. Guardá tu **Access Token privado** y tu **Public Key** de prueba en `.env` (Compose los pasa al backend):

   ```dotenv
   MERCADOPAGO_ACCESS_TOKEN=tu_access_token_de_prueba
   MERCADOPAGO_PUBLIC_KEY=tu_public_key_de_prueba
   ```

   No uses ni solicites las claves del dueño del proyecto. Si `.env` ya existe, agregá las variables sin sobrescribirlo; para crearlo, seguí la instrucción de arriba. Usá únicamente credenciales de prueba: no se deben generar cargos reales.
3. Reiniciá la demo para que el backend lea las variables.

El checkout local puede crear y abrir una preferencia de prueba, pero eso **no** confirma que el ciclo de pago se haya completado en la aplicación. El webhook de Mercado Pago necesita una URL pública alcanzable por el proveedor; `localhost` no es accesible desde Internet. Para probar notificaciones, desplegá la API o usá un túnel público y definí `MERCADOPAGO_NOTIFICATION_URL` con una URL HTTPS pública terminada en `/api/pagos/webhook`. No expongas servicios locales sin entender el riesgo.

### Clima: configuración futura, no implementada

La aplicación actualmente no consume OpenWeatherMap ni implementa funcionalidad de clima. Obtener una clave no agrega ni habilita esa función. Si querés conservar una clave para una futura integración, creá una cuenta, generá una API key en [OpenWeatherMap](https://openweathermap.org/appid) / [tus API keys](https://home.openweathermap.org/api_keys) y guardala opcionalmente como `OPENWEATHERMAP_API_KEY` en `.env`. No es necesaria para iniciar ni usar la demo.

---

## Tabla de Contenidos

- [Descripción del Proyecto](#descripcion-del-proyecto)
- [Arquitectura del Sistema](#arquitectura-del-sistema)
- [Funcionalidades por Rol](#funcionalidades-por-rol)
  - [Cliente](#cliente)
  - [Restaurante](#restaurante)
  - [Repartidor](#repartidor)
  - [Administrador](#administrador)
- [Tecnologías Utilizadas](#tecnologias-utilizadas)
- [Modelo de Datos](#modelo-de-datos)
- [Instalación y Ejecución](#instalacion-y-ejecucion)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [API Documentation](#api-documentation)
- [Seguridad](#seguridad)
- [Autores](#autores)

---

## Descripcion del Proyecto

**Mordisco** es una solucion completa de delivery de comida desarrollada como proyecto de tesis. La plataforma permite a los usuarios realizar pedidos desde restaurantes locales, con soporte para entregas a domicilio y retiro en local.

### Objetivos Alcanzados

- Sistema multirol con experiencias personalizadas para cada tipo de usuario
- Integracion de pagos online con MercadoPago y opcion de pago en efectivo
- Notificaciones en tiempo real via WebSocket
- Sistema de calificaciones bidireccional (restaurantes y repartidores)
- Dashboards de estadisticas con metricas de negocio
- Geolocalizacion para asignacion inteligente de repartidores
- Arquitectura escalable y mantenible

---

## Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────────────┐
│                        FRONTEND (Angular 20)                     │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────────┐ │
│  │  Cliente │  │Restaurant│  │Repartidor│  │  Administrador   │ │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────────┬─────────┘ │
└───────┼─────────────┼─────────────┼─────────────────┼───────────┘
        │             │             │                 │
        └─────────────┴─────────────┴─────────────────┘
                              │
                    ┌─────────▼─────────┐
                    │   REST API / WS   │
                    └─────────┬─────────┘
                              │
┌─────────────────────────────▼───────────────────────────────────┐
│                     BACKEND (Spring Boot 3)                      │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                    Security Layer (JWT)                     │ │
│  ├────────────────────────────────────────────────────────────┤ │
│  │  Controllers │ Services │ Repositories │ Event Listeners   │ │
│  ├────────────────────────────────────────────────────────────┤ │
│  │     WebSocket (STOMP)    │    MercadoPago SDK    │  Email  │ │
│  └────────────────────────────────────────────────────────────┘ │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                    ┌─────────▼─────────┐
                    │   MySQL Database  │
                    └───────────────────┘
```

### Monorepo Structure

```
mordisco-app/
├── mordisco-api/          # Backend Spring Boot
│   ├── src/main/java/
│   │   └── utn/back/mordiscoapi/
│   │       ├── controller/       # REST endpoints
│   │       ├── service/          # Business logic
│   │       ├── repository/       # Data access
│   │       ├── model/            # Entities, DTOs, Projections
│   │       ├── security/         # JWT, Guards
│   │       ├── event/            # Event-driven notifications
│   │       ├── scheduler/        # Scheduled tasks
│   │       └── config/           # Configurations
│   └── docker-compose.yml
│
└── mordisco-front/        # Frontend Angular
    └── src/app/
        ├── core/                 # Guards, Interceptors, Core Services
        ├── features/             # Feature modules by domain
        │   ├── auth/
        │   ├── home/
        │   ├── carrito/
        │   ├── mi-restaurante/
        │   ├── entregas/
        │   ├── admin/
        │   ├── calificacion/
        │   └── estadisticas/
        └── shared/               # Shared components, services, models
```

---

## Funcionalidades por Rol

### Cliente

| Funcionalidad | Descripcion |
|--------------|-------------|
| **Registro y Autenticacion** | Registro con validaciones, login con JWT, recuperacion de contrasena por email |
| **Exploracion de Restaurantes** | Busqueda por nombre, filtrado por ciudad, badges de estado (abierto/cerrado), horarios en tiempo real |
| **Sistema de Promociones** | Visualizacion de restaurantes con promociones activas y descuentos aplicados |
| **Carrito de Compras** | Agregar/eliminar productos, persistencia local, resumen de pedido |
| **Checkout Completo** | Seleccion de direccion, tipo de entrega (delivery/retiro), calculo de costos |
| **Métodos de Pago** | Mercado Pago con credenciales propias configuradas, o pago en efectivo contra entrega |
| **Seguimiento de Pedidos** | Estados en tiempo real: Pendiente → En Preparacion → En Camino → Completado |
| **Notificaciones Push** | WebSocket para actualizaciones instantaneas del estado del pedido |
| **Historial de Pedidos** | Lista completa con filtros por estado y fecha |
| **Sistema de Calificaciones** | Calificar restaurante (comida, tiempo, packaging) y repartidor (atencion, comunicacion) |
| **Gestion de Direcciones** | CRUD de direcciones de entrega con geolocalizacion |
| **Gestion de Perfil** | Edicion de datos personales y cambio de contrasena |

### Restaurante

| Funcionalidad | Descripcion |
|--------------|-------------|
| **Gestion de Perfil** | Edicion de razon social, logo, estado activo/inactivo |
| **Gestion de Direccion** | Configuracion de ubicacion con coordenadas geograficas |
| **Gestion de Menu** | Crear y editar menus con nombre y descripcion |
| **Gestion de Productos** | CRUD completo: nombre, descripcion, precio, imagen, disponibilidad |
| **Gestion de Horarios** | Configuracion de horarios de atencion por dia de la semana |
| **Gestion de Promociones** | Crear promociones con porcentaje de descuento y fechas de vigencia |
| **Panel de Pedidos** | Visualizacion y gestion de pedidos entrantes con filtros |
| **Cambio de Estados** | Flujo de estados: Pendiente → En Preparacion → Listo para Entregar/Retirar |
| **Notificaciones en Tiempo Real** | Alertas de nuevos pedidos via WebSocket |
| **Dashboard de Estadisticas** | Ingresos por periodo, productos mas vendidos, tiempo promedio de preparacion |
| **Visualizacion de Calificaciones** | Ver calificaciones recibidas por los clientes |

### Repartidor

| Funcionalidad | Descripcion |
|--------------|-------------|
| **Pedidos Disponibles** | Lista de pedidos "Listo para Entregar" filtrados por proximidad geografica |
| **Aceptacion de Pedidos** | Tomar un pedido disponible y asignarselo |
| **Flujo de Entrega** | Estados: Asignado → En Camino → Completado con PIN de confirmacion |
| **Historial de Entregas** | Lista de pedidos entregados con filtros |
| **Dashboard de Estadisticas** | Total de entregas, ganancias por periodo, calificacion promedio |
| **Ganancias Detalladas** | Registro de ganancias por cada entrega realizada |
| **Visualizacion de Calificaciones** | Ver calificaciones recibidas de los clientes |

### Administrador

| Funcionalidad | Descripcion |
|--------------|-------------|
| **Gestion de Usuarios** | Listado completo, busqueda, filtros por rol, visualizacion de detalles |
| **Gestion de Restaurantes** | Ver todos los restaurantes, sus menus, calificaciones y estadisticas |
| **Gestion de Pedidos** | Vista global de todos los pedidos, filtros avanzados, cancelacion con motivo |
| **Gestion de Calificaciones** | Busqueda y filtrado de calificaciones del sistema |
| **Configuracion del Sistema** | Parametros globales: costo por km, porcentaje de ganancia repartidor |
| **Dashboard de Estadisticas** | Metricas globales: usuarios totales, pedidos, ingresos, metodos de pago mas usados |
| **Baja Logica** | Dar de baja usuarios, restaurantes o pedidos con motivo |

---

## Tecnologias Utilizadas

### Backend

| Tecnologia | Version | Uso |
|------------|---------|-----|
| Java | 21 | Lenguaje de programacion |
| Spring Boot | 3.x | Framework principal |
| Spring Security | 6.x | Autenticacion y autorizacion |
| Spring Data JPA | 3.x | ORM y persistencia |
| Spring WebSocket | 6.x | Comunicacion en tiempo real |
| Spring Mail | 3.x | Envio de emails |
| MySQL | 8.0 | Base de datos relacional |
| JWT (JJWT) | 0.11.5 | Tokens de autenticacion |
| MercadoPago SDK | - | Integracion de pagos |
| Caffeine | - | Cache en memoria |
| Lombok | 1.18.x | Reduccion de boilerplate |
| Springdoc OpenAPI | - | Documentacion de API |

### Frontend

| Tecnologia | Version | Uso |
|------------|---------|-----|
| Angular | 20 | Framework SPA |
| TypeScript | 5.x | Lenguaje tipado |
| Tailwind CSS | 4 | Estilos utility-first |
| Angular Material | 18.x | Componentes UI |
| RxJS | 7.x | Programacion reactiva |
| @stomp/stompjs | - | Cliente WebSocket |
| Chart.js + ng2-charts | - | Graficos estadisticos |

### Infraestructura

| Herramienta | Uso |
|-------------|-----|
| Docker Compose v2 | Stack local de demo (MySQL, API, frontend y Mailpit) |
| Maven | Gestion de dependencias backend |
| npm | Gestion de paquetes frontend |
| Git | Control de versiones |

---

## Modelo de Datos

### Entidades Principales

```
Usuario (usuarios)
├── id, email, password, nombre, apellido, telefono
├── activo, bajaLogica, motivoBaja
├── roles (ManyToMany → Rol)
├── direcciones (OneToMany → Direccion)
└── [Repartidor]: latitud, longitud, disponible

Restaurante (restaurantes)
├── id, razonSocial, activo, bajaLogica
├── usuario (ManyToOne → Usuario)
├── direccion (OneToOne → Direccion)
├── menu (OneToOne → Menu)
├── horarios (OneToMany → HorarioAtencion)
└── promociones (OneToMany → Promocion)

Menu (menus)
├── id, nombre
└── productos (OneToMany → Producto)

Producto (productos)
├── id, nombre, descripcion, precio
├── disponible, imagen (OneToOne → Imagen)
└── menu (ManyToOne → Menu)

Pedido (pedidos)
├── id, fechaHora, estado, tipoEntrega
├── total, subtotalProductos, costoDelivery, distanciaKm
├── cliente (ManyToOne → Usuario)
├── restaurante (ManyToOne → Restaurante)
├── repartidor (ManyToOne → Usuario)
├── direccionEntrega (ManyToOne → Direccion)
├── items (OneToMany → ProductoPedido)
├── calificacionPedido (OneToOne → CalificacionPedido)
└── calificacionRepartidor (OneToOne → CalificacionRepartidor)

Pago (pagos)
├── id, metodoPago, monto, estado
├── mercadoPagoId, fechaPago
└── pedido (OneToOne → Pedido)

CalificacionPedido (calificaciones_pedido)
├── id, estrellas, comentario, fechaCreacion
├── pedido (OneToOne → Pedido)
└── cliente (ManyToOne → Usuario)

CalificacionRepartidor (calificaciones_repartidor)
├── id, estrellas, comentario, fechaCreacion
├── pedido (OneToOne → Pedido)
├── repartidor (ManyToOne → Usuario)
└── cliente (ManyToOne → Usuario)
```

### Estados del Pedido

```
RETIRO EN LOCAL:
Pendiente → En Preparacion → Listo para Retirar → Completado

DELIVERY:
Pendiente → En Preparacion → Listo para Entregar → Asignado a Repartidor → En Camino → Completado

Cualquier estado (excepto Completado) → Cancelado
```

---

## Instalacion y Ejecucion

El inicio recomendado para probar la aplicación con datos de demo es el quickstart Docker de arriba. Esta sección describe una alternativa para desarrollo manual desde el host; no es necesaria para la demo y no ejecuta su seed ni levanta Mailpit.

### Requisitos para desarrollo manual

- Java 21+
- Node.js 18+ (LTS)
- Maven 3.8+
- MySQL 8.0

### Backend

```bash
# 1. Iniciar MySQL con Docker Compose v2.
#    Definí MYSQL_ROOT_PASSWORD y MYSQL_PASSWORD en el entorno antes de arrancar.
cd mordisco-api
docker compose up -d

# 2. Configurar las variables de entorno requeridas por la API para tu entorno local.
#    Luego ejecutar la API (perfil dev).
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# La API estará disponible en http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### Frontend

```bash
# 1. Instalar dependencias
cd mordisco-front
npm install

# 2. Iniciar servidor de desarrollo
npm start

# La aplicación estará disponible en http://localhost:4200
```

---

## Estructura del Proyecto

### Backend - Controladores

| Controlador | Descripcion |
|-------------|-------------|
| `UsuarioController` | Registro, gestion de usuarios, bajas logicas |
| `PedidoController` | CRUD pedidos, cambio de estados, asignacion repartidor |
| `RestauranteController` | CRUD restaurantes, busquedas, filtros |
| `MenuController` | Gestion de menus |
| `ProductoController` | CRUD productos con imagenes |
| `HorarioController` | Gestion de horarios de atencion |
| `PromocionController` | CRUD promociones |
| `DireccionController` | CRUD direcciones con geocodificacion |
| `CalificacionController` | Calificaciones de pedidos y repartidores |
| `RepartidorController` | Gestion de repartidores y pedidos disponibles |
| `EstadisticasController` | Dashboards por rol |
| `GananciaRepartidorController` | Registro de ganancias |
| `ConfiguracionSistemaController` | Parametros globales |
| `PagoController` | Consulta de pagos |

### Frontend - Features

| Feature | Descripcion |
|---------|-------------|
| `auth` | Login, registro, recuperacion de contrasena |
| `home` | Paginas de inicio por rol |
| `carrito` | Carrito, checkout, confirmacion de pago |
| `mi-restaurante` | Panel completo del restaurante |
| `mis-pedidos` | Historial de pedidos (cliente/restaurante) |
| `entregas` | Panel del repartidor |
| `admin` | Panel de administracion |
| `calificacion` | Formularios y listados de calificaciones |
| `estadisticas` | Dashboards con graficos |
| `direccion` | Gestion de direcciones |
| `profile` | Gestion de perfil de usuario |

---

## API Documentation

La documentacion interactiva de la API esta disponible en Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

### Endpoints Principales

| Recurso | Metodo | Endpoint | Descripcion |
|---------|--------|----------|-------------|
| Auth | POST | `/api/auth/login` | Iniciar sesion |
| Auth | POST | `/api/auth/register` | Registrar usuario |
| Auth | POST | `/api/auth/refresh` | Renovar token |
| Pedidos | POST | `/api/pedidos/save` | Crear pedido |
| Pedidos | PUT | `/api/pedidos/state/{id}` | Cambiar estado |
| Pedidos | POST | `/api/pedidos/{id}/aceptar-repartidor` | Repartidor acepta pedido |
| Restaurantes | GET | `/api/restaurantes` | Listar restaurantes |
| Calificaciones | POST | `/api/calificaciones/pedido` | Calificar pedido |
| Estadisticas | GET | `/api/estadisticas/admin` | Dashboard admin |

---

## Seguridad

### Autenticacion

- **JWT Access Token**: Expiracion 15 minutos
- **Refresh Token**: Cookie httpOnly, 7 dias de duracion
- Renovacion automatica en frontend

### Password recovery operations

Password recovery uses opaque, single-use credentials. This is a backend security change; Angular production files and the existing user journey remain unchanged.

#### Public compatibility contract

- The existing frontend public routes remain `/recover-password` and `/reset-password`.
- The reset page continues to read the `token` query parameter and sends the unchanged `{ token, newPassword }` payload to `POST /api/usuarios/reset-password`; recovery continues through `POST /api/usuarios/recover-password`.
- A syntactically valid recovery request always receives `200 OK` with an empty body. The response does not reveal account existence, activation, cooldown, token state, or mail delivery outcome.
- Reset does not auto-login the user. A successful reset revokes every refresh session for the affected user only; existing access JWT authentication, claims, and expiry behavior are unchanged, so an already-issued access JWT remains valid until its normal expiry.

#### Credential storage and lifecycle

The dedicated `password_recovery_credentials` table stores the current recovery state. It is intentionally one row per user: `usuario_id` is a unique foreign key to `usuarios(id)`, and `token_digest` is unique. `issued_at`, `expires_at`, `cooldown_until`, and nullable `consumed_at` use MySQL `DATETIME(6)` with UTC semantics.

Only a lowercase SHA-256 digest is stored. The raw opaque token exists only in memory while building the email reset link and while processing the submitted reset request. Never persist, log, trace, or include a raw token, reset URL, password, digest, or full recovery email address in operational output.

| Setting | Environment variable | Unit | Default | Allowed range | Safe behavior |
|---|---|---:|---:|---:|---|
| `app.password-recovery.expiration-seconds` | `PASSWORD_RECOVERY_EXPIRATION_SECONDS` | seconds | 3600 | 300–86400 | Missing values use the default; non-numeric or out-of-range values fail application startup. |
| `app.password-recovery.cooldown-seconds` | `PASSWORD_RECOVERY_COOLDOWN_SECONDS` | seconds | 300 | 60–86400 | Missing values use the default; non-numeric or out-of-range values fail application startup. |

Expiration and cooldown are independent. Expiry is exclusive (`now >= expires_at` is invalid); cooldown is inclusive of suppression (`now < cooldown_until` is suppressed). A permitted resend replaces the prior digest; a cooldown-suppressed request changes nothing.

A UTC cleanup runs daily and deletes only records for which `cooldown_until <= now` and either `consumed_at` is set or `expires_at <= now`. This retains a consumed record until cooldown ends and an expired record while needed to enforce a longer cooldown; rows are otherwise bounded by the lifecycle rather than kept as history.

#### Delivery, privacy, and failure semantics

Recovery and password-change email handlers run asynchronously only after the database transaction commits (`AFTER_COMMIT`). Delivery is best effort: an SMTP, template, or executor failure does not roll back a committed credential or password reset, does not retry automatically, and does not change the generic public response. A process failure between commit and dispatch can lose an email; this implementation is not a durable outbox.

Operational logs for these handlers use fixed aggregate failure text only. Do not add request-level success, suppression, account-state, recipient, exception-detail, credential, or link logging. Mail delivery is the sole boundary where the raw token and recipient address are used.

#### MySQL deployment verification

Hibernate additive DDL (`spring.jpa.hibernate.ddl-auto=update`) creates the table under the repository's no-migration-framework convention. Before deployment, confirm that the production database principal is permitted to apply this additive DDL. If runtime DDL is restricted, have an operator create the equivalent additive table and constraints before starting the backend; do not introduce a migration framework solely for this change.

Before enabling recovery traffic, inspect the deployed MySQL schema and confirm:

1. `password_recovery_credentials` exists with the expected primary key.
2. The `usuario_id` unique constraint and foreign key to `usuarios(id)` exist.
3. The `token_digest` uniqueness constraint exists.
4. The four lifecycle columns use `DATETIME(6)` and the application is configured with valid duration settings.
5. A successful reset revokes only the affected user's refresh sessions, while access JWT behavior remains unchanged.

Use only redacted aggregate mail and application-health signals during rollout. Do not place secrets, passwords, reset URLs, raw tokens, or token digests in deployment notes, commands, dashboards, or support tickets.

#### Rollout and rollback

1. Back up or inspect the schema, verify the DDL capability above, and deploy the backend as one opaque-credential behavior switch.
2. Existing recovery JWTs are not accepted after the switch; users request a new recovery email. Never restore mixed JWT/opaque acceptance.
3. Keep the frontend routes and access-JWT deployment unchanged, then validate only generic responses and redacted aggregate mail health.
4. For an emergency rollback, the additive table may remain. Clear its rows before a corrected forward deployment so issued opaque links are invalidated. Do not drop the table during the emergency action.
5. Do not re-enable recovery JWT handling as a normal rollback. If the backend must be reverted, disable both recovery endpoints until a fixed forward deployment is available rather than accepting either a mixed JWT/opaque path or previously issued JWT recovery credentials.

### Autorizacion

- **RBAC** (Role-Based Access Control)
- Guards en frontend por ruta
- `@PreAuthorize` en backend por endpoint
- Security beans para validar ownership de recursos

### Roles

| Rol | Descripcion |
|-----|-------------|
| `ROLE_CLIENTE` | Usuario que realiza pedidos |
| `ROLE_RESTAURANTE` | Dueno de restaurante |
| `ROLE_REPARTIDOR` | Repartidor de pedidos |
| `ROLE_ADMIN` | Administrador del sistema |

---

## Autores

| Autor | Rol | GitHub |
|-------|-----|--------|
| **Facundo Burgos** | Desarrollo Full Stack | [@burgosfacundo](https://github.com/burgosfacundo) |
| **Micaela Mandes** | Desarrollo Full Stack | [@micamandes9](https://github.com/micamandes9) |
| **Luana Mena** | Desarrollo Full Stack | [@luanamena2004](https://github.com/luanamena2004) |

---

<div align="center">
  <br/>
  <p><strong>Universidad Tecnologica Nacional (UTN)</strong></p>
  <p>Tesis de Grado - 2025</p>
  <br/>
  <sub>Desarrollado con dedicacion para la comunidad academica</sub>
</div>
