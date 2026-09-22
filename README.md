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

## 🚀 Inicio Rápido local con Docker Compose

```bash
git clone https://github.com/burgosfacundo/mordisco-app.git
cd mordisco-app
./start.sh
```

Esto levanta MySQL, Backend y Frontend con datos de prueba. Accede en: **http://localhost:4200**

**Usuarios de prueba:**
- Admin: `mordiscoapp@gmail.com` / `Admin123!`
- Restaurante: `restaurante1@gmail.com` / `Mordisco123!`
- Cliente: `usuario1@gmail.com` / `Mordisco123!`
- Repartidor: `repartidor1@gmail.com` / `Mordisco123!`

**Detener:** `./stop.sh`

### Configuracion local

Antes de iniciar, copia `.env.example` a `.env`. Completa las credenciales locales cuando sea necesario, nunca confirmes `.env` y genera localmente un secreto JWT fuerte.

> Esta sección es exclusivamente local: usa Docker Compose, MySQL y datos de prueba. No reutilices sus credenciales, URLs ni el volumen `mysql_data` para producción.

---

## Despliegue de producción (DEPLOY-5)

> Esta ruta usa un único proyecto de Vercel. El frontend y el backend son servicios del mismo `vercel.json`; la persistencia queda fuera de Vercel, en TiDB Cloud.

### Topología de producción

| Entrada | Servicio | Contrato |
|---|---|---|
| `/` y cualquier ruta que no empiece por `/api` | Angular (`mordisco-front`) | Build de producción con `apiUrl: '/api'`; no necesita variables de entorno en runtime. |
| `/api/*` | Spring Boot (`mordisco-api`) | Contenedor `Dockerfile.vercel`; Vercel le entrega `PORT`. |
| Persistencia | TiDB Cloud externo | Endpoint MySQL-compatible de Connector/J, con los parámetros TLS entregados por el proveedor. |
| TLS público | Edge de Vercel | Vercel termina HTTPS; el contenedor sirve HTTP y no usa keystore de aplicación. |

`vercel.json` implementa los rewrites `/api/(.*) → backend` y `/(.*) → frontend`. No despliegues un segundo proyecto para separar el API: esa separación rompería la topología same-origin documentada.

### Ruta rápida

**Prerrequisitos**

- Un proyecto de Vercel conectado al repositorio y un dominio HTTPS público.
- Una base TiDB Cloud creada, con usuario, contraseña y JDBC URL emitidos por el proveedor.
- Credenciales de Mercado Pago **Sandbox**, su secreto de firma de webhooks y una URL pública HTTPS.
- Credenciales SMTP y una API key de OpenWeatherMap.
- Un scheduler HTTP externo con plan gratuito que pueda ejecutar un `POST` al menos una vez por día.
- Para validar localmente: Java 21, Maven, Node.js/npm y, para la suite completa, Docker disponible para Testcontainers.

**Pasos**

1. En **Vercel → Project Settings → Environment Variables**, carga las variables de las tablas siguientes para el entorno **Production**. Usa valores reales sólo en Vercel; el repositorio contiene únicamente placeholders.
2. Haz el bootstrap de esquema **una sola vez**: cambia temporalmente `SPRING_PROFILES_ACTIVE` a `prod,schema-bootstrap` y ejecuta un arranque/redeploy. Verifica en los logs que la aplicación inicia y que TiDB acepta el DDL.
3. Vuelve a `SPRING_PROFILES_ACTIVE=prod` y ejecuta el arranque/redeploy normal. Desde ese momento el esquema debe validarse, no modificarse automáticamente.
4. Ejecuta los smoke checks de abajo y configura el scheduler de mantenimiento. No consideres completado el despliegue si queda alguno de los holds externos sin resolver.

### Variables de producción del backend

Los nombres de esta tabla son los nombres exactos que consume la configuración Spring. Los ejemplos son placeholders: no los copies como credenciales. Podés usar `mordisco-api/env.production.example` como checklist para cargar las variables en Vercel; no lo conviertas en un archivo con valores reales dentro del repositorio.

#### Requeridas para el arranque `prod`

| Variable | Placeholder/valor esperado | Uso y validación |
|---|---|---|
| `DATABASE_URL` | `<provider-issued-jdbc-url-with-tls-options>` | JDBC URL completa de TiDB; no tiene default útil. |
| `DATABASE_USERNAME` | `<set-in-vercel>` | Usuario de TiDB; requerido y no versionado. |
| `DATABASE_PASSWORD` | `<set-in-vercel>` | Contraseña de TiDB; requerido y no versionado. |
| `FRONTEND_URL` | `https://<vercel-domain>` | Origin público para CORS y enlaces de correo; debe ser exacto, sin wildcard. |
| `JWT_SECRET` | `<random-value-at-least-32-characters>` | Se rechaza un secreto de menos de 32 caracteres. No lo reutilices entre entornos. |
| `SPRING_MAIL_HOST` | `<smtp-host>` | Host SMTP. |
| `SPRING_MAIL_PORT` | `<smtp-port>` | Puerto SMTP; no hay default en la configuración base. |
| `SPRING_MAIL_USERNAME` | `<smtp-username>` | Cuenta remitente SMTP. |
| `SPRING_MAIL_PASSWORD` | `<set-in-vercel>` | Credencial SMTP. |
| `OPENWEATHERMAP_API_KEY` | `<set-in-vercel>` | API externa usada por las funcionalidades meteorológicas. |
| `MAINTENANCE_SECRET` | `<set-in-vercel>` | Secreto requerido para el endpoint de mantenimiento. |

#### Requeridas para pagos y webhooks

| Variable | Placeholder/valor esperado | Uso y validación |
|---|---|---|
| `MERCADOPAGO_ACCESS_TOKEN` | `<set-in-vercel>` | Crea preferencias y consulta pagos en Mercado Pago. Usa el token del mismo entorno seleccionado. |
| `MERCADOPAGO_PUBLIC_KEY` | `<set-in-vercel>` | Propiedad de configuración de la cuenta MP; se carga junto con el token, pero el checkout actual es server-side y el frontend no la lee en runtime. |
| `MERCADOPAGO_WEBHOOK_SECRET` | `<set-in-vercel>` | Secreto de firma v1 entregado por Mercado Pago; obligatorio en perfil `prod`. |
| `MERCADOPAGO_NOTIFICATION_URL` | `https://<vercel-domain>/api/pagos/webhook` | URL pública HTTPS exacta; el perfil `prod` exige el path `/api/pagos/webhook` y rechaza localhost. |
| `MERCADOPAGO_ENVIRONMENT` | `sandbox` | Selector admitido: `sandbox` o `production`. DEPLOY-5 usa `sandbox` con credenciales Sandbox. |

#### Opcionales o con default seguro

| Variable | Default | Nota operativa |
|---|---:|---|
| `SPRING_PROFILES_ACTIVE` | `prod` en `Dockerfile.vercel` | Para el bootstrap se cambia sólo una vez a `prod,schema-bootstrap`; luego debe volver a `prod`. |
| `PORT` | `SERVER_PORT` o `8080` | Vercel entrega `PORT`; no fuerces un puerto TLS dentro del contenedor. |
| `SERVER_PORT` | `8080` | Fallback si no existe `PORT`. |
| `WEBSOCKET_ALLOWED_ORIGINS` | `FRONTEND_URL` en `prod` | Debe ser un origin exacto y sin `*`. STOMP está desactivado en producción, pero la validación de configuración sigue aplicando. |
| `SMTP_CONNECTION_TIMEOUT_MS` | `5000` | Timeout de conexión SMTP en milisegundos. |
| `SMTP_READ_TIMEOUT_MS` | `5000` | Timeout de lectura SMTP en milisegundos. |
| `SMTP_WRITE_TIMEOUT_MS` | `5000` | Timeout de escritura SMTP en milisegundos. |
| `PASSWORD_RECOVERY_EXPIRATION_SECONDS` | `3600` | Rango válido: 300–86400 segundos. |
| `PASSWORD_RECOVERY_COOLDOWN_SECONDS` | `300` | Rango válido: 60–86400 segundos. |
| `DB_POOL_MIN_IDLE` | `0` | Pool Hikari sin conexiones ociosas por defecto, adecuado para scale-to-zero. |
| `DB_POOL_MAX_SIZE` | `4` | Máximo de conexiones Hikari por instancia. |
| `DB_POOL_CONNECTION_TIMEOUT_MS` | `10000` | Timeout para obtener una conexión. |
| `DB_POOL_VALIDATION_TIMEOUT_MS` | `5000` | Timeout de validación de conexión. |
| `DB_POOL_IDLE_TIMEOUT_MS` | `60000` | Tiempo de inactividad antes de retirar una conexión. |
| `DB_POOL_MAX_LIFETIME_MS` | `300000` | Vida máxima de una conexión. |

El frontend **no tiene variables de entorno de runtime**. `mordisco-front/src/environments/environment.ts` compila `apiUrl: '/api'` y `websocketEnabled: false`; por eso el navegador usa el mismo origin y la producción no intenta abrir STOMP. No agregues secretos al build Angular.

### TiDB, TLS y esquema

- Copia en `DATABASE_URL` la JDBC URL completa emitida por TiDB Cloud. Conserva host, puerto, base, parámetros de certificado y opciones como `sslMode=VERIFY_IDENTITY`; no reconstruyas la URL ni le quites la query del proveedor.
- `application-prod.properties` agrega además el guard de Connector/J `sslMode=VERIFY_IDENTITY`. El TLS de TiDB es independiente del TLS público: Vercel termina HTTPS en el edge y Spring no necesita `server.ssl.*`, certificado ni keystore.
- `prod,schema-bootstrap` habilita el único arranque controlado con `spring.jpa.hibernate.ddl-auto=update` para crear/actualizar el esquema. Haz backup/verifica permisos antes de ejecutarlo y no lo repitas como mecanismo de migración sobre una base con datos.
- El perfil normal `prod` usa `spring.jpa.hibernate.ddl-auto=validate`: si hay drift, el backend debe fallar al iniciar para que se corrija el esquema de forma controlada.
- `spring.sql.init.mode=never` en producción: `data.sql` no se ejecuta automáticamente y no debe usarse para inicializar ni resetear una base real. Un reset implica una operación explícita del proveedor sobre una base descartable o respaldada; nunca borres datos de producción para repetir `data.sql`.

### Mercado Pago Sandbox y SMTP

**Webhook Sandbox**

1. En el panel de Mercado Pago Sandbox, registra exactamente `https://<vercel-domain>/api/pagos/webhook` como URL de notificaciones. No agregues una `/` final.
2. Guarda el secreto de firma v1 del proveedor en `MERCADOPAGO_WEBHOOK_SECRET`; no uses el access token como secreto de firma.
3. Define `MERCADOPAGO_ENVIRONMENT=sandbox` y usa el access token/public key Sandbox. Cambiar a `production` requiere credenciales de producción y la URL HTTPS correspondiente.
4. El webhook es autoritativo por consulta al proveedor: para `type=payment`, el backend valida `x-signature`/`x-request-id`, toma `data.id`, consulta el pago en Mercado Pago e ignora el body como fuente de estado. Una notificación firmada de otro tipo se confirma sin mutar el pago; una firma inválida responde `401`.

**SMTP**

Los tres timeouts SMTP son de 5 segundos por defecto (`connectiontimeout`, `timeout`, `writetimeout`). La recuperación y el cambio de contraseña se envían de forma síncrona en el mismo thread mediante listeners no asíncronos `AFTER_COMMIT`; no se encolan. El bean `SyncTaskExecutor` de `PasswordRecoveryAsyncConfiguration` se conserva sólo por compatibilidad y no participa en este flujo. El envío es best effort: un fallo SMTP no revierte la transacción, no hay reintento automático ni outbox durable. Un proveedor lento o caído es un hold externo y debe quedar visible durante la operación.

### Mantenimiento periódico

El endpoint público de operación es `POST /api/internal/maintenance` y se protege con el header exacto `X-Maintenance-Secret`. No espera un payload de negocio; el body puede ser `{}`. Una llamada autorizada responde `200` con:

```json
{"status":"completed"}
```

Plantilla segura (reemplaza sólo los placeholders en tu scheduler; nunca pegues un secreto real en documentación o shell history compartido):

```bash
curl --fail-with-body --silent --show-error \\
  --request POST "https://<vercel-domain>/api/internal/maintenance" \\
  --header "X-Maintenance-Secret: <set-in-vercel>" \\
  --header "Content-Type: application/json" \\
  --data '{}'
```

Configura este POST en un scheduler HTTP externo y gratuito al menos una vez por día. El job ejecuta limpieza de promociones vencidas, refresh tokens y credenciales de recuperación; Vercel puede escalar a cero y no es un lugar confiable para mantener un scheduler JVM activo.

### Limitaciones conocidas de producción

