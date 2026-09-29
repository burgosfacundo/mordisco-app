import { HttpClient, HttpErrorResponse, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { authInterceptor } from './auth-interceptor';
import { csrfInterceptor } from './csrf-interceptor';
import { AuthService } from '../../shared/services/auth-service';
import { CarritoService } from '../../shared/services/carrito/carrito-service';
import { ErrorHandlerService } from '../services/error-handler-service';
import { ToastService } from '../services/toast-service';
import { httpErrorInterceptor } from './http-error-interceptor';
import { throwError } from 'rxjs';

describe('csrfInterceptor', () => {
  let http: HttpClient;
  let httpTestingController: HttpTestingController;
  let router: jasmine.SpyObj<Router>;
  let carritoService: jasmine.SpyObj<CarritoService>;
  let errorHandler: jasmine.SpyObj<ErrorHandlerService>;
  let toastService: jasmine.SpyObj<ToastService>;

  const csrfUrl = `${environment.apiUrl}/auth/csrf`;

  beforeEach(() => {
    sessionStorage.clear();
    localStorage.clear();
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);
    router.navigate.and.returnValue(Promise.resolve(true));
    carritoService = jasmine.createSpyObj<CarritoService>('CarritoService', ['vaciarCarrito']);
    errorHandler = jasmine.createSpyObj<ErrorHandlerService>('ErrorHandlerService', [
      'handleHttpError',
      'handle'
    ]);
    errorHandler.handleHttpError.and.callFake((error: HttpErrorResponse) => ({
      type: 'http',
      severity: 'warning',
      userMessage: 'Forbidden',
      technicalMessage: 'Forbidden',
      showToUser: false,
      statusCode: error.status
    }));
    errorHandler.handle.and.callFake((error: HttpErrorResponse) =>
      throwError(() => ({ statusCode: error.status, message: 'Forbidden' }))
    );
    toastService = jasmine.createSpyObj<ToastService>('ToastService', ['showFromError']);

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor, httpErrorInterceptor, csrfInterceptor])),
        provideHttpClientTesting(),
        { provide: Router, useValue: router },
        { provide: CarritoService, useValue: carritoService },
        { provide: ErrorHandlerService, useValue: errorHandler },
        { provide: ToastService, useValue: toastService }
      ]
    });

    http = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    TestBed.inject(AuthService).clearAuthSilently();
    httpTestingController.verify();
    sessionStorage.clear();
    localStorage.clear();
  });

  it('bootstraps again after successful login before reusing the token for refresh and logout', () => {
    const loginUrl = `${environment.apiUrl}/auth/login`;
    const refreshUrl = `${environment.apiUrl}/auth/refresh`;
    const logoutUrl = `${environment.apiUrl}/auth/logout`;

    http.post(loginUrl, {}, { withCredentials: false }).subscribe();
    const loginBootstrap = httpTestingController.expectOne(csrfUrl);
    expect(loginBootstrap.request.method).toBe('GET');
    expect(loginBootstrap.request.withCredentials).toBeTrue();
    expect(loginBootstrap.request.headers.has('X-XSRF-TOKEN')).toBeFalse();
    loginBootstrap.flush({ token: 'csrf-token-a' });

    const loginRequest = httpTestingController.expectOne(loginUrl);
    expect(loginRequest.request.withCredentials).toBeTrue();
    expect(loginRequest.request.headers.get('X-XSRF-TOKEN')).toBe('csrf-token-a');
    expect(loginRequest.request.headers.has('Authorization')).toBeFalse();
    loginRequest.flush({});

    http.post(refreshUrl, {}).subscribe();
    const refreshBootstrap = httpTestingController.expectOne(csrfUrl);
    refreshBootstrap.flush({ token: 'csrf-token-b' });
    const refreshRequest = httpTestingController.expectOne(refreshUrl);
    expect(refreshRequest.request.headers.get('X-XSRF-TOKEN')).toBe('csrf-token-b');
    expect(refreshRequest.request.headers.has('Authorization')).toBeFalse();
    refreshRequest.flush({});

    http.post(logoutUrl, {}).subscribe();
    const logoutRequest = httpTestingController.expectOne(logoutUrl);
    expect(logoutRequest.request.withCredentials).toBeTrue();
    expect(logoutRequest.request.headers.get('X-XSRF-TOKEN')).toBe('csrf-token-b');
    expect(logoutRequest.request.headers.has('Authorization')).toBeFalse();
    logoutRequest.flush({});
    httpTestingController.expectNone(csrfUrl);

    expect(sessionStorage.getItem('csrf-token')).toBeNull();
    expect(sessionStorage.getItem('XSRF-TOKEN')).toBeNull();
    expect(localStorage.getItem('csrf-token')).toBeNull();
    expect(localStorage.getItem('XSRF-TOKEN')).toBeNull();
  });

  it('fetches a fresh token for logout after successful login', () => {
    const loginUrl = `${environment.apiUrl}/auth/login`;
    const logoutUrl = `${environment.apiUrl}/auth/logout`;

    http.post(loginUrl, {}).subscribe();
    httpTestingController.expectOne(csrfUrl).flush({ token: 'csrf-token-a' });
    const loginRequest = httpTestingController.expectOne(loginUrl);
    expect(loginRequest.request.headers.get('X-XSRF-TOKEN')).toBe('csrf-token-a');
    loginRequest.flush({});

    http.post(logoutUrl, {}).subscribe();
    const logoutBootstrap = httpTestingController.expectOne(csrfUrl);
    expect(logoutBootstrap.request.method).toBe('GET');
    expect(logoutBootstrap.request.withCredentials).toBeTrue();
    logoutBootstrap.flush({ token: 'csrf-token-b' });

    const logoutRequest = httpTestingController.expectOne(logoutUrl);
    expect(logoutRequest.request.headers.get('X-XSRF-TOKEN')).toBe('csrf-token-b');
    logoutRequest.flush({});
  });

  it('does not bootstrap or add a CSRF header for safe methods, logout-all, or non-exact API URLs', () => {
    const apiUrl = new URL(environment.apiUrl);
    const lookalikeHost = new URL(apiUrl.origin);
    lookalikeHost.hostname = `${lookalikeHost.hostname}.evil`;
    const requests = [
      { method: 'GET', url: `${environment.apiUrl}/auth/login` },
      { method: 'HEAD', url: `${environment.apiUrl}/auth/logout` },
      { method: 'OPTIONS', url: `${environment.apiUrl}/auth/refresh` },
      { method: 'POST', url: `${environment.apiUrl}/auth/logout-all` },
      { method: 'POST', url: `${environment.apiUrl}/auth/login/` },
      { method: 'POST', url: `${environment.apiUrl}/auth/login/extra` },
      {
        method: 'POST',
        url: new URL(`${apiUrl.pathname}/auth/login`, lookalikeHost).toString()
      },
      { method: 'POST', url: new URL('/foo/auth/login', apiUrl.origin).toString() },
      { method: 'POST', url: 'https://external.example/api/auth/login' }
    ];

    requests.forEach(({ method, url }) => {
      http.request(method, url, { body: method === 'GET' || method === 'HEAD' ? undefined : {} }).subscribe();
    });

    requests.forEach(({ method, url }) => {
      const request = httpTestingController.expectOne(candidate =>
        candidate.urlWithParams === url && candidate.method === method
      );
      expect(request.request.headers.has('X-XSRF-TOKEN')).toBeFalse();
      request.flush({});
    });
    httpTestingController.expectNone(csrfUrl);
  });

  it('does not retry a 403, renews for later requests, and preserves a newer token after a stale 403', () => {
    const errors: unknown[] = [];
    const loginUrl = `${environment.apiUrl}/auth/login`;
    const refreshUrl = `${environment.apiUrl}/auth/refresh`;
    const logoutUrl = `${environment.apiUrl}/auth/logout`;

    http.post(loginUrl, {}).subscribe({ error: error => errors.push(error) });
    http.post(refreshUrl, {}).subscribe({ error: error => errors.push(error) });

    httpTestingController.expectOne(csrfUrl).flush({ token: 'csrf-token-old' });
    const loginRequest = httpTestingController.expectOne(loginUrl);
    const staleRefreshRequest = httpTestingController.expectOne(refreshUrl);
    expect(loginRequest.request.headers.get('X-XSRF-TOKEN')).toBe('csrf-token-old');
    expect(staleRefreshRequest.request.headers.get('X-XSRF-TOKEN')).toBe('csrf-token-old');

    loginRequest.flush({ message: 'CSRF rejected' }, { status: 403, statusText: 'Forbidden' });
    expect(errors.length).toBe(1);
    httpTestingController.expectNone(loginUrl);
    httpTestingController.expectNone(refreshUrl);

    http.post(logoutUrl, {}).subscribe();
    httpTestingController.expectOne(csrfUrl).flush({ token: 'csrf-token-new' });
    const logoutRequest = httpTestingController.expectOne(logoutUrl);
    expect(logoutRequest.request.headers.get('X-XSRF-TOKEN')).toBe('csrf-token-new');

    staleRefreshRequest.flush({ message: 'CSRF rejected' }, { status: 403, statusText: 'Forbidden' });
    expect(errors.length).toBe(2);

    http.post(loginUrl, {}).subscribe();
    const nextLoginRequest = httpTestingController.expectOne(loginUrl);
    expect(nextLoginRequest.request.headers.get('X-XSRF-TOKEN')).toBe('csrf-token-new');
    httpTestingController.expectNone(csrfUrl);
    httpTestingController.expectNone(refreshUrl);

    logoutRequest.flush({});
    nextLoginRequest.flush({});
  });
});
