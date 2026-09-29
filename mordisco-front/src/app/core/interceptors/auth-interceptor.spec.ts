import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { HttpClient, HttpErrorResponse, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { AuthResponse } from '../../features/auth/models/auth-response';
import { AuthService } from '../../shared/services/auth-service';
import { CarritoService } from '../../shared/services/carrito/carrito-service';
import { environment } from '../../../environments/environment';
import { authInterceptor } from './auth-interceptor';

describe('authInterceptor', () => {
  let http: HttpClient;
  let httpTestingController: HttpTestingController;
  let authService: AuthService;
  let router: jasmine.SpyObj<Router>;
  let carritoService: jasmine.SpyObj<CarritoService>;

  const authResponse = (accessToken: string, expiresIn = 900_000): AuthResponse => ({
    accessToken,
    userId: 1,
    email: 'user@example.com',
    nombre: 'User',
    role: 'CLIENTE',
    expiresIn
  });

  const protectedUrl = (path: string) => `${environment.apiUrl}/protected/${path}`;

  beforeEach(() => {
    sessionStorage.clear();
    sessionStorage.setItem('access_token', 'expired-access-token');
    sessionStorage.setItem('user_data', JSON.stringify({
      ...authResponse('expired-access-token', 90_000),
      issuedAt: Date.now()
    }));

    router = jasmine.createSpyObj<Router>('Router', ['navigate']);
    router.navigate.and.returnValue(Promise.resolve(true));
    carritoService = jasmine.createSpyObj<CarritoService>('CarritoService', ['vaciarCarrito']);

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        { provide: Router, useValue: router },
        { provide: CarritoService, useValue: carritoService }
      ]
    });

    http = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService);
  });

  afterEach(() => {
    authService.clearAuthSilently();
    httpTestingController.verify();
    sessionStorage.clear();
  });

  it('omits credentials from exact public auth routes on the configured API', () => {
    const publicEndpoints: { method: 'GET' | 'POST'; path: string }[] = [
      { method: 'POST', path: '/auth/login' },
      { method: 'POST', path: '/auth/register' },
      { method: 'POST', path: '/auth/refresh' },
      { method: 'POST', path: '/auth/logout' },
      { method: 'POST', path: '/auth/recover-password' },
      { method: 'POST', path: '/auth/reset-password' },
      { method: 'GET', path: '/auth/csrf' }
    ];

    for (const endpoint of publicEndpoints) {
      const url = `${environment.apiUrl}${endpoint.path}`;
      const request$ = endpoint.method === 'GET' ? http.get(url) : http.post(url, {});
      request$.subscribe();
      const request = httpTestingController.expectOne(url);
      expect(request.request.headers.has('Authorization')).withContext(url).toBeFalse();
      request.flush({});
    }
  });

  it('does not exempt nested API paths that end with a public auth route', () => {
    const url = `${environment.apiUrl}/protected/auth/login`;
    http.get(url).subscribe();

    const request = httpTestingController.expectOne(url);
    expect(request.request.headers.get('Authorization')).toBe('Bearer expired-access-token');
    request.flush({ ok: true });
  });

  it('does not attach a stored token or refresh an external request after 401', () => {
    const errors: unknown[] = [];
    const url = 'https://third-party.example/protected/resource';
    http.get(url).subscribe({ error: error => errors.push(error) });

    const request = httpTestingController.expectOne(url);
    expect(request.request.headers.has('Authorization')).toBeFalse();
    request.flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(errors.length).toBe(1);
    expect(authService.getAccessToken()).toBe('expired-access-token');
    expect(httpTestingController.match(request => request.url.includes('/auth/refresh')).length).toBe(0);
  });

  it('does not attach bearer or refresh for same-origin paths outside the exact API boundary', () => {
    const configuredApi = new URL(environment.apiUrl);
    const apiPath = configuredApi.pathname.replace(/\/+$/, '');
    const alternatePort = configuredApi.port === '1' ? '2' : '1';
    const alternateScheme = configuredApi.protocol === 'http:' ? 'https:' : 'http:';
    const outsideApiUrls = [
      '/protected/resource',
      new URL('/outside/protected', configuredApi.origin).toString(),
      new URL(`${apiPath}/protected`, `${alternateScheme}//${configuredApi.host}`).toString(),
      new URL(`${apiPath}-evil/protected`, configuredApi.origin).toString(),
      new URL(
        `${apiPath}/protected`,
        `${configuredApi.protocol}//${configuredApi.hostname}.evil.test${configuredApi.port ? `:${configuredApi.port}` : ''}`
      ).toString(),
      new URL(`${apiPath}/protected`, `${configuredApi.protocol}//${configuredApi.hostname}:${alternatePort}`).toString(),
      new URL(`${apiPath}/protected`, `${configuredApi.protocol}//user:password@${configuredApi.host}`).toString(),
      `${configuredApi.protocol}//@${configuredApi.host}${apiPath}/protected`
    ];

    for (const url of outsideApiUrls) {
      const errors: unknown[] = [];
      http.get(url).subscribe({ error: error => errors.push(error) });
      const request = httpTestingController.expectOne(url);
      expect(request.request.headers.has('Authorization')).withContext(url).toBeFalse();
      request.flush({}, { status: 401, statusText: 'Unauthorized' });
      expect(errors.length).withContext(url).toBe(1);
    }

    expect(httpTestingController.match(request => request.url.includes('/auth/refresh')).length).toBe(0);
  });

  it('shares a timer-triggered refresh with concurrent 401s and retries with the returned token', fakeAsync(() => {
    const sessionGeneration = authService.getSessionGeneration();
    const results: unknown[] = [];
    http.get(protectedUrl('resource')).subscribe(result => results.push(result));
    http.get(protectedUrl('another')).subscribe(result => results.push(result));

    const firstRequest = httpTestingController.expectOne(protectedUrl('resource'));
    const secondRequest = httpTestingController.expectOne(protectedUrl('another'));
    expect(firstRequest.request.headers.get('Authorization')).toBe('Bearer expired-access-token');
    expect(secondRequest.request.headers.get('Authorization')).toBe('Bearer expired-access-token');

    firstRequest.flush({}, { status: 401, statusText: 'Unauthorized' });
    secondRequest.flush({}, { status: 401, statusText: 'Unauthorized' });
    const refreshRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/refresh`);
    expect(refreshRequest.request.withCredentials).toBeTrue();

    // The stored session schedules its own refresh for the same moment.
    tick(30_000);
    httpTestingController.expectNone(`${environment.apiUrl}/auth/refresh`);

    refreshRequest.flush(authResponse('fresh-access-token'));
    const firstRetry = httpTestingController.expectOne(protectedUrl('resource'));
    const secondRetry = httpTestingController.expectOne(protectedUrl('another'));
    expect(firstRetry.request.headers.get('Authorization')).toBe('Bearer fresh-access-token');
    expect(secondRetry.request.headers.get('Authorization')).toBe('Bearer fresh-access-token');
    firstRetry.flush({ ok: true });
    secondRetry.flush({ ok: true });

    expect(results).toEqual([{ ok: true }, { ok: true }]);
    expect(authService.getAccessToken()).toBe('fresh-access-token');
    expect(authService.getSessionGeneration()).toBe(sessionGeneration);
    expect(authService.isAuthenticated()).toBeTrue();
    authService.clearAuthSilently();
  }));

  it('clears and redirects once on a shared refresh failure without retrying failed requests', () => {
    const firstErrors: unknown[] = [];
    const secondErrors: unknown[] = [];
    http.get(protectedUrl('first')).subscribe({ error: error => firstErrors.push(error) });
    http.get(protectedUrl('second')).subscribe({ error: error => secondErrors.push(error) });

    httpTestingController
      .expectOne(protectedUrl('first'))
      .flush({}, { status: 401, statusText: 'Unauthorized' });
    httpTestingController
      .expectOne(protectedUrl('second'))
      .flush({}, { status: 401, statusText: 'Unauthorized' });

    httpTestingController
      .expectOne(`${environment.apiUrl}/auth/refresh`)
      .flush({ message: 'refresh denied' }, { status: 401, statusText: 'Unauthorized' });

    expect(firstErrors.length).toBe(1);
    expect(secondErrors.length).toBe(1);
    expect(authService.getAccessToken()).toBeNull();
    expect(authService.currentUser()).toBeNull();
    expect(authService.isAuthenticated()).toBeFalse();
    expect(carritoService.vaciarCarrito).toHaveBeenCalledTimes(1);
    expect(router.navigate).toHaveBeenCalledTimes(1);
    expect(httpTestingController.match(request => request.url.includes('/auth/refresh')).length).toBe(0);
    expect(httpTestingController.match(request => request.url.startsWith(`${environment.apiUrl}/protected/`)).length).toBe(0);
  });

  it('does not start another refresh when a retried request still returns 401', () => {
    const errors: unknown[] = [];
    http.get(protectedUrl('resource')).subscribe({ error: error => errors.push(error) });
    httpTestingController
      .expectOne(protectedUrl('resource'))
      .flush({}, { status: 401, statusText: 'Unauthorized' });

    httpTestingController
      .expectOne(`${environment.apiUrl}/auth/refresh`)
      .flush(authResponse('fresh-access-token'));
    const retryRequest = httpTestingController.expectOne(protectedUrl('resource'));
    expect(retryRequest.request.headers.get('Authorization')).toBe('Bearer fresh-access-token');
    retryRequest.flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(errors.length).toBe(1);
    expect(httpTestingController.match(request => request.url.includes('/auth/refresh')).length).toBe(0);
    expect(router.navigate).not.toHaveBeenCalled();
    expect(carritoService.vaciarCarrito).not.toHaveBeenCalled();
    expect(authService.isAuthenticated()).toBeTrue();
  });

  it('does not start a refresh when a protected 401 arrives after auth was cleared', () => {
    const errors: unknown[] = [];
    authService.clearAuthSilently();

    http.get(protectedUrl('after-logout')).subscribe({ error: error => errors.push(error) });
    const request = httpTestingController.expectOne(protectedUrl('after-logout'));
    expect(request.request.headers.has('Authorization')).toBeFalse();
    request.flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(errors.length).toBe(1);
    expect(httpTestingController.match(request => request.url.includes('/auth/refresh')).length).toBe(0);
    expect(router.navigate).not.toHaveBeenCalled();
    expect(carritoService.vaciarCarrito).toHaveBeenCalledTimes(1);
  });

  it('does not replay a session A request under session B after logout and login', () => {
    const errors: unknown[] = [];
    http.get(protectedUrl('session-a')).subscribe({ error: error => errors.push(error) });
    const sessionARequest = httpTestingController.expectOne(protectedUrl('session-a'));
    expect(sessionARequest.request.headers.get('Authorization')).toBe('Bearer expired-access-token');

    authService.logout();
    httpTestingController.expectOne(`${environment.apiUrl}/auth/logout`).flush({});
    authService.login({ email: 'user@example.com', password: 'Password1!' }).subscribe();
    httpTestingController
      .expectOne(`${environment.apiUrl}/auth/login`)
      .flush(authResponse('session-b-access-token'));

    sessionARequest.flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(errors.length).toBe(1);
    expect((errors[0] as HttpErrorResponse).status).toBe(401);
    expect(authService.getAccessToken()).toBe('session-b-access-token');
    expect(httpTestingController.match(request => request.url === protectedUrl('session-a')).length).toBe(0);
    expect(httpTestingController.match(request => request.url.includes('/auth/refresh')).length).toBe(0);
  });

  it('does not refresh a session A request whose 401 arrives after logout but before login', () => {
    const errors: unknown[] = [];
    http.get(protectedUrl('session-a-before-login')).subscribe({ error: error => errors.push(error) });
    const sessionARequest = httpTestingController.expectOne(protectedUrl('session-a-before-login'));
    expect(sessionARequest.request.headers.get('Authorization')).toBe('Bearer expired-access-token');

    authService.logout();
    const logoutRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/logout`);
    sessionARequest.flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(errors.length).toBe(1);
    expect((errors[0] as HttpErrorResponse).status).toBe(401);
    expect(authService.getAccessToken()).toBeNull();
    expect(httpTestingController.match(request => request.url === protectedUrl('session-a-before-login')).length).toBe(0);
    expect(httpTestingController.match(request => request.url.includes('/auth/refresh')).length).toBe(0);

    logoutRequest.flush({});
  });

  it('does not retry or redirect a pending 401 refresh after logout-all clears auth', () => {
    const errors: unknown[] = [];
    http.get(protectedUrl('logout-all-race')).subscribe({ error: error => errors.push(error) });
    httpTestingController
      .expectOne(protectedUrl('logout-all-race'))
      .flush({}, { status: 401, statusText: 'Unauthorized' });

    const refreshRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/refresh`);
    authService.logoutAllDevices();

    expect(authService.getAccessToken()).toBeNull();
    const earlyLogoutAllRequests = httpTestingController.match(`${environment.apiUrl}/auth/logout-all`);
    expect(earlyLogoutAllRequests.length).toBe(0);

    refreshRequest.flush(authResponse('late-access-token'));

    const logoutAllRequests = httpTestingController.match(`${environment.apiUrl}/auth/logout-all`);
    expect(logoutAllRequests.length).toBe(1);
    logoutAllRequests.forEach(request => {
      expect(request.request.headers.get('Authorization')).toBe('Bearer late-access-token');
      expect(request.request.withCredentials).toBeTrue();
      request.flush({});
    });

    expect(errors.length).toBe(1);
    expect(httpTestingController.match(request => request.url === protectedUrl('logout-all-race')).length).toBe(0);
    expect(httpTestingController.match(request => request.url.includes('/auth/refresh')).length).toBe(0);
    expect(authService.getAccessToken()).toBeNull();
    expect(sessionStorage.getItem('user_data')).toBeNull();
    expect(authService.currentUser()).toBeNull();
    expect(authService.isAuthenticated()).toBeFalse();
    expect(carritoService.vaciarCarrito).toHaveBeenCalledTimes(1);
    expect(router.navigate).toHaveBeenCalledOnceWith(['/login']);
  });
});
