# 🍔 Mordisco - Plataforma de Delivery

> Plataforma web integral que conecta restaurantes, clientes y repartidores para facilitar pedidos de comida online.

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-20-red.svg)](https://angular.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)

---

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Funcionalidades Actuales](#-funcionalidades-actuales)
- [Funcionalidades Futuras (Tesis)](#-funcionalidades-futuras-tesis)
- [Tecnologías](#-tecnologías)
- [Usuarios de Prueba](#-usuarios-de-prueba)
- [API Endpoints](#-api-endpoints)
- [Autores](#-autores)

---

## 📖 Descripción

**Mordisco** es una plataforma web de delivery que permite a los usuarios realizar pedidos de comida desde restaurantes locales. El sistema cuenta con cuatro roles principales: **Clientes**, **Restaurantes** , **Repartidores** y **Administradores**, cada uno con funcionalidades específicas.

### Objetivo del Proyecto

Desarrollar una solución completa y escalable que optimice el proceso de pedidos online, mejorando la experiencia tanto para clientes como para restaurantes, con un enfoque en la usabilidad, seguridad y rendimiento.

---

## ✅ Funcionalidades Actuales

### 🛍️ Panel de Cliente

- ✅ **Registro e Inicio de Sesión**
  - Autenticación JWT con refresh tokens
  - Gestión de perfil (nombre, apellido, teléfono)
  - Cambio de contraseña seguro

- ✅ **Gestión de Direcciones**
  - CRUD completo de direcciones de entrega
  - Validación de campos obligatorios

- ✅ **Exploración de Restaurantes**
  - Filtrado por ciudad
  - Búsqueda por nombre de restaurante
  - Visualización de horarios de atención en tiempo real
  - Sistema de calificaciones (estrellas)
  - Badges de estado (Abierto/Cerrado)

- ✅ **Visualización de Promociones**
  - Sección destacada de restaurantes con promociones activas
  - Descuentos visibles por porcentaje

- ✅ **Historial de Pedidos**
  - Visualización de pedidos realizados
  - Estados: Pendiente, En Proceso, En Camino, Recibido, Cancelado
  - Detalles completos de cada pedido

### 🍽️ Panel de Restaurante

- ✅ **Dashboard Principal**
  - Resumen de pedidos pendientes

- ✅ **Gestión de Perfil del Restaurante**
  - Edición de razón social
  - Actualización de logo (imagen)
  - Estado activo/inactivo

- ✅ **Gestión de Menú**
  - Crear/editar nombre del menú
  - CRUD completo de productos
  - Cada producto incluye: nombre, descripción, precio, disponibilidad, imagen

- ✅ **Gestión de Horarios de Atención**
  - CRUD de horarios por día de la semana
  - Horarios de apertura y cierre
  - Validación de rangos horarios

- ✅ **Gestión de Promociones**
  - CRUD completo de promociones
  - Configuración de: descripción, descuento (%), fecha inicio/fin

- ✅ **Gestión de Pedidos**
  - Visualización de pedidos pendientes
  - Cambio de estado: Pendiente → En Proceso → En Camino
  - Filtrado por estado

### 🔒 Seguridad

- ✅ **Autenticación y Autorización**
  - JWT (Access Token) con expiración de 15 minutos
  - Refresh Token con expiración de 7 días (httpOnly cookie)
  - Guards de Angular para protección de rutas
  - Interceptor HTTP para inyección automática de tokens
  - Role-based access control (RBAC)

- ✅ **Validaciones**
  - Validaciones frontend con Angular Reactive Forms
  - Validaciones backend con Bean Validation (@Valid)
  - Mensajes de error personalizados

### 🎨 Interfaz de Usuario

- ✅ **Diseño Moderno**
  - Tailwind CSS v4 con tema personalizado
  - Componentes reutilizables
  - Responsive design (mobile-first)
  - Animaciones y transiciones suaves

- ✅ **UX Optimizada**
  - Navegación intuitiva con navbar dinámica según rol
  - Búsqueda en tiempo real con debounce
  - Loading states y spinners
  - Empty states informativos
  - Mensajes de confirmación (SnackBar de Material)

---

## 🚀 Funcionalidades Futuras (Tesis)

### Fase 1: Mejoras en el Sistema de Pedidos

- 🔄 **Sistema de Calificaciones Completo**
  - Permitir que clientes califiquen restaurantes después de recibir pedido
  - Comentarios opcionales
  - Promedio de calificación en tiempo real
 
### Fase 2: Modulo de Administrador

- 🔄 Dashboard de Administración
  - Vista general del sistema
  - Estadísticas globales (restaurantes, usuarios, pedidos)
  - Cards con métricas rápidas


- 🔄 Gestión de Restaurantes
  - Listado completo con paginación
  - Visualización de información detallada
  - Filtros y búsqueda


- 🔄 Gestión de Usuarios
  - Listado completo con roles (Cliente, Restaurante, Admin)
  - Visualización de perfiles
  - Badges de rol diferenciados por color


- 🔄 Gestión de Pedidos Global
  - Vista de todos los pedidos del sistema
  - Filtrado por estado y restaurante
  - Paginación

### Fase 3: Integración de Pagos

- 🔄 **Integración con MercadoPago**
  - Pagos online con tarjeta de crédito/débito
  - Generación de preference_id
  - Webhooks para confirmación de pago
  - Estados de pago (Pendiente, Aprobado, Rechazado)

- 🔄 **Pago en Efectivo**
  - Opción de pago contra entrega
  - Confirmación manual por restaurante

### Fase 4: Módulo de Repartidores

- 🔄 **Rol de Repartidor**
  - Registro e inicio de sesión
  - Panel de repartidor con pedidos disponibles

- 🔄 **Asignación de Pedidos**
  - Listado de pedidos "En Camino"
  - Aceptación manual de entregas
  - Estado: Asignado, En Ruta, Entregado

- 🔄 **Tracking en Tiempo Real**
  - Notificación al cliente del estado de entrega

### Fase 5: Notificaciones en Tiempo Real

- 🔄 **WebSockets con Spring Boot**
  - Notificaciones push cuando:
    - Restaurante acepta/rechaza pedido
    - Pedido cambia de estado
    - Repartidor acepta entrega

- 🔄 **Notificaciones por Email**
  - Confirmación de pedido
  - Cambios de estado importantes


## 🛠️ Tecnologías

### Backend

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Java | 17+ | Lenguaje base |
| Spring Boot | 3.x | Framework principal |
| Spring Security | 6.x | Autenticación y autorización |
| Spring Data JPA | 3.x | ORM y persistencia |
| MySQL | 8.0 | Base de datos relacional |
| JWT (JJWT) | 0.12.x | Tokens de autenticación |
| Lombok | 1.18.x | Reducción de boilerplate |
| Bean Validation | 3.x | Validaciones |

### Frontend

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Angular | 20 | Framework SPA |
| TypeScript | 5.x | Lenguaje tipado |
| Tailwind CSS | 4 | Estilos utility-first |
| Angular Material | 18.x | Componentes UI |
| RxJS | 7.x | Programación reactiva |
| Angular Router | 20 | Navegación SPA |
| Angular Forms | 20 | Formularios reactivos |

### Herramientas

- **Git** - Control de versiones
- **Maven** - Gestión de dependencias (Backend)
- **npm** - Gestión de paquetes (Frontend)
- **Postman** - Testing de API
- **MySQL Workbench** - Administración de BD

---

## 👨‍💻 Autores

- **Facundo Burgos** - *Desarrollo Full Stack* - [GitHub](https://github.com/burgosfacundo)
- **Micaela Mandes** - *Desarrollo Full Stack* - [GitHub](https://github.com/micamandes9)
- **Luana Mena** - *Desarrollo Full Stack* - [GitHub](https://github.com/luanamena2004)
---

<div align="center">
  <p>Desarrollado con ❤️ para la Tesis de Grado</p>
  <p><strong>Universidad Tecnológica Nacional (UTN)</strong></p>
  <p>2025</p>
</div>
