import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth/auth-guard';
import { Profile } from './pages/profile/profile';
import { EditPasswordComponent } from './components/edit-password/edit-password';
import { EditProfile } from './pages/edit-profile/edit-profile';
import { MyAddress } from './pages/my-address/my-address';
import { FormAddress } from './pages/form-address/form-address';
import { Home } from './pages/home/redirect-home';
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
