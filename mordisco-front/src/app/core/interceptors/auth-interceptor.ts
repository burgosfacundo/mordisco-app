import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../../shared/services/auth-service';


export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = sessionStorage.getItem('access_token');
  
  // Lista de endpoints que NO requieren autenticación
  const publicEndpoints = [
    '/auth/login',
    '/auth/register',
    '/auth/refresh',
    '/auth/logout',
    '/auth/recover-password',
    '/auth/reset-password'
  ];
  
  const isPublicEndpoint = publicEndpoints.some(endpoint => req.url.includes(endpoint));
  
  // Si es endpoint público o no hay token, continuar sin modificar
  if (isPublicEndpoint || !token) {
    return next(req);
  }

  // Clonar request y agregar token
  const clonedReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  // Ejecutar request y manejar errores 401
  return next(clonedReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Si es 401 (Unauthorized), intentar refresh token
      if (error.status === 401 && !req.url.includes('/auth/refresh')) {
        return authService.refreshToken().pipe(
          switchMap(() => {
            // Después del refresh exitoso, reintentar la request original con el nuevo token
            const newToken = sessionStorage.getItem('access_token');
            const retryReq = req.clone({
              setHeaders: {
                Authorization: `Bearer ${newToken}`
              }
            });
            return next(retryReq);
          }),
          catchError((refreshError) => {
            // Si el refresh falla, limpiar auth y redirigir a login
            authService.clearAuthAndRedirect();
            return throwError(() => refreshError);
          })
        );
      }
      return throwError(() => error);
    })
  );
};