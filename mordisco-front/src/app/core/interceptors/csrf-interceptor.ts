import {
  HttpBackend,
  HttpClient,
  HttpErrorResponse,
  HttpInterceptorFn
} from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { catchError, finalize, map, Observable, of, shareReplay, switchMap, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';

interface CsrfToken {
  value: string;
}

interface CsrfBootstrapResponse {
  token: unknown;
}

const PROTECTED_AUTH_PATHS = new Set([
  '/auth/login',
  '/auth/refresh',
  '/auth/logout'
]);

function csrfEndpoint(): string {
  const apiUrl = new URL(environment.apiUrl);
  const apiPath = apiUrl.pathname.replace(/\/+$/, '');
  return new URL(`${apiPath}/auth/csrf`, apiUrl.origin).toString();
}

function isProtectedAuthRequest(method: string, requestUrl: string): boolean {
  if (method !== 'POST') {
    return false;
  }

  try {
    const apiUrl = new URL(environment.apiUrl);
    const request = new URL(requestUrl, document.baseURI);
    const apiPath = apiUrl.pathname.replace(/\/+$/, '');
    const isApiPath = apiPath === ''
      ? request.pathname.startsWith('/')
      : request.pathname === apiPath || request.pathname.startsWith(`${apiPath}/`);

    if (!isApiPath) {
      return false;
    }

    const authPath = request.pathname.slice(apiPath.length);
    return request.origin === apiUrl.origin &&
      !request.username &&
      !request.password &&
      PROTECTED_AUTH_PATHS.has(authPath);
  } catch {
    return false;
  }
}

@Injectable({ providedIn: 'root' })
export class CsrfTokenService {
  // Use HttpBackend directly so the bootstrap cannot recurse through auth or error interceptors.
  private readonly bootstrapHttp = new HttpClient(inject(HttpBackend));
  private readonly bootstrapUrl = csrfEndpoint();
  private token?: CsrfToken;
  private bootstrapRequest$?: Observable<CsrfToken>;

  getToken(): Observable<CsrfToken> {
    if (this.token) {
      return of(this.token);
    }
    if (this.bootstrapRequest$) {
      return this.bootstrapRequest$;
    }

    let bootstrapRequest$: Observable<CsrfToken>;
    bootstrapRequest$ = this.bootstrapHttp.get<CsrfBootstrapResponse>(this.bootstrapUrl, {
      withCredentials: true
    }).pipe(
      map(response => {
        if (typeof response?.token !== 'string' || response.token.length === 0) {
          throw new Error('CSRF bootstrap returned no token');
        }

        const token = { value: response.token };
        this.token = token;
        return token;
      }),
      finalize(() => {
        if (this.bootstrapRequest$ === bootstrapRequest$) {
          this.bootstrapRequest$ = undefined;
        }
      }),
      shareReplay({ bufferSize: 1, refCount: false })
    );
    this.bootstrapRequest$ = bootstrapRequest$;
    return bootstrapRequest$;
  }

  invalidate(token: CsrfToken): void {
    // An older 403 must not discard a token obtained by a newer bootstrap.
    if (this.token === token) {
      this.token = undefined;
    }
  }
}

export const csrfInterceptor: HttpInterceptorFn = (request, next) => {
  if (!isProtectedAuthRequest(request.method, request.urlWithParams)) {
    return next(request);
  }

  const csrfTokenService = inject(CsrfTokenService);
  return csrfTokenService.getToken().pipe(
    switchMap(token => next(request.clone({
      withCredentials: true,
      setHeaders: { 'X-XSRF-TOKEN': token.value }
    })).pipe(
      catchError((error: unknown) => {
        if (error instanceof HttpErrorResponse && error.status === 403) {
          csrfTokenService.invalidate(token);
        }
        return throwError(() => error);
      })
    ))
  );
};
