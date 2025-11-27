import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { BehaviorSubject, catchError, filter, Observable, switchMap, take, tap, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { AuthResponse } from '../../features/auth/models/auth-response';
import { LoginRequest } from '../../features/auth/models/login-request';
import { CarritoService } from './carrito/carrito-service';

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

  // Subject para evitar múltiples refresh simultáneos
  private refreshInProgress$ = new BehaviorSubject<boolean>(false);
  private refreshTokenSubject$ = new BehaviorSubject<string | null>(null);

  constructor() {
    this.loadStoredAuth();
  }

  updatePassword(dto: { currentPassword: string, newPassword: string }) {
    return this.http.patch<void>(`${environment.apiUrl}/usuarios/password`, dto);
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/login`, credentials, {
      withCredentials: true
    }).pipe(
      tap(response => this.handleAuthResponse(response))
    );
  }

  refreshToken(): Observable<AuthResponse> {
    // Si ya hay un refresh en progreso, esperar a que termine
    if (this.refreshInProgress$.value) {
      return this.refreshTokenSubject$.pipe(
        filter(token => token !== null),
        take(1),
        switchMap(() => {
          const userData = sessionStorage.getItem(this.USER_DATA_KEY);
          if (userData) {
            return new Observable<AuthResponse>(observer => {
              observer.next(JSON.parse(userData));
              observer.complete();
            });
          }
          return throwError(() => new Error('No hay user data despues del refresh'));
        })
      );
    }

    // Marcar que el refresh está en progreso
    this.refreshInProgress$.next(true);
    
    return this.http.post<AuthResponse>(`${this.API_URL}/refresh`, {}, {
      withCredentials: true
    }).pipe(
      tap(response => {
        this.handleAuthResponse(response);
        this.refreshTokenSubject$.next(response.accessToken);
        this.refreshInProgress$.next(false);
      }),
      catchError((error) => {
        this.refreshInProgress$.next(false);
        this.refreshTokenSubject$.next(null);
        this.clearAuth();
        return throwError(() => error);
      })
    );
  }

  logout(): void {
    this.http.post(`${this.API_URL}/logout`, {}, {
      withCredentials: true
    }).subscribe({
      next: () => {
        this.clearAuth();
        this.router.navigate(['/login']);
      },
      error: () => {
        this.clearAuth();
        this.router.navigate(['/login']);
      }
    });
  }

  logoutAllDevices(): void {
    this.http.post(`${this.API_URL}/logout-all`, {}, {
      withCredentials: true
    }).subscribe({
      next: () => {
        this.clearAuth();
        this.router.navigate(['/login']);
      },
      error: () => {
        this.clearAuth();
        this.router.navigate(['/login']);
      }
    });
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

  getCurrentUser(): AuthResponse | null {
    return this.currentUser();
  }

  private handleAuthResponse(response: AuthResponse): void {
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
                this.clearAuth();
              }
            });
          }
        } else {
          // Datos viejos sin timestamp, hacer refresh inmediato
          this.refreshToken().subscribe({
            error: () => {
              this.clearAuth();
            }
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