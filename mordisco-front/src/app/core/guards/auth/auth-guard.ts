import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../../../shared/services/auth-service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  const isAuthenticated = authService.isAuthenticated();
  
  if (!isAuthenticated) {
    router.navigate(['/login'], {
      queryParams: { 
        returnUrl: state.url
      }
    });
    return false;
  }

  return true;
};