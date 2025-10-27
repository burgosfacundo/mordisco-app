import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { map, catchError, of } from 'rxjs';
import { AuthService } from '../../../auth/services/auth-service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Si ya está autenticado, permitir acceso
  if (authService.isAuthenticated()) {
    return true;
  }

  // Si no, intentar refrescar el token
  return authService.refreshToken().pipe(
    map(() => true),
    catchError(() => {
      // Si falla el refresh, redirigir a login con returnUrl
      router.navigate(['/login'], { 
        queryParams: { returnUrl: state.url } 
      });
      return of(false);
    })
  );
};