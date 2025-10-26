import { HttpInterceptorFn } from '@angular/common/http';
import { catchError, switchMap, throwError } from 'rxjs';
import { inject } from '@angular/core';
import { AuthService } from '../../auth/services/auth-service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService  = inject(AuthService)
// No agregar token a endpoints públicos
  if (req.url.includes('/api/auth/login') || 
      req.url.includes('/api/auth/refresh')) {
    return next(req)
  }

  const token = authService.getAccessToken()
  
  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      },
      withCredentials: true // CRÍTICO: envía cookies httpOnly
    })
  }

  return next(req).pipe(
    catchError(error => {
      // Si es 401, intentar renovar el token
      if (error.status === 401 && !req.url.includes('/api/auth/refresh')) {
        return authService.refreshToken().pipe(
          switchMap(response => {
            // Reintentar request original con nuevo token
            const clonedReq = req.clone({
              setHeaders: {
                Authorization: `Bearer ${response.accessToken}`
              }
            })
            return next(clonedReq)
          }),
          catchError(refreshError => {
            // Refresh falló - logout y redirigir a login
            authService.logout()
            return throwError(() => refreshError)
          })
        );
      }
      return throwError(() => error)
    })
  )
}