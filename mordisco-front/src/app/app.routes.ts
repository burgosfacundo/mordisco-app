import { Routes } from '@angular/router';
import { Login } from './pages/login/login/login';
import { Registro } from './pages/registro/registro';
import { authGuard } from './core/guards/auth/auth-guard';
import { Profile } from './pages/profile/profile';
import { EditPasswordComponent } from './components/edit-password/edit-password';
import { Home } from './pages/home/home';
import { EditProfile } from './pages/edit-profile/edit-profile';
import { publicOnlyGuard } from './core/guards/public-only-guard';

export const routes: Routes = [
 // Páginas públicas 
  { path: 'login', component: Login, canActivate: [publicOnlyGuard]},
  { path: 'registro', component: Registro, canActivate: [publicOnlyGuard]},

  // Rutas protegidas con authGuard
  {path : '', component: Home, canActivate: [authGuard]},
  {path: 'profile', component: Profile, canActivate: [authGuard]},
  {path: 'profile/edit', component:EditProfile, canActivate: [authGuard]},
  {path: 'edit-password', component : EditPasswordComponent, canActivate: [authGuard]},

  // Rutas con autorización por roles
  {path: 'admin', canMatch: [authGuard], data: { roles: ['ROLE_ADMIN'] } },
  {path: 'owner', canMatch: [authGuard], data: { roles: ['ROLE_RESTAURANTE'] } },
  {path: 'client', canMatch: [authGuard], data: { roles: ['ROLE_CLIENTE'] } },
  {path: '**', redirectTo: ''}  // Ruta comodín para redirigir a la página principal en caso de ruta no encontrada
];
