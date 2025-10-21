import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, Observable, map } from 'rxjs';
import { LoginRequestDto } from '../models/login-request-dto';
import { LoginResponseDto } from '../models/login-response-dto';
import { environment } from '../../../environments/environment';// Interfaces para los claims del JW
import { JwtUser } from '../models/jwt-user';
import { JwtClaims } from '../models/jwt-claims';
import { AuthUtilsService } from './auth-utils-service';
import User from '../../models/user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http : HttpClient = inject(HttpClient);
  private utils : AuthUtilsService = inject(AuthUtilsService)
  private _currentUser = new BehaviorSubject<JwtUser | null>(this.getCurrentUser())
  currentUser$ = this._currentUser.asObservable()

  login(credentials: LoginRequestDto): Observable<JwtUser> {
    return this.http.post<LoginResponseDto>(`${environment.apiUrl}/auth/login`, credentials)
      .pipe(
        map((response: LoginResponseDto) => {
          // Guardar el JWT en localStorage
          if (response.jwt) {
            localStorage.setItem('accessToken', response.jwt);
            // Decodificar el JWT y retornar el usuario
            return this.utils.decodeJwtToUser(response.jwt);
          }
          throw new Error('No se recibió token JWT');
        })
      );
  }

  register(userData : User) : Observable<string>{
    return this.http.post(`${environment.apiUrl}/usuario/save`, userData, {
      responseType: 'text' as const ,
    })
  }

  // Método para verificar si el usuario está autenticado
  isAuthenticated(): boolean {
    const token = localStorage.getItem('accessToken');
    return !!token;
  }

  // Método para cerrar sesión
  logout(): void {
    localStorage.removeItem('accessToken');
  }

  // Método para obtener el token actual
  getToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  
  // Método para obtener el usuario actual desde el token
  getCurrentUser(): JwtUser | null {
    const token = this.getToken();
    if (!token) return null;

    try {
      return this.utils.decodeJwtToUser(token);
    } catch (error) {
      // Si el token es inválido, limpiarlo
      this.logout();
      return null;
    }
  }

  // Método para verificar si el token está expirado
  isTokenExpired(): boolean {
    const token = this.getToken();
    if (!token) return true;

    try {
      const claims = this.utils.decodeJwt(token);
      const currentTime = Math.floor(Date.now() / 1000);
      return claims.exp < currentTime;
    } catch (error) {
      return true;
    }
  }

  // Método para verificar si el usuario tiene un rol específico
  hasRole(roleName: string): boolean {
    const user = this.getCurrentUser();
    if (!user) return false;
    return user.role.nombre === roleName;
  }

  // Método para verificar si es admin
  isAdmin(): boolean {
    return this.hasRole('ROLE_ADMIN');
  }

  // Método para verificar si es cliente
  isClient(): boolean {
    return this.hasRole('ROLE_CLIENT');
  }

  // Método para verificar si es dueño
  isOwner(): boolean {
    return this.hasRole('ROLE_OWNER');
  }
}
