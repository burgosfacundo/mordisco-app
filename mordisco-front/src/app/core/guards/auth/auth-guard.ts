import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../../../auth/services/auth-service';
import { map, take } from 'rxjs';

export const authGuard: CanActivateFn = (route, _state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const currentUser = authService.getCurrentUser();
  const tokenExpired = authService.isTokenExpired();

  if (!currentUser || tokenExpired) {
    return router.parseUrl('/login');
  }

  const requiredRoles = (route.data && (route.data as any)['roles']) as | string[] | undefined;

  if (Array.isArray(requiredRoles) && requiredRoles.length > 0) {
    const userRole = currentUser.role?.nombre;
    const hasRequiredRole = requiredRoles.includes(userRole);
    if (!hasRequiredRole) {
      return router.parseUrl('/forbidden');
    }
  }

  return true;
};
