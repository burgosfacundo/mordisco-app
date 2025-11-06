import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../../../shared/services/auth-service';

export const roleGuard = (allowedRoles: string[]): CanActivateFn => {
  return (route: ActivatedRouteSnapshot) => {
    const authService = inject(AuthService);
    const router = inject(Router);
    
    const user = authService.currentUser();
    
    // Si no hay usuario autenticado, redirigir a login
    if (!user) {
      router.navigate(['/login']);
      return false;
    }
    
    // Verificar si el rol del usuario está en la lista de roles permitidos
    if (user.role && allowedRoles.includes(user.role)) {
      return true;
    }
    
    // Si no tiene el rol necesario, redirigir a home
    router.navigate(['/home']);
    return false;
  };
};