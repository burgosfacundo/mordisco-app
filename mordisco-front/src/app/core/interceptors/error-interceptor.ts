import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) =>
  next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      const payload = (err?.error ?? {}) as any;

      const normalized = {
        error: payload?.error ?? 'Error',
        message: payload?.message ?? err.message ?? 'Ocurrió un error',
        status: err.status,
      };

      return throwError(() => normalized);
    })
  );