| Limitación | Efecto esperado |
|---|---|
| Scale-to-zero de Vercel | Puede haber cold start; no dependas de estado o tareas en memoria de una instancia. |
| STOMP desactivado | `websocketEnabled=false`; no hay notificaciones WebSocket en producción. |
| Polling de `CLIENTE` y `RESTAURANTE` | El frontend consulta cambios de pedidos cada 15 segundos. |
| Estado de pago pendiente | La vista consulta el pago cada 5 segundos durante como máximo 2 minutos; después requiere volver a Mis Pedidos. |
| `REPARTIDOR` | No hay polling de notificaciones de pedidos como fallback de producción. |
| Scheduling JVM | Los `@Scheduled` están deshabilitados con el perfil `prod`; el mantenimiento externo es obligatorio. |
| Dependencias externas | TiDB, Mercado Pago, SMTP, OpenWeatherMap, Vercel y el scheduler tienen disponibilidad, límites y credenciales propios. |

### Verificación y holds

Ejecuta desde la raíz del repositorio. Estas comprobaciones no sustituyen la validación de servicios externos:

```bash
# Backend: confirmar Java 21 y ejecutar primero el foco DEPLOY-5
java -version
cd mordisco-api
mvn -q -Dtest=ProductionConfigurationValidatorTest,TiDbPersistenceConfigurationTest,SchedulingConfigurationTest,MaintenanceControllerTest,MercadoPagoWebhookControllerTest test

# Empaquetado Maven sin repetir la suite
mvn -q -DskipTests package

# Suite completa: puede quedar en hold si Docker/Testcontainers no está disponible
mvn -q test

# Frontend: tests y build de producción (Angular usa environment.ts)
cd ../mordisco-front
npm test -- --watch=false --browsers=ChromeHeadless
npm run build

# Validaciones estáticas de raíz
cd ..
python3 -m json.tool vercel.json >/dev/null
git diff --check
```

Checklist adicional:

- [ ] El escaneo de secretos sobre los archivos cambiados sólo encuentra placeholders (`<...>`), nunca tokens, claves privadas, contraseñas ni valores reales.
- [ ] `docker compose config` se usa únicamente como validación local del Compose; un pase local no prueba Vercel, TiDB ni la URL pública.
- [ ] Los holds live externos (TiDB/TLS, Vercel/`PORT`, Mercado Pago Sandbox/webhook, SMTP, OpenWeatherMap y scheduler) tienen evidencia operativa separada.
- [ ] Los smoke checks públicos devuelven el resultado esperado: `GET /api/restaurantes` responde, el mantenimiento devuelve `{"status":"completed"}` y el webhook sin firma no se acepta.

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
| **Pagos Integrados** | MercadoPago (tarjetas credito/debito) o pago en efectivo contra entrega |
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
| Docker | Contenedorizacion de MySQL |
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

### Requisitos Previos

- Java 21+
- Node.js 18+ (LTS)
- Maven 3.8+
- MySQL 8.0

### Backend

```bash
# 1. Iniciar MySQL con Docker
cd mordisco-api
docker-compose up -d

# 2. Ejecutar la API (perfil dev)
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

Recovery and password-change email handlers run synchronously on the publishing thread after the database transaction commits (`AFTER_COMMIT`); they are not asynchronous. The `SyncTaskExecutor` bean in `PasswordRecoveryAsyncConfiguration` is compatibility-only and is not part of this delivery path. Delivery is best effort: an SMTP or template failure does not roll back a committed credential or password reset, does not retry automatically, and does not change the generic public response. A process failure between commit and dispatch can lose an email; this implementation is not a durable outbox.

Operational logs for these handlers use fixed aggregate failure text only. Do not add request-level success, suppression, account-state, recipient, exception-detail, credential, or link logging. Mail delivery is the sole boundary where the raw token and recipient address are used.

#### MySQL deployment verification

Normal production startup always uses `spring.jpa.hibernate.ddl-auto=validate` and must fail on schema drift rather than modify the database. The only schema-changing startup is the one-time `prod,schema-bootstrap` profile, which sets `spring.jpa.hibernate.ddl-auto=update`; return to `prod` immediately afterward. Before that bootstrap, confirm that the production database principal is permitted to apply the additive DDL. If runtime DDL is restricted, have an operator create the equivalent additive table and constraints before starting the backend; do not introduce a migration framework solely for this change.

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
