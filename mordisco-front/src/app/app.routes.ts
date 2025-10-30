import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth/auth-guard';
import { Profile } from './features/profile/components/profile-page/profile-page';
import { EditPasswordComponent } from './features/profile/components/edit-password-component/edit-password-component';
import { EditProfile } from './features/profile/components/edit-profile-page/edit-profile-page';
import { MyAddress } from './features/direccion/components/my-address-page/my-address-page';
import { FormAddress } from './features/direccion/components/form-address-page/form-address-page';
import { Home } from './features/home/components/home-page/redirect-home-page';
import { publicOnlyGuard } from './core/guards/public-only-guard';
import { LoginPage } from './features/auth/components/login-page/login-page';
import { RegistroPage } from './features/registro/components/registro-page/registro-page';

export const routes: Routes = [
 // Páginas públicas 
  {path : 'home', component: Home},
  { path: 'login', component: LoginPage, canActivate: [publicOnlyGuard]},
  { path: 'registro', component: RegistroPage, canActivate: [publicOnlyGuard]},

  // Rutas protegidas con authGuard
  {path: 'profile', component: Profile, canActivate: [authGuard]},
  {path: 'profile/edit', component:EditProfile, canActivate: [authGuard]},
  {path: 'profile/my-address', component: MyAddress, canActivate : [authGuard]},
  {path: 'profile/my-address/form-address', component: FormAddress, canActivate : [authGuard]},
  {path: 'edit-password', component : EditPasswordComponent, canActivate: [authGuard]},
 

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
