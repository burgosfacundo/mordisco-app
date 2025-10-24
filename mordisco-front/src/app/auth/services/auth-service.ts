import { HttpClient, HttpContext } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, Observable, map } from 'rxjs';
import { LoginRequestDto } from '../models/login-request-dto';
import { LoginResponseDto } from '../models/login-response-dto';
import { environment } from '../../../environments/environment';
import { JwtUser } from '../models/jwt-user';
import { AuthUtilsService } from './auth-utils-service';
import { SKIP_AUTH } from '../../core/context/auth-context';
import User from '../../models/user/user-register';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http  = inject(HttpClient);
  private utils = inject(AuthUtilsService);

  private _currentUser = new BehaviorSubject<JwtUser | null>(this.restoreUserFromStorage());
  currentUser$ = this._currentUser.asObservable();

  private restoreUserFromStorage(): JwtUser | null {
    const token = localStorage.getItem('accessToken');
    if (!token) return null;
  
    try {
      return this.utils.decodeJwtToUser(token);
    } catch {
      localStorage.removeItem('accessToken');
      return null;
    }
  }

  get currentUserValue(): JwtUser | null {
    return this._currentUser.value;
  }

  login(credentials: LoginRequestDto): Observable<JwtUser> {
    return this.http.post<LoginResponseDto>(
      `${environment.apiUrl}/auth/login`,
       credentials,
       { context: new HttpContext().set(SKIP_AUTH, true) }
      ).pipe(
      map(res => {
        if (!res.jwt) throw new Error('No se recibió token JWT');
        localStorage.setItem('accessToken', res.jwt);
        const user = this.utils.decodeJwtToUser(res.jwt);
        this._currentUser.next(user);
        return user;
      })
    );
  }

  logout(): void {
    localStorage.removeItem('accessToken');
    this._currentUser.next(null);
  }

  register(userData : User) : Observable<string>{
    return this.http.post(
      `${environment.apiUrl}/usuario/save`,
       userData, {
        context: new HttpContext().set(SKIP_AUTH,true),
        responseType: 'text' as const
      }
    )
  }

  updatePassword(dto : { passwordActual : string, passwordNueva : string }){
    return this.http.patch<void>(`${environment.apiUrl}/usuarios/password`,dto)
  }

  getToken(): string | null { return localStorage.getItem('accessToken'); }

  isAuthenticated(): boolean { return !!this.getToken() && !this.isTokenExpired(); }

  isTokenExpired(): boolean {
    const token = this.getToken();
    if (!token) return true;
    try {
      const claims = this.utils.decodeJwt(token);
      const now = Math.floor(Date.now() / 1000);
      return (claims.exp ?? 0) < now;
    } catch { return true; }
  }

  hasRole(roleName: string): boolean {
    const role = this._currentUser.value?.role?.nombre;
    return !!role && role === roleName;
}

  isAdmin(): boolean  { return this.hasRole('ROLE_ADMIN'); }
  isClient(): boolean { return this.hasRole('ROLE_CLIENT'); }
  isOwner(): boolean  { return this.hasRole('ROLE_OWNER'); }
}
