import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  
  // Rutas que no requieren token
  const excludedRoutes = ['/login', '/register'];
  const shouldExclude = excludedRoutes.some(route => req.url.includes(route));
  
  // Si la ruta está excluida, continuar sin modificar
  if (shouldExclude) {
    return next(req);
  }

  // Obtener token del localStorage
  const token = localStorage.getItem('accessToken');
  
  // Si no hay token, continuar sin modificar
  if (!token) {
    return next(req);
  }

  // Clonar la request y agregar el header de autorización
  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  // Continuar con la request modificada y manejar errores
  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Si el error es 401 (No autorizado), el token expiró
      if (error.status === 401) {
        // Eliminar token expirado
        localStorage.removeItem('accessToken');
        
        // Redirigir al login
        router.navigate(['/login']);
      }
      
      // Re-lanzar el error para que otros interceptors o servicios lo manejen
      return throwError(() => error);
    })
  );
};
