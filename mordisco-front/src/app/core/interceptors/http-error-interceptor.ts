import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError } from 'rxjs/operators';
import { ErrorHandlerService } from '../services/error-handler-service';
import { ToastService } from '../services/toast-service';

/**
 * Interceptor HTTP para manejo centralizado de TODOS los errores HTTP
 * 
 * MANEJA AUTOMÁTICAMENTE:
 * - Errores de red (sin conexión) - status 0
 * - Errores de servidor (500+)
 * - Sesión expirada (401) en rutas protegidas
 * - Errores de validación (400, 422) - muestra mensaje del backend
 * - Conflictos (409) - muestra mensaje del backend
 * - No encontrado (404) - muestra mensaje del backend
 * 
 * EXCEPCIONES (URLs sensibles que NO maneja automáticamente):
 * - /usuarios/recover-password - Por seguridad (evitar User Enumeration)
 * - /auth/login - El componente maneja el error específicamente
 * - /auth/register - El componente maneja el error específicamente
 * 
 * Los componentes ya NO necesitan manejar errores manualmente,
 * solo suscribirse y el mensaje del backend se mostrará automáticamente.
 */
export const httpErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const errorHandler = inject(ErrorHandlerService);
  const toastService = inject(ToastService);

  // URLs sensibles que el interceptor NO debe manejar automáticamente
  const sensitiveUrls = [
    '/usuarios/recover-password',  // Evitar User Enumeration
  ];

  // Verificar si la URL actual es sensible
  const isSensitiveUrl = sensitiveUrls.some(url => req.url.includes(url));

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // Si es una URL sensible, NO mostrar el error automáticamente
      if (isSensitiveUrl) {
        return errorHandler.handle(error);
      }

      // Procesar el error con el servicio centralizado
      // Esto extrae automáticamente el mensaje del backend
      const errorContext = errorHandler.handleHttpError(error);
      
      // Mostrar notificación al usuario con el mensaje del backend
      if (errorContext.showToUser) {
        toastService.showFromError(errorContext);
      }

      // Re-lanzar el error para que los componentes puedan manejarlo si necesitan lógica adicional
      return errorHandler.handle(error);
    })
  );
};
