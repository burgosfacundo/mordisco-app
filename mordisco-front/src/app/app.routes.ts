import { Routes } from '@angular/router';
import { Login } from './pages/login/login/login';
import { Registro } from './pages/registro/registro';
import { authGuard } from './core/guards/auth/auth-guard';
import { Profile } from './pages/profile/profile';
import { EditProfile } from './components/edit-profile/edit-profile';
import { EditPasswordComponent } from './components/edit-password/edit-password';

export const routes: Routes = [
  // páginas publicas
  { path: 'login', component: Login },
  { path: 'registro', component: Registro },
  {path: 'profile', component : EditProfile},
  {path :'edit-password', component : EditPasswordComponent}

  // página accesible a cualquier usuario logueado
  //{ path: 'perfil', canMatch: [authGuard] },

  // página solo para dueños
  //{ path: 'owner', canMatch: [authGuard], data: { roles: ['ROLE_RESTAURANTE'] } },

  // página solo para administrador
  //{ path: 'admin', canMatch: [authGuard], data: { roles: ['ROLE_ADMIN'] } },

    // página solo para clientes
  //{ path: 'client', canMatch: [authGuard], data: { roles: ['ROLE_CLIENTE'] } },

    // página solo para repartidores
  //{ path: 'delivery', canMatch: [authGuard], data: { roles: ['ROLE_REPARTIDOR'] } },
];
