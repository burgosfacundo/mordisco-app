import { fakeAsync, flushMicrotasks, TestBed, tick } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { AuthResponse } from '../../features/auth/models/auth-response';
import { CarritoService } from './carrito/carrito-service';
import { AuthService } from './auth-service';
import { environment } from '../../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpTestingController: HttpTestingController;
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

  beforeEach(() => {
    sessionStorage.clear();
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);
    router.navigate.and.returnValue(Promise.resolve(true));
    carritoService = jasmine.createSpyObj<CarritoService>('CarritoService', ['vaciarCarrito']);

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Router, useValue: router },
        { provide: CarritoService, useValue: carritoService }
      ]
    });

    service = TestBed.inject(AuthService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
    service.clearAuthSilently();
    sessionStorage.clear();
  });

  it('shares one active refresh request and returns its current response to every caller', () => {
    const firstResults: AuthResponse[] = [];
    const secondResults: AuthResponse[] = [];
    const response = authResponse('fresh-access-token');

    service.refreshToken().subscribe(result => firstResults.push(result));
    service.refreshToken().subscribe(result => secondResults.push(result));

    const refreshRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/refresh`);
    expect(refreshRequest.request.withCredentials).toBeTrue();
    refreshRequest.flush(response);

    expect(firstResults).toEqual([response]);
    expect(secondResults).toEqual([response]);
    expect(service.getAccessToken()).toBe('fresh-access-token');
    expect(service.currentUser()).toEqual(response);
    expect(service.isAuthenticated()).toBeTrue();
    expect(JSON.parse(sessionStorage.getItem('user_data')!).accessToken).toBe('fresh-access-token');
    expect(carritoService.vaciarCarrito).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('changes session generation for login and clear but not for token rotation', () => {
    expect(service.getSessionGeneration()).toBe(0);

    service.login({ email: 'user@example.com', password: 'Password1!' }).subscribe();
    httpTestingController
      .expectOne(`${environment.apiUrl}/auth/login`)
      .flush(authResponse('session-a-access-token'));
    const sessionGeneration = service.getSessionGeneration();
    expect(sessionGeneration).toBe(1);

    service.refreshToken().subscribe();
    httpTestingController
      .expectOne(`${environment.apiUrl}/auth/refresh`)
      .flush(authResponse('rotated-session-a-access-token'));
    expect(service.getSessionGeneration()).toBe(sessionGeneration);

    service.clearAuthSilently();
    expect(service.getSessionGeneration()).toBe(sessionGeneration + 1);

    service.login({ email: 'user@example.com', password: 'Password1!' }).subscribe();
    httpTestingController
      .expectOne(`${environment.apiUrl}/auth/login`)
      .flush(authResponse('session-b-access-token'));
    expect(service.getSessionGeneration()).toBe(sessionGeneration + 2);
  });

  it('starts a fresh request after a previous refresh completes instead of replaying stale data', () => {
    const results: AuthResponse[] = [];
    service.refreshToken().subscribe(result => results.push(result));

    httpTestingController
      .expectOne(`${environment.apiUrl}/auth/refresh`)
      .flush(authResponse('first-access-token'));

    service.refreshToken().subscribe(result => results.push(result));
    const secondRefresh = httpTestingController.expectOne(`${environment.apiUrl}/auth/refresh`);
    secondRefresh.flush(authResponse('second-access-token'));

    expect(results.map(result => result.accessToken)).toEqual([
      'first-access-token',
      'second-access-token'
    ]);
    expect(service.getAccessToken()).toBe('second-access-token');
    expect(service.currentUser()?.accessToken).toBe('second-access-token');
  });

  it('shares a pending manual refresh with the scheduled timer and sends cookie credentials', fakeAsync(() => {
    service.login({ email: 'user@example.com', password: 'Password1!' }).subscribe();
    const loginRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/login`);
    expect(loginRequest.request.withCredentials).toBeTrue();
    loginRequest.flush(authResponse('login-access-token', 60_000));

    const refreshResults: AuthResponse[] = [];
    service.refreshToken().subscribe(result => refreshResults.push(result));
    const refreshRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/refresh`);
    expect(refreshRequest.request.withCredentials).toBeTrue();

    tick(30_000);
    httpTestingController.expectNone(`${environment.apiUrl}/auth/refresh`);

    refreshRequest.flush(authResponse('timer-shared-access-token'));
    expect(refreshResults.map(result => result.accessToken)).toEqual(['timer-shared-access-token']);
    expect(service.getAccessToken()).toBe('timer-shared-access-token');

    service.clearAuthSilently();
  }));

  it('clears session state and redirects once when a shared refresh fails', () => {
    const firstErrors: unknown[] = [];
    const secondErrors: unknown[] = [];
    service.refreshToken().subscribe({ error: error => firstErrors.push(error) });
    service.refreshToken().subscribe({ error: error => secondErrors.push(error) });

    const refreshRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/refresh`);
    refreshRequest.flush({ message: 'refresh denied' }, { status: 401, statusText: 'Unauthorized' });

    expect(firstErrors.length).toBe(1);
    expect(secondErrors.length).toBe(1);
    expect(service.getAccessToken()).toBeNull();
    expect(service.currentUser()).toBeNull();
    expect(service.isAuthenticated()).toBeFalse();
    expect(carritoService.vaciarCarrito).toHaveBeenCalledTimes(1);
    expect(router.navigate).toHaveBeenCalledTimes(1);
    expect(router.navigate).toHaveBeenCalledWith(['/login'], {
      queryParams: { sessionExpired: 'true' }
    });
  });

  it('keeps logout cookie-backed and clears the local session on success', () => {
    service.login({ email: 'user@example.com', password: 'Password1!' }).subscribe();
    httpTestingController
      .expectOne(`${environment.apiUrl}/auth/login`)
      .flush(authResponse('login-access-token'));

    service.logout();
    const logoutRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/logout`);
    expect(logoutRequest.request.withCredentials).toBeTrue();
    logoutRequest.flush({});

    expect(service.getAccessToken()).toBeNull();
    expect(service.currentUser()).toBeNull();
    expect(service.isAuthenticated()).toBeFalse();
    expect(carritoService.vaciarCarrito).toHaveBeenCalledTimes(1);
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('captures pending logout work when the login observable is subscribed', fakeAsync(() => {
    const login$ = service.login({ email: 'user@example.com', password: 'Password1!' });
    service.logout();
    const logoutRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/logout`);

    login$.subscribe();
    httpTestingController.expectNone(`${environment.apiUrl}/auth/login`);
    logoutRequest.flush({});
    flushMicrotasks();

    httpTestingController
      .expectOne(`${environment.apiUrl}/auth/login`)
      .flush(authResponse('subscribed-after-logout-access-token'));
    expect(service.getAccessToken()).toBe('subscribed-after-logout-access-token');
    service.clearAuthSilently();
  }));

  it('waits for a direct logout request before starting a subscribed login', fakeAsync(() => {
    service.logout();
    const logoutRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/logout`);
    const loginResults: AuthResponse[] = [];

    service.login({ email: 'user@example.com', password: 'Password1!' })
      .subscribe(response => loginResults.push(response));
    httpTestingController.expectNone(`${environment.apiUrl}/auth/login`);

    // HttpTestingController verifies ordering but cannot simulate browser-managed HttpOnly cookies.
    logoutRequest.flush({});
    flushMicrotasks();

    const loginRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/login`);
    loginRequest.flush(authResponse('after-logout-access-token'));
    expect(loginResults.map(response => response.accessToken)).toEqual(['after-logout-access-token']);
    service.clearAuthSilently();
  }));

  it('waits for both a pending refresh and its logout request before starting login', fakeAsync(() => {
    service.login({ email: 'user@example.com', password: 'Password1!' }).subscribe();
    httpTestingController
      .expectOne(`${environment.apiUrl}/auth/login`)
      .flush(authResponse('initial-access-token'));

    service.refreshToken().subscribe({ error: () => undefined });
    const refreshRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/refresh`);
    service.logout();

    service.login({ email: 'user@example.com', password: 'Password1!' }).subscribe();
    httpTestingController.expectNone(`${environment.apiUrl}/auth/login`);

    refreshRequest.flush(authResponse('rotated-access-token'));
    const logoutRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/logout`);
    httpTestingController.expectNone(`${environment.apiUrl}/auth/login`);

    logoutRequest.flush({});
    flushMicrotasks();
    const loginRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/login`);
    loginRequest.flush(authResponse('after-refresh-and-logout-access-token'));
    expect(service.getAccessToken()).toBe('after-refresh-and-logout-access-token');
    service.clearAuthSilently();
  }));

  it('releases the login gate when logout-all succeeds', fakeAsync(() => {
    service.logoutAllDevices();
    const logoutAllRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/logout-all`);

    service.login({ email: 'user@example.com', password: 'Password1!' }).subscribe();
    httpTestingController.expectNone(`${environment.apiUrl}/auth/login`);

    logoutAllRequest.flush({});
    flushMicrotasks();
    httpTestingController
      .expectOne(`${environment.apiUrl}/auth/login`)
      .flush(authResponse('after-logout-all-access-token'));
    expect(service.getAccessToken()).toBe('after-logout-all-access-token');
    service.clearAuthSilently();
  }));

  it('releases the login gate when logout-all fails', fakeAsync(() => {
    service.logoutAllDevices();
    const logoutAllRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/logout-all`);

    service.login({ email: 'user@example.com', password: 'Password1!' }).subscribe();
    httpTestingController.expectNone(`${environment.apiUrl}/auth/login`);

    logoutAllRequest.flush({ message: 'logout-all failed' }, { status: 500, statusText: 'Server Error' });
    flushMicrotasks();
    httpTestingController
      .expectOne(`${environment.apiUrl}/auth/login`)
      .flush(authResponse('after-failed-logout-all-access-token'));
    expect(service.getAccessToken()).toBe('after-failed-logout-all-access-token');
    service.clearAuthSilently();
  }));

  it('waits for every initiated logout request before releasing login', fakeAsync(() => {
    service.logout();
    service.logout();
    const logoutRequests = httpTestingController.match(`${environment.apiUrl}/auth/logout`);
    expect(logoutRequests.length).toBe(2);

    service.login({ email: 'user@example.com', password: 'Password1!' }).subscribe();
    httpTestingController.expectNone(`${environment.apiUrl}/auth/login`);

    logoutRequests[0].flush({});
    flushMicrotasks();
    httpTestingController.expectNone(`${environment.apiUrl}/auth/login`);

    logoutRequests[1].flush({});
    flushMicrotasks();
    httpTestingController
      .expectOne(`${environment.apiUrl}/auth/login`)
      .flush(authResponse('after-all-logouts-access-token'));
    service.clearAuthSilently();
  }));

  it('fails login retryably after a stalled logout request, then allows a later retry', fakeAsync(() => {
    service.logout();
    const stalledLogoutRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/logout`);
    const loginErrors: unknown[] = [];

    service.login({ email: 'user@example.com', password: 'Password1!' })
      .subscribe({ error: error => loginErrors.push(error) });
    tick(9_999);
    httpTestingController.expectNone(`${environment.apiUrl}/auth/login`);
    expect(loginErrors).toEqual([]);

    tick(1);
    expect(loginErrors.length).toBe(1);
    expect((loginErrors[0] as Error).message).toContain('Please retry');
    httpTestingController.expectNone(`${environment.apiUrl}/auth/login`);

    stalledLogoutRequest.flush({});
    flushMicrotasks();
    service.login({ email: 'user@example.com', password: 'Password1!' }).subscribe();
    httpTestingController
      .expectOne(`${environment.apiUrl}/auth/login`)
      .flush(authResponse('retry-after-logout-access-token'));
    service.clearAuthSilently();
  }));

  it('keeps the timeout logout-all fallback gated until its backend request settles', fakeAsync(() => {
    service.login({ email: 'user@example.com', password: 'Password1!' }).subscribe();
    httpTestingController
      .expectOne(`${environment.apiUrl}/auth/login`)
      .flush(authResponse('initial-access-token'));

    service.refreshToken().subscribe({ error: () => undefined });
    const refreshRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/refresh`);
    service.logout();

    const firstLoginErrors: unknown[] = [];
    service.login({ email: 'user@example.com', password: 'Password1!' })
      .subscribe({ error: error => firstLoginErrors.push(error) });
    tick(10_000);
    expect(firstLoginErrors.length).toBe(1);
    const fallbackRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/logout-all`);
    httpTestingController.expectNone(`${environment.apiUrl}/auth/login`);

    const retryLoginResults: AuthResponse[] = [];
    service.login({ email: 'user@example.com', password: 'Password1!' })
      .subscribe(response => retryLoginResults.push(response));
    refreshRequest.flush(authResponse('late-refresh-access-token'));
    flushMicrotasks();
    httpTestingController.expectNone(`${environment.apiUrl}/auth/login`);

    fallbackRequest.flush({});
    flushMicrotasks();
    httpTestingController
      .expectOne(`${environment.apiUrl}/auth/login`)
      .flush(authResponse('after-fallback-access-token'));
    expect(retryLoginResults.map(response => response.accessToken)).toEqual(['after-fallback-access-token']);
    service.clearAuthSilently();
  }));

  it('waits for a pending refresh before logout and fences its late auth response', () => {
    service.login({ email: 'user@example.com', password: 'Password1!' }).subscribe();
    httpTestingController
      .expectOne(`${environment.apiUrl}/auth/login`)
      .flush(authResponse('login-access-token'));

    const refreshErrors: unknown[] = [];
    service.refreshToken().subscribe({ error: error => refreshErrors.push(error) });
    const refreshRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/refresh`);

    service.logout();
    expect(service.getAccessToken()).toBeNull();
    expect(service.currentUser()).toBeNull();
    expect(service.isAuthenticated()).toBeFalse();

    const earlyLogoutRequests = httpTestingController.match(`${environment.apiUrl}/auth/logout`);
    expect(earlyLogoutRequests.length).toBe(0);
    earlyLogoutRequests.forEach(request => request.flush({}));

    // HttpTestingController sees request ordering, but cannot model browser-managed HttpOnly cookies.
    refreshRequest.flush(authResponse('rotated-access-token'));

    const logoutRequests = httpTestingController.match(`${environment.apiUrl}/auth/logout`);
    expect(logoutRequests.length).toBe(1);
    logoutRequests.forEach(request => {
      expect(request.request.withCredentials).toBeTrue();
      request.flush({});
    });

    expect(refreshErrors.length).toBe(1);
    expect(service.getAccessToken()).toBeNull();
    expect(sessionStorage.getItem('user_data')).toBeNull();
    expect(service.currentUser()).toBeNull();
    expect(service.isAuthenticated()).toBeFalse();
    expect(carritoService.vaciarCarrito).toHaveBeenCalledTimes(1);
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('uses one authenticated logout-all fallback when refresh stalls and ignores its late response', fakeAsync(() => {
    service.login({ email: 'user@example.com', password: 'Password1!' }).subscribe();
    httpTestingController
      .expectOne(`${environment.apiUrl}/auth/login`)
      .flush(authResponse('login-access-token'));

    const refreshErrors: unknown[] = [];
    service.refreshToken().subscribe({ error: error => refreshErrors.push(error) });
    const refreshRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/refresh`);

    service.logout();
    expect(service.getAccessToken()).toBeNull();
    expect(service.currentUser()).toBeNull();
    expect(service.isAuthenticated()).toBeFalse();
    expect(router.navigate).toHaveBeenCalledOnceWith(['/login']);

    tick(9_999);
    httpTestingController.expectNone(`${environment.apiUrl}/auth/logout-all`);
    tick(1);

    const fallbackRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/logout-all`);
    expect(fallbackRequest.request.withCredentials).toBeTrue();
    expect(fallbackRequest.request.headers.get('Authorization')).toBe('Bearer login-access-token');
    fallbackRequest.flush({});

    refreshRequest.flush(authResponse('late-access-token'));

    expect(httpTestingController.match(`${environment.apiUrl}/auth/logout-all`).length).toBe(0);
    expect(httpTestingController.match(`${environment.apiUrl}/auth/logout`).length).toBe(0);
    expect(refreshErrors.length).toBe(1);
    expect(service.getAccessToken()).toBeNull();
    expect(sessionStorage.getItem('user_data')).toBeNull();
    expect(service.currentUser()).toBeNull();
    expect(service.isAuthenticated()).toBeFalse();
    expect(carritoService.vaciarCarrito).toHaveBeenCalledTimes(1);
    expect(router.navigate).toHaveBeenCalledOnceWith(['/login']);
  }));

  it('keeps logout-all effective when an in-flight refresh responds late', () => {
    service.login({ email: 'user@example.com', password: 'Password1!' }).subscribe();
    httpTestingController
      .expectOne(`${environment.apiUrl}/auth/login`)
      .flush(authResponse('login-access-token'));

    const refreshErrors: unknown[] = [];
    service.refreshToken().subscribe({ error: error => refreshErrors.push(error) });
    const refreshRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/refresh`);

    service.logoutAllDevices();
    expect(service.getAccessToken()).toBeNull();
    const earlyLogoutAllRequests = httpTestingController.match(`${environment.apiUrl}/auth/logout-all`);
    expect(earlyLogoutAllRequests.length).toBe(0);

    refreshRequest.flush(authResponse('late-access-token'));

    const logoutAllRequests = httpTestingController.match(`${environment.apiUrl}/auth/logout-all`);
    expect(logoutAllRequests.length).toBe(1);
    logoutAllRequests.forEach(request => {
      expect(request.request.withCredentials).toBeTrue();
      expect(request.request.headers.get('Authorization')).toBe('Bearer late-access-token');
      request.flush({});
    });

    expect(refreshErrors.length).toBe(1);
    expect(service.getAccessToken()).toBeNull();
    expect(sessionStorage.getItem('user_data')).toBeNull();
    expect(service.currentUser()).toBeNull();
    expect(service.isAuthenticated()).toBeFalse();
    expect(carritoService.vaciarCarrito).toHaveBeenCalledTimes(1);
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('queues a new login behind a pending refresh and starts a fresh refresh afterward', () => {
    service.login({ email: 'user@example.com', password: 'Password1!' }).subscribe();
    httpTestingController
      .expectOne(`${environment.apiUrl}/auth/login`)
      .flush(authResponse('initial-access-token'));

    const staleRefreshErrors: unknown[] = [];
    service.refreshToken().subscribe({ error: error => staleRefreshErrors.push(error) });
    const staleRefreshRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/refresh`);

    service.clearAuthSilently();
    const loginResults: AuthResponse[] = [];
    service.login({ email: 'user@example.com', password: 'Password1!' })
      .subscribe(response => loginResults.push(response));
    httpTestingController.expectNone(`${environment.apiUrl}/auth/login`);

    staleRefreshRequest.flush(authResponse('stale-refresh-access-token'));
    expect(staleRefreshErrors.length).toBe(1);
    expect(service.getAccessToken()).toBeNull();

    httpTestingController
      .expectOne(`${environment.apiUrl}/auth/login`)
      .flush(authResponse('new-login-access-token'));
    expect(loginResults.map(response => response.accessToken)).toEqual(['new-login-access-token']);
    expect(service.getAccessToken()).toBe('new-login-access-token');

    const newRefreshResults: AuthResponse[] = [];
    service.refreshToken().subscribe(response => newRefreshResults.push(response));
    const newRefreshRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/refresh`);
    newRefreshRequest.flush(authResponse('new-refresh-access-token'));

    expect(newRefreshResults.map(response => response.accessToken)).toEqual(['new-refresh-access-token']);
    expect(service.getAccessToken()).toBe('new-refresh-access-token');
    expect(service.currentUser()?.accessToken).toBe('new-refresh-access-token');
  });

  it('fails login retryably when a prior refresh stalls, then allows login after it settles', fakeAsync(() => {
    service.login({ email: 'user@example.com', password: 'Password1!' }).subscribe();
    httpTestingController
      .expectOne(`${environment.apiUrl}/auth/login`)
      .flush(authResponse('login-access-token'));

    const refreshErrors: unknown[] = [];
    service.refreshToken().subscribe({ error: error => refreshErrors.push(error) });
    const oldRefreshRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/refresh`);

    service.logout();
    const loginErrors: unknown[] = [];
    service.login({ email: 'user@example.com', password: 'Password1!' })
      .subscribe({ error: error => loginErrors.push(error) });

    tick(9_999);
    expect(loginErrors).toEqual([]);
    httpTestingController.expectNone(`${environment.apiUrl}/auth/login`);
    tick(1);

    expect(loginErrors.length).toBe(1);
    expect(loginErrors[0]).toEqual(jasmine.any(Error));
    expect((loginErrors[0] as Error).message).toContain('Please retry');
    expect(httpTestingController.match(`${environment.apiUrl}/auth/login`).length).toBe(0);
    expect(service.getAccessToken()).toBeNull();
    expect(service.currentUser()).toBeNull();
    expect(service.isAuthenticated()).toBeFalse();

    const fallbackRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/logout-all`);
    expect(fallbackRequest.request.headers.get('Authorization')).toBe('Bearer login-access-token');
    fallbackRequest.flush({});

    // A browser may apply the old refresh cookie, so do not issue retry login until it settles.
    oldRefreshRequest.flush(authResponse('stale-refresh-access-token'));
    httpTestingController.expectNone(`${environment.apiUrl}/auth/refresh`);
    expect(refreshErrors.length).toBe(1);
    expect(service.getAccessToken()).toBeNull();
    expect(service.currentUser()).toBeNull();
    expect(service.isAuthenticated()).toBeFalse();

    const retryResults: AuthResponse[] = [];
    service.login({ email: 'user@example.com', password: 'Password1!' })
      .subscribe(response => retryResults.push(response));
    const retryLoginRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/login`);
    expect(retryLoginRequest.request.withCredentials).toBeTrue();
    retryLoginRequest.flush(authResponse('new-login-access-token'));
    httpTestingController.expectNone(`${environment.apiUrl}/auth/refresh`);

    expect(retryResults.map(response => response.accessToken)).toEqual(['new-login-access-token']);
    expect(service.getAccessToken()).toBe('new-login-access-token');
    expect(service.currentUser()?.accessToken).toBe('new-login-access-token');
    expect(service.isAuthenticated()).toBeTrue();
    service.clearAuthSilently();
  }));

  it('keeps password-change silent clearing effective when a refresh responds late', () => {
    const refreshErrors: unknown[] = [];
    service.refreshToken().subscribe({ error: error => refreshErrors.push(error) });
    const refreshRequest = httpTestingController.expectOne(`${environment.apiUrl}/auth/refresh`);

    service.clearAuthSilently();
    refreshRequest.flush(authResponse('late-access-token'));

    expect(refreshErrors.length).toBe(1);
    expect(service.getAccessToken()).toBeNull();
    expect(sessionStorage.getItem('user_data')).toBeNull();
    expect(service.currentUser()).toBeNull();
    expect(service.isAuthenticated()).toBeFalse();
    expect(carritoService.vaciarCarrito).toHaveBeenCalledTimes(1);
    expect(router.navigate).not.toHaveBeenCalled();
  });
});
