import { inject } from '@angular/core';
import { CanActivateFn, } from '@angular/router';
import { AuthService } from '../../shared/services/auth-service';

/**
 * Guard para rutas públicas (login, registro)
 * Redirige a home si el usuario ya está autenticado
 */
export const publicOnlyGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  
  const isAuthenticated = authService.isAuthenticated();
  
  if (isAuthenticated) {
    return false;
  }

  return true;
};