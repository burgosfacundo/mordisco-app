import { HttpClient, HttpContext } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { AuthResponse } from '../models/auth-response';
import { LoginRequest } from '../models/login-request';
import UserRegister from '../../models/user/user-register';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient)
  private router = inject(Router)
  
  private readonly API_URL = `${environment.apiUrl}/auth`;
  private readonly ACCESS_TOKEN_KEY = 'access_token'
  
  // Signals para reactividad
  currentUser = signal<AuthResponse | null>(null)
  isAuthenticated = signal(false)
  
  // Timer para renovar token antes de que expire
  private refreshTimer?: ReturnType<typeof setTimeout>

  constructor() {
    this.loadStoredToken()
  }


  register(userData : UserRegister) : Observable<string>{
    return this.http.post(
      `${environment.apiUrl}/usuarios/save`,
       userData, { responseType: 'text' as const }
    )
  }

  updatePassword(dto : { passwordActual : string, passwordNueva : string }){
    return this.http.patch<void>(`${environment.apiUrl}/usuarios/password`,dto)
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/login`, credentials, {
      withCredentials: true
    }).pipe(
      tap(response => this.handleAuthResponse(response))
    )
  }

  refreshToken(): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/refresh`, {}, {
      withCredentials: true
    }).pipe(
      tap(response => this.handleAuthResponse(response))
    )
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

  private handleAuthResponse(response: AuthResponse): void {
    // Guardar access token en sessionStorage (NO localStorage)
    sessionStorage.setItem(this.ACCESS_TOKEN_KEY, response.accessToken)
    
    this.currentUser.set(response)
    this.isAuthenticated.set(true)
    
    // Programar renovación automática 1 minuto antes de expirar
    this.scheduleTokenRefresh(response.expiresIn)
  }

  private scheduleTokenRefresh(expiresIn: number): void {
    // Limpiar timer anterior si existe
    if (this.refreshTimer) {
      clearTimeout(this.refreshTimer)
    }

    // Renovar 60 segundos antes de expirar
    const refreshTime = (expiresIn - 60) * 1000;
    
    this.refreshTimer = setTimeout(() => {
      this.refreshToken().subscribe({
        error: () => this.clearAuth()
      });
    }, refreshTime)
  }

  private loadStoredToken(): void {
    const token = this.getAccessToken();
    if (token) {
      // Verificar si el token es válido haciendo un request
      this.refreshToken().subscribe({
        error: () => this.clearAuth()
      })
    }
  }

  private clearAuth(): void {
    sessionStorage.removeItem(this.ACCESS_TOKEN_KEY)
    this.currentUser.set(null)
    this.isAuthenticated.set(false)
    
    if (this.refreshTimer) {
      clearTimeout(this.refreshTimer)
    }
    
    this.router.navigate(['/login'])
  }
}