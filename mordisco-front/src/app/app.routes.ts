import { Routes } from '@angular/router';
import { LoginFormComponent } from './components/login-form/login-form-component';
import { Login } from './pages/login/login/login';
import { Registro } from './pages/registro/registro';

export const routes: Routes = [
    {path:'login', component : Login},
    {path:'registro', component: Registro}
];
