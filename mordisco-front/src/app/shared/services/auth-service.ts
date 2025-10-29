import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { AuthResponse } from '../../features/auth/models/auth-response';
import { LoginRequest } from '../../features/auth/models/login-request';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  
  private readonly API_URL = `${environment.apiUrl}/auth`;
  private readonly ACCESS_TOKEN_KEY = 'access_token';
  private readonly USER_DATA_KEY = 'user_data';
  
  // Signals para reactividad
  currentUser = signal<AuthResponse | null>(null);
  readonly isAuthenticated = signal(false);
  
  // Timer para renovar token antes de que expire
  private refreshTimer?: ReturnType<typeof setTimeout>;

  constructor() {
    console.log('🚀 AuthService constructor called');
    this.loadStoredAuth();
  }

  updatePassword(dto: { passwordActual: string, passwordNueva: string }) {
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
    return this.http.post<AuthResponse>(`${this.API_URL}/refresh`, {}, {
      withCredentials: true
    }).pipe(
      tap(response => this.handleAuthResponse(response))
    );
  }

  logout(): void {
    this.http.post(`${this.API_URL}/logout`, {}, {
      withCredentials: true
    }).subscribe({
      complete: () => this.clearAuth()
    });
  }

  logoutAllDevices(): void {
    this.http.post(`${this.API_URL}/logout-all`, {}, {
      withCredentials: true
    }).subscribe({
      complete: () => this.clearAuth()
    });
  }

  getAccessToken(): string | null {
    return sessionStorage.getItem(this.ACCESS_TOKEN_KEY);
  }

  getCurrentUser(): AuthResponse | null {
    return this.currentUser();
  }

  /**
   * Maneja la respuesta de autenticación y actualiza el estado
   */
  private handleAuthResponse(response: AuthResponse): void {
    // Guardar access token y datos de usuario en sessionStorage
    sessionStorage.setItem(this.ACCESS_TOKEN_KEY, response.accessToken);
    sessionStorage.setItem(this.USER_DATA_KEY, JSON.stringify(response));
    
    // Actualizar signals
    this.currentUser.set(response);
    this.isAuthenticated.set(true);
  
    
    // Programar renovación automática 1 minuto antes de expirar
    this.scheduleTokenRefresh(response.expiresIn);
  }

  /**
   * Programa la renovación automática del token
   */
  private scheduleTokenRefresh(expiresIn: number): void {
    // Limpiar timer anterior si existe
    if (this.refreshTimer) {
      clearTimeout(this.refreshTimer);
    }

    const expiresInSeconds = expiresIn > 100000 ? expiresIn / 1000 : expiresIn;
    
    const refreshTime = Math.max((expiresInSeconds - 60) * 1000, 30000);
    
    this.refreshTimer = setTimeout(() => {
      this.refreshToken().subscribe({
        error: () => {
          this.clearAuth();
        }
      });
    }, refreshTime);
  }

  /**
   * Carga el estado de autenticación almacenado al iniciar la app
   */
  private loadStoredAuth(): void {
    const token = this.getAccessToken();
    const userData = sessionStorage.getItem(this.USER_DATA_KEY);
    
    if (token && userData) {
      try {
        const user: AuthResponse = JSON.parse(userData);
        
        this.currentUser.set(user);
        this.isAuthenticated.set(true);
        
        this.scheduleTokenRefresh(user.expiresIn);
        
      } catch (error) {
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
    
    if (this.refreshTimer) {
      clearTimeout(this.refreshTimer);
      this.refreshTimer = undefined;
    }
  }
}