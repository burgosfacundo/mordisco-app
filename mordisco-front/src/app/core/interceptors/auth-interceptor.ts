import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../auth/services/auth-service';
import { SKIP_AUTH } from '../context/auth-context';
import { environment } from '../../../environments/environment';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const auth  = inject(AuthService);

  if (req.context.get(SKIP_AUTH)) {
    return next(req);
  }

  const isApiCall = req.url.startsWith(environment.apiUrl);
  if (!isApiCall) {
    return next(req);
  }

  const token = auth.getToken();
  if (!token || auth.isTokenExpired()) {
    auth.logout();                  
    return next(req);
  }

  const authReq = req.clone({
    setHeaders: { Authorization: `Bearer ${token}` }
  });

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        auth.logout();
        router.navigate(['/login']);
      } else if (error.status === 403) {
        router.navigate(['/forbidden']);
      }
      return throwError(() => error);
    })
  );
};