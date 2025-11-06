import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth/auth-guard';
import { publicOnlyGuard } from './core/guards/public-only-guard';
import { roleGuard } from './core/guards/role/role-guard';
import { LoginPage } from './features/auth/components/login-page/login-page';
import { RegistroPage } from './features/registro/components/registro-page/registro-page';
import { HomePage } from './features/home/components/home-page/redirect-home-page';
import { ProfilePage } from './features/profile/components/profile-page/profile-page';
import { EditProfilePage } from './features/profile/components/edit-profile-page/edit-profile-page';
import { MyAddressPage } from './features/direccion/components/my-address-page/my-address-page';
import { FormAddressPage } from './features/direccion/components/form-address-page/form-address-page';
import { EditPasswordPage } from './features/profile/components/edit-password-page/edit-password-page';
import { MiMenuPage } from './features/mi-restaurante/components/mi-menu-page/mi-menu-page';
import { HorarioPage } from './features/mi-restaurante/components/horario-page/horario-page';
import { HorarioFormPage } from './features/mi-restaurante/components/horario-form-page/horario-form-page';
import { MisPedidosPage } from './features/mis-pedidos/components/mis-pedidos-page/mis-pedidos-page';
import { PromocionFormComponent } from './features/mi-restaurante/components/promocion-form-component/promocion-form-component';
import { MiRestaurantePageComponent } from './features/mi-restaurante/components/mi-restaurante-page/mi-restaurante-page';
import { ProductoFormComponent } from './features/mi-restaurante/components/producto-form-component/producto-form-component';
import { RestauranteFormComponent } from './features/mi-restaurante/components/form-restaurante-component/form-restaurante-component';
import { DetallePedidoPage } from './features/mis-pedidos/components/detalle-pedido-page/detalle-pedido-page';

export const routes: Routes = [
  // Páginas públicas 
  { path: 'home', component: HomePage },
  { path: 'login', component: LoginPage, canActivate: [publicOnlyGuard] },
  { path: 'registro', component: RegistroPage, canActivate: [publicOnlyGuard] },

  // Rutas de perfil (protegidas)
  { path: 'profile', component: ProfilePage, canActivate: [authGuard] },
  { path: 'profile/edit', component: EditProfilePage, canActivate: [authGuard] },
  { path: 'edit-password', component: EditPasswordPage, canActivate: [authGuard] },

  // Rutas de direcciones (protegidas)
  { path: 'my-address', component: MyAddressPage, canActivate: [authGuard] },
  { path: 'my-address/form-address', component: FormAddressPage, canActivate: [authGuard] },

  // Rutas de restaurante (protegidas + role guard)
  { 
    path: 'mi-restaurante', 
    component: MiRestaurantePageComponent, 
    canActivate: [authGuard, roleGuard(['ROLE_RESTAURANTE'])]
  },
  { 
    path: 'restaurante-form', 
    component: RestauranteFormComponent, 
    canActivate: [authGuard, roleGuard(['ROLE_RESTAURANTE'])]
  },
  { 
    path: 'restaurante-form/:id', 
    component: RestauranteFormComponent, 
    canActivate: [authGuard, roleGuard(['ROLE_RESTAURANTE'])]
  },

  // Rutas de menú (protegidas + role guard)
  { 
    path: 'mi-menu', 
    component: MiMenuPage, 
    canActivate: [authGuard, roleGuard(['ROLE_RESTAURANTE'])]
  },
  { 
    path: 'productos/nuevo/:menuId', 
    component: ProductoFormComponent, 
    canActivate: [authGuard, roleGuard(['ROLE_RESTAURANTE'])]
  },
  { 
    path: 'productos/editar/:id', 
    component: ProductoFormComponent, 
    canActivate: [authGuard, roleGuard(['ROLE_RESTAURANTE'])]
  },

  // Rutas de horarios (protegidas + role guard)
  { 
    path: 'horarios', 
    component: HorarioPage, 
    canActivate: [authGuard, roleGuard(['ROLE_RESTAURANTE'])]
  },
  { 
    path: 'horarios/form-horarios', 
    component: HorarioFormPage, 
    canActivate: [authGuard, roleGuard(['ROLE_RESTAURANTE'])]
  },

  // Rutas de promociones (protegidas + role guard)
  { 
    path: 'promocion-form', 
    component: PromocionFormComponent, 
    canActivate: [authGuard, roleGuard(['ROLE_RESTAURANTE'])]
  },
  { 
    path: 'promocion-form/:id', 
    component: PromocionFormComponent, 
    canActivate: [authGuard, roleGuard(['ROLE_RESTAURANTE'])]
  },
  // Rutas de pedidos(protegidas + role guard)
  {
  path: 'pedidos',
  component: MisPedidosPage,
  canActivate: [authGuard, roleGuard(['ROLE_RESTAURANTE'])]
  },

  //Rutas de pedido(protegidas)
  {
    path: 'pedidos/detalle/:id',
    component: DetallePedidoPage,
    canActivate: [authGuard]
  },

  // Rutas de pedidos
  { 
    path: 'pedidos', 
    component: MisPedidosPage, 
    canActivate: [authGuard]
  },

  // Ruta por defecto
  { path: '', redirectTo: 'home', pathMatch: 'full' },

  // Ruta comodín
  { path: '**', redirectTo: 'home' }
];