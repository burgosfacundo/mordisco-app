import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../../../auth/services/auth-service';

export const authGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isAuthenticated()) {
    return router.parseUrl('/login');
  }

  const requiredRoles = route.data?.['roles'] as string[] | undefined;
  if (requiredRoles?.length) {
    const userRole = auth.currentUserValue?.role?.nombre;
    if (!userRole || !requiredRoles.includes(userRole)) {
      return router.parseUrl('/forbidden');
    }
  }
  return true;
};