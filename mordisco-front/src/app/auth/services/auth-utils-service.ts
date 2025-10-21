import { Injectable } from "@angular/core";
import { JwtClaims } from "../models/jwt-claims";
import { JwtUser } from "../models/jwt-user";

@Injectable({
  providedIn: 'root'
})
export class AuthUtilsService{

    // Método para decodificar JWT y obtener usuario
    public decodeJwtToUser(token: string): JwtUser {
    try {
      const claims = this.decodeJwt(token);
      return {
        id: claims.id,
        email: claims.email,
        role: {
          id: claims.role.id,
          nombre: claims.role.nombre
        }
      };
    } catch (error) {
      throw new Error('Token JWT inválido');
    }
  }

  // Método para decodificar JWT
  public decodeJwt(token: string): JwtClaims {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload);
    } catch (error) {
      throw new Error('Token JWT inválido');
    }
  }
}