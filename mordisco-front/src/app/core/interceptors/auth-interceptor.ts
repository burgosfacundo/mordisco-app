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
          switchMap(response => {
            // Retry once with the access token returned by this refresh operation.
            const retryReq = req.clone({
              setHeaders: {
                Authorization: `Bearer ${response.accessToken}`
              }
            });
            return next(retryReq);
          })
        );
      }
      return throwError(() => error);
    })
  );
};