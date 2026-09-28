import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import {
  catchError, defer, finalize, forkJoin, from, Observable, of, shareReplay, switchMap, take, tap, timeout, throwError
} from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { AuthResponse } from '../../features/auth/models/auth-response';
import { LoginRequest } from '../../features/auth/models/login-request';
import { CarritoService } from './carrito/carrito-service';

const LOGOUT_REFRESH_TIMEOUT_MS = 10_000;
const LOGIN_REFRESH_TIMEOUT_MS = LOGOUT_REFRESH_TIMEOUT_MS;

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private carritoService = inject(CarritoService);
  
  private readonly API_URL = `${environment.apiUrl}/auth`;
  private readonly ACCESS_TOKEN_KEY = 'access_token';
  private readonly USER_DATA_KEY = 'user_data';
  
  // Signals para reactividad
  currentUser = signal<AuthResponse | null>(null);
  readonly isAuthenticated = signal(false);
  
  // Timer para renovar token antes de que expire
  private refreshTimer?: ReturnType<typeof setTimeout>;

  // Share only the active request; a later refresh must always make a fresh HTTP call.
  private refreshInFlight$?: Observable<AuthResponse>;
  private refreshResponseInFlight$?: Observable<AuthResponse>;
  private readonly pendingLogoutOperations = new Set<Promise<void>>();

  // Fences refresh responses after this app instance changes authentication state.
  private authStateVersion = 0;
  private sessionGeneration = 0;

  constructor() {
    this.loadStoredAuth();
  }

  updatePassword(dto: { currentPassword: string, newPassword: string }) {
    return this.http.patch<void>(`${environment.apiUrl}/usuarios/password`, dto);
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return defer(() => {
      // Capture both guards on subscription, so logout calls between login() and subscribe() count.
      const pendingRefresh = this.refreshResponseInFlight$;
      const pendingLogouts = Array.from(this.pendingLogoutOperations);
      const startLogin = () => this.http.post<AuthResponse>(`${this.API_URL}/login`, credentials, {
        withCredentials: true
      }).pipe(
        tap(response => {
          this.sessionGeneration++;
          this.handleAuthResponse(response);
        })
      );
      const waits: Observable<unknown>[] = [];

      if (pendingRefresh) {
        // Let an older refresh response process its cookie before a new login can set one.
        waits.push(pendingRefresh.pipe(
          take(1),
          catchError(() => of(null))
        ));
      }
      if (pendingLogouts.length > 0) {
        waits.push(from(Promise.all(pendingLogouts)));
      }

      if (waits.length === 0) {
        return startLogin();
      }

      return forkJoin(waits).pipe(
        timeout({
          first: LOGIN_REFRESH_TIMEOUT_MS,
          with: () => throwError(() => new Error(
            'A previous session operation is still pending. Please retry login.'
          ))
        }),
        switchMap(() => startLogin())
      );
    });
  }

  refreshToken(): Observable<AuthResponse> {
    const inFlightRefresh = this.refreshInFlight$;
    if (inFlightRefresh) {
      return inFlightRefresh;
    }

    const refreshStateVersion = this.authStateVersion;
    let refreshResponse$: Observable<AuthResponse>;
    refreshResponse$ = this.http.post<AuthResponse>(`${this.API_URL}/refresh`, {}, {
      withCredentials: true
    }).pipe(
      finalize(() => {
        if (this.refreshResponseInFlight$ === refreshResponse$) {
          this.refreshResponseInFlight$ = undefined;
          this.refreshInFlight$ = undefined;
        }
      }),
      shareReplay({ bufferSize: 1, refCount: false })
    );
    const refresh$ = refreshResponse$.pipe(
      tap(response => {
        if (refreshStateVersion !== this.authStateVersion) {
          throw new Error('Refresh response superseded by an auth state change');
        }
        this.handleAuthResponse(response);
      }),
      catchError(error => {
        // Handle the shared failure once, unless another auth operation superseded it.
        if (refreshStateVersion === this.authStateVersion) {
          this.clearAuthAndRedirect();
        }
        return throwError(() => error);
      }),
      shareReplay({ bufferSize: 1, refCount: false })
    );

    this.refreshResponseInFlight$ = refreshResponse$;
    this.refreshInFlight$ = refresh$;
    return refresh$;
  }

  logout(): void {
    const completeLogout = this.trackLogoutOperation();
    const pendingRefresh = this.refreshResponseInFlight$;
    const accessToken = this.getAccessToken();
    this.clearAuth();
    this.router.navigate(['/login']);

    this.runAfterPendingRefresh(
      pendingRefresh,
      () => this.sendLogout(completeLogout),
      () => this.sendLogoutAll(accessToken, completeLogout)
    );
  }

  logoutAllDevices(): void {
    const completeLogout = this.trackLogoutOperation();
    const pendingRefresh = this.refreshResponseInFlight$;
    const accessToken = this.getAccessToken();
    this.clearAuth();
    this.router.navigate(['/login']);

    this.runAfterPendingRefresh(
      pendingRefresh,
      response => this.sendLogoutAll(response?.accessToken ?? accessToken, completeLogout),
      () => this.sendLogoutAll(accessToken, completeLogout)
    );
  }

  clearAuthAndRedirect(): void {
    this.clearAuth();
    this.router.navigate(['/login'], {
      queryParams: { sessionExpired: 'true' }
    });
  }

  getAccessToken(): string | null {
    return sessionStorage.getItem(this.ACCESS_TOKEN_KEY);
  }

  getSessionGeneration(): number {
    return this.sessionGeneration;
  }

  getCurrentUser(): AuthResponse | null {
    return this.currentUser();
  }

  private runAfterPendingRefresh(
    pendingRefresh: Observable<AuthResponse> | undefined,
    onRefreshSettled: (response: AuthResponse | null) => void,
    onTimeout: () => void
  ): void {
    if (!pendingRefresh) {
      onRefreshSettled(null);
      return;
    }

    let settled = false;
    let pendingSubscription: { unsubscribe(): void } | undefined;
    const timeout = setTimeout(() => {
      if (settled) {
        return;
      }
      settled = true;
      pendingSubscription?.unsubscribe();
      onTimeout();
    }, LOGOUT_REFRESH_TIMEOUT_MS);

    pendingSubscription = pendingRefresh.pipe(
      take(1),
      catchError(() => of(null))
    ).subscribe({
      next: response => {
        if (settled) {
          return;
        }
        settled = true;
        clearTimeout(timeout);
        onRefreshSettled(response);
      },
      complete: () => {
        if (!settled) {
          settled = true;
          clearTimeout(timeout);
          onRefreshSettled(null);
        }
      }
    });
  }

  private trackLogoutOperation(): () => void {
    let resolveCompletion!: () => void;
    let completed = false;
    const completion = new Promise<void>(resolve => {
      resolveCompletion = resolve;
    });
    this.pendingLogoutOperations.add(completion);

    return () => {
      if (completed) {
        return;
      }
      completed = true;
      this.pendingLogoutOperations.delete(completion);
      resolveCompletion();
    };
  }

  private sendLogout(onSettled: () => void): void {
    try {
      this.http.post(`${this.API_URL}/logout`, {}, {
        withCredentials: true
      }).pipe(finalize(onSettled)).subscribe({ error: () => undefined });
    } catch {
      onSettled();
    }
  }

  private sendLogoutAll(token: string | null, onSettled: () => void): void {
    const options = token
      ? { withCredentials: true, headers: { Authorization: `Bearer ${token}` } }
      : { withCredentials: true };
    try {
      this.http.post(`${this.API_URL}/logout-all`, {}, options)
        .pipe(finalize(onSettled))
        .subscribe({ error: () => undefined });
    } catch {
      onSettled();
    }
  }

  private handleAuthResponse(response: AuthResponse): void {
    this.authStateVersion++;

    // Guardar access token y datos de usuario en sessionStorage
    sessionStorage.setItem(this.ACCESS_TOKEN_KEY, response.accessToken);

    // Agregar timestamp de creación para calcular edad del token
    const userDataWithTimestamp = {
      ...response,
      issuedAt: Date.now() // Guardar cuándo se emitió el token
    };
    
    sessionStorage.setItem(this.USER_DATA_KEY, JSON.stringify(userDataWithTimestamp));
    
    // Actualizar signals
    this.currentUser.set(response);
    this.isAuthenticated.set(true);
  
    
    // Programar renovación automática 1 minuto antes de expirar
    this.scheduleTokenRefresh(response.expiresIn);
  }

    private scheduleTokenRefresh(expiresIn: number): void {
    // Limpiar timer anterior si existe
    if (this.refreshTimer) {
      clearTimeout(this.refreshTimer);
    }

    // El backend envía milisegundos (900000 = 15 min)
    // Refrescar 1 minuto antes de expirar (mínimo 30 segundos antes)
    const oneMinuteInMs = 60000;
    const thirtySecondsInMs = 30000;
    
    const refreshTimeMs = Math.max(expiresIn - oneMinuteInMs, thirtySecondsInMs);
    
    this.refreshTimer = setTimeout(() => {
      this.refreshToken().subscribe({
        next: () => {
        },
        error: () => {
        }
      });
    }, refreshTimeMs);
  }


  /**
   * Carga el estado de autenticación almacenado al iniciar la app
   */
  private loadStoredAuth(): void {
    const token = this.getAccessToken();
    const userData = sessionStorage.getItem(this.USER_DATA_KEY);
    
    if (token && userData) {
      try {
        const user: AuthResponse & { issuedAt?: number } = JSON.parse(userData);
        
        this.currentUser.set(user);
        this.isAuthenticated.set(true);

              // Verificar si el token ya expiró
        if (user.issuedAt) {
          const now = Date.now();
          const tokenAgeMs = now - user.issuedAt;
          const tokenExpiresInMs = user.expiresIn || 900000; // default 15 min
          
          // Si el token tiene menos de 14 minutos de edad, programar refresh
          if (tokenAgeMs < (tokenExpiresInMs - 60000)) { // Tiene al menos 1 minuto antes de expirar
            const remainingTimeMs = tokenExpiresInMs - tokenAgeMs;
            this.scheduleTokenRefresh(remainingTimeMs);
          } else {
            // Token muy viejo o a punto de expirar, hacer refresh inmediato
            this.refreshToken().subscribe({
              error: () => {
              console.error('❌ Refresh falló, limpiando auth');
            }
          });
          }
        } else {
          // Datos viejos sin timestamp, hacer refresh inmediato
          this.refreshToken().subscribe({
            error: () => {}
          });
        }
        
      } catch (_error) {
        this.clearAuth();
      }
    } else {
      this.currentUser.set(null);
      this.isAuthenticated.set(false);
    }
  }

  /**
   * Limpia todo el estado de autenticación
   */
  private clearAuth(): void {
    this.authStateVersion++;
    this.sessionGeneration++;
    this.refreshInFlight$ = undefined;
    sessionStorage.removeItem(this.ACCESS_TOKEN_KEY);
    sessionStorage.removeItem(this.USER_DATA_KEY);
    
    this.currentUser.set(null);
    this.isAuthenticated.set(false);
    
    // Limpiar el carrito para evitar problemas de memoria y privacidad
    this.carritoService.vaciarCarrito();
    
    if (this.refreshTimer) {
      clearTimeout(this.refreshTimer);
      this.refreshTimer = undefined;
    }
  }

  clearAuthSilently(): void {
    this.clearAuth();
  }
}