# Mordisco - Plataforma de Delivery de Comida

<div align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Angular-20-DD0031?style=for-the-badge&logo=angular&logoColor=white" alt="Angular"/>
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL"/>
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

## Tabla de Contenidos

- [Descripcion del Proyecto](#descripcion-del-proyecto)
- [Arquitectura del Sistema](#arquitectura-del-sistema)
- [Funcionalidades por Rol](#funcionalidades-por-rol)
  - [Cliente](#-cliente)
  - [Restaurante](#-restaurante)
  - [Repartidor](#-repartidor)
  - [Administrador](#-administrador)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Modelo de Datos](#modelo-de-datos)
- [Instalacion y Ejecucion](#instalacion-y-ejecucion)
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
- Node.js (LTS)
- Docker y Docker Compose
- Maven 3.8+

### Backend

```bash
# 1. Iniciar MySQL con Docker
cd mordisco-api
docker-compose up -d

# 2. Ejecutar la API (perfil dev)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# La API estara disponible en http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### Frontend

```bash
# 1. Instalar dependencias
cd mordisco-front
npm install

# 2. Iniciar servidor de desarrollo
npm start

# La aplicacion estara disponible en http://localhost:4200
```

### Configuracion

**Backend** (`application-dev.properties`):
- Base de datos: `mordisco` (creada automaticamente)
- Puerto: 8080
- CORS habilitado para localhost:4200

**Frontend**:
- Proxy configurado para redirigir `/api` al backend
- Environment files en `src/environments/`

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
