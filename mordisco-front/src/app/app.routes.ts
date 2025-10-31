import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth/auth-guard';
import { publicOnlyGuard } from './core/guards/public-only-guard';
import { LoginPage } from './features/auth/components/login-page/login-page';
import { RegistroPage } from './features/registro/components/registro-page/registro-page';
import { HomePage } from './features/home/components/home-page/redirect-home-page';
import { ProfilePage } from './features/profile/components/profile-page/profile-page';
import { EditProfilePage } from './features/profile/components/edit-profile-page/edit-profile-page';
import { MyAddressPage } from './features/direccion/components/my-address-page/my-address-page';
import { FormAddressPage } from './features/direccion/components/form-address-page/form-address-page';
import { EditPasswordPage } from './features/profile/components/edit-password-page/edit-password-page';

export const routes: Routes = [
 // Páginas públicas 
  {path : 'home', component: HomePage},
  { path: 'login', component: LoginPage, canActivate: [publicOnlyGuard]},
  { path: 'registro', component: RegistroPage, canActivate: [publicOnlyGuard]},

  // Rutas protegidas con authGuard
  {path: 'profile', component: ProfilePage, canActivate: [authGuard]},
  {path: 'profile/edit', component:EditProfilePage, canActivate: [authGuard]},
  {path: 'profile/my-address', component: MyAddressPage, canActivate : [authGuard]},
  {path: 'profile/my-address/form-address', component: FormAddressPage, canActivate : [authGuard]},
  {path: 'edit-password', component : EditPasswordPage, canActivate: [authGuard]},
 

  // Ruta por defecto
  {path : '', redirectTo: 'home', pathMatch: 'full'},

  // Ruta comodín
  {path: '**', redirectTo: 'home'}
/*
  // Rutas con autorización por roles
  {path: 'admin', canMatch: [authGuard], data: { roles: ['ROLE_ADMIN'] } },
  {path: 'owner', canMatch: [authGuard], data: { roles: ['ROLE_RESTAURANTE'] } },
  {path: 'client', canMatch: [authGuard], data: { roles: ['ROLE_CLIENTE'] } },
  {path: '**', redirectTo: ''}  // Ruta comodín para redirigir a la página principal en caso de ruta no encontrada*/
];
