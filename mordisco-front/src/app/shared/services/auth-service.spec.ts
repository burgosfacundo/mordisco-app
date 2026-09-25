import { fakeAsync, TestBed, tick } from '@angular/core/testing';
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
});
