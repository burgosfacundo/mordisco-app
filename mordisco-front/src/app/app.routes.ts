import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth/auth-guard';
import { publicOnlyGuard } from './core/guards/public-only-guard';
import { roleGuard } from './core/guards/role/role-guard';
import { HomePage } from './features/home/components/home-page/redirect-home-page';
import { LoginPage } from './features/auth/components/login-page/login-page';
import { RegistroPage } from './features/registro/components/registro-page/registro-page';
import { ProfilePage } from './features/profile/components/profile-page/profile-page';
import { EditProfilePage } from './features/profile/components/edit-profile-page/edit-profile-page';
import { EditPasswordPage } from './features/profile/components/edit-password-page/edit-password-page';
import { MyAddressPage } from './features/direccion/components/my-address-page/my-address-page';
import { MiRestaurantePageComponent } from './features/mi-restaurante/components/mi-restaurante-page/mi-restaurante-page';
import { RestauranteFormComponent } from './features/mi-restaurante/components/form-restaurante-component/form-restaurante-component';
import { MiMenuPage } from './features/mi-restaurante/components/mi-menu-page/mi-menu-page';
import { ProductoFormComponent } from './features/mi-restaurante/components/producto-form-component/producto-form-component';
import { HorarioPage } from './features/mi-restaurante/components/horario-page/horario-page';
import { HorarioFormPage } from './features/mi-restaurante/components/horario-form-page/horario-form-page';
import { PromocionFormComponent } from './features/mi-restaurante/components/promocion-form-component/promocion-form-component';
import { MisPedidosPage } from './features/mis-pedidos/components/mis-pedidos-page/mis-pedidos-page';
import { DetallePedidoPage } from './features/mis-pedidos/components/detalle-pedido-page/detalle-pedido-page';
import { CarritoPage } from './features/carrito/components/carrito-page/carrito-page';
import { CheckoutPage } from './features/carrito/components/checkout-page/checkout-page';
import { PagoExitosoPage } from './features/carrito/components/pago-exitoso-page/pago-exitoso-page';
import { PagoFallidoPage } from './features/carrito/components/pago-fallido-page/pago-fallido-page';
import { RestauranteDetallePage } from './features/menu/detalle-restaurante/detalle-restaurante';
import { MisPedidosClientePage } from './features/mis-pedidos/components/mis-pedidos-cliente-page/mis-pedidos-cliente-page';
import { RecoverPasswordPage } from './features/auth/components/recover-password-page/recover-password-page';
import { ResetPasswordPage } from './features/auth/components/reset-password-page/reset-password-page';
import { RestauranteDireccionPage } from './features/mi-restaurante/components/restaurante-direccion-page/restaurante-direccion-page';
import { CalificacionFormPage } from './features/calificacion/calificacion-form-page/calificacion-form-page';
import { EntregasPage } from './features/entregas/entregas-page/entregas-page';
import { ConfiguracionFormPage } from './features/configuracion-sistema/configuracion-form-page/configuracion-form-page';
import { ConfiguracionPage } from './features/configuracion-sistema/configuracion-page/configuracion-page';
import { DetalleUsuarioPage } from './features/admin/detalle-usuario-page/detalle-usuario-page';

export const routes: Routes = [
  // ==========================================
  // RUTAS PÚBLICAS
  // ==========================================
  { 
    path: '', 
    redirectTo: 'home', 
    pathMatch: 'full' 
  },
  { 
    path: 'home', 
    component: HomePage 
  },
  { 
    path: 'login', 
    component: LoginPage, 
    canActivate: [publicOnlyGuard] 
  },
  { 
    path: 'registro', 
    component: RegistroPage, 
    canActivate: [publicOnlyGuard] 
  },
  {
    path: 'recover-password',
    component: RecoverPasswordPage,
    canActivate: [publicOnlyGuard]
  },
  {
    path: 'reset-password',
    component: ResetPasswordPage,
    canActivate: [publicOnlyGuard]
  },

  // ==========================================
  // RUTAS DE PERFIL (Protegidas)
  // ==========================================
  { 
    path: 'profile', 
    component: ProfilePage, 
    canActivate: [authGuard] 
  },
  { 
    path: 'profile/edit', 
    component: EditProfilePage, 
    canActivate: [authGuard] 
  },
  { 
    path: 'edit-password', 
    component: EditPasswordPage, 
    canActivate: [authGuard] 
  },
  
  // ==========================================
  // (Role: RESTAURANTE)
  // ==========================================
  {
    path: 'restaurante',
    canActivate: [authGuard, roleGuard(['ROLE_RESTAURANTE'])],
    children: [
      { path: '', component: MiRestaurantePageComponent },
      { path: 'editar/:id', component: RestauranteFormComponent },
      { path: 'crear', component: RestauranteFormComponent },
      { path: 'direccion', component: RestauranteDireccionPage },
      { path: 'menu', component: MiMenuPage },
      { path: 'menu/producto/nuevo/:menuId', component: ProductoFormComponent },
      { path: 'menu/producto/editar/:id', component: ProductoFormComponent },
      { path: 'horarios', component: HorarioPage },
      { path: 'horarios/nuevo', component: HorarioFormPage },
      { path: 'horarios/editar/:id', component: HorarioFormPage },
      { path: 'promociones', component: PromocionFormComponent },
      { path: 'promociones/nueva/:idRestaurante', component: PromocionFormComponent },
      { path: 'promociones/editar/:id', component: PromocionFormComponent },
      { path: 'pedidos', component: MisPedidosPage },
      { path: 'pedidos/detalle/:id', component: DetallePedidoPage }
    ]
  },

  // ==========================================
  // (Role: CLIENTE)
  // ==========================================
  {
    path: 'cliente',
    canActivate: [authGuard, roleGuard(['ROLE_CLIENTE'])],
    children: [
      { path: 'my-address',component: MyAddressPage },
      // Ver restaurante y menú
      { path: 'restaurante/:id', component: RestauranteDetallePage },
      // Carrito y checkout
      { path: 'carrito', component: CarritoPage },
      { path: 'checkout', component: CheckoutPage },
      { path: 'calificar/:var/:id', component : CalificacionFormPage},
      { path: 'calificar/:var/:id', component : CalificacionFormPage},

      // Confirmaciones de pago
      { path: 'pedidos/pago-exitoso', component: PagoExitosoPage },
      { path: 'pedidos/pago-fallido', component: PagoFallidoPage },
      { path: 'pedidos/pago-pendiente', component: PagoExitosoPage },
      
      // Mis pedidos
      { path: 'pedidos', component: MisPedidosClientePage },
      { path: 'pedidos/detalle/:id', component: DetallePedidoPage }
    ]
  },

  // ==========================================
  // (Role: REPARTIDOR)
  // ==========================================
  {
    path: 'repartidor',
    canActivate: [authGuard, roleGuard(['ROLE_REPARTIDOR'])],
    children: [
      { path : 'pedidos/historial', component : EntregasPage},
      { path : 'pedidos/detalle/:id', component: DetallePedidoPage}
    ]
  },

  // ==========================================
  // (Role: ADMIN)
  // ==========================================
  {
    path: 'admin',
    canActivate: [authGuard, roleGuard(['ROLE_ADMIN'])],
    children: [
      {path : 'configuracion', component : ConfiguracionPage},
      {path : 'configuracion/edit', component :ConfiguracionFormPage},
      {path : 'detalle-usuario/:idRol/:idUser', component : DetalleUsuarioPage},
    ]
  },

  // ==========================================
  // RUTA 404
  // ==========================================
  { 
    path: '**', 
    redirectTo: 'home' 
  }
];