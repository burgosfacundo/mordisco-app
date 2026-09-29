import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../shared/services/auth-service';

const PUBLIC_AUTH_PATHS = new Set([
  '/auth/login',
  '/auth/register',
  '/auth/refresh',
  '/auth/logout',
  '/auth/recover-password',
  '/auth/reset-password',
  '/auth/csrf'
]);

function getApiRelativePath(requestUrl: string): string | null {
  try {
    const apiUrl = new URL(environment.apiUrl);
    const request = new URL(requestUrl, document.baseURI);
    const hasUserInfo = request.username !== '' || request.password !== '' ||
      /^(?:[a-z][a-z\d+.-]*:)?\/\/[^/?#]*@/i.test(requestUrl.trim());
    const apiPath = apiUrl.pathname.replace(/\/+$/, '');
    const isApiPath = apiPath === ''
      ? request.pathname.startsWith('/')
      : request.pathname === apiPath || request.pathname.startsWith(`${apiPath}/`);

    if (
      request.origin !== apiUrl.origin ||
      hasUserInfo ||
      !isApiPath
    ) {
      return null;
    }

    return request.pathname.slice(apiPath.length);
  } catch {
    return null;
  }
}

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const apiRelativePath = getApiRelativePath(req.urlWithParams);
  if (apiRelativePath === null || PUBLIC_AUTH_PATHS.has(apiRelativePath)) {
    return next(req);
  }

  const authService = inject(AuthService);
  const sessionGeneration = authService.getSessionGeneration();
  const token = sessionStorage.getItem('access_token');
  if (!token) {
    return next(req);
  }

  const clonedReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  return next(clonedReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (
        error.status === 401 &&
        authService.isAuthenticated() &&
        sessionGeneration === authService.getSessionGeneration()
      ) {
        return authService.refreshToken().pipe(
          switchMap(response => {
            if (sessionGeneration !== authService.getSessionGeneration()) {
              return throwError(() => error);
            }

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