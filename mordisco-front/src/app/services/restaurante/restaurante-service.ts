import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import RestauranteForCard from '../../models/restaurante/restaurante-for-card';
import Restaurante from '../../models/restaurante/restaurante';

@Injectable({
  providedIn: 'root'
})
export class RestauranteService {
  private http : HttpClient = inject(HttpClient)

  getAllActivosByCiudad(ciudad : string) : Observable<RestauranteForCard[]>{
    return this.http.get<RestauranteForCard[]>(`${environment.apiUrl}/restaurantes/ciudades?ciudad=${ciudad}`)
  }

  getAllWithPromocionActivaByCiudad(ciudad : string) : Observable<RestauranteForCard[]>{
    return this.http.get<RestauranteForCard[]>(`${environment.apiUrl}/restaurantes/promociones?ciudad=${ciudad}`)
  }

  getByUsuario(idUsuario : number): Observable<Restaurante>{
    return this.http.get<Restaurante>(`${environment.apiUrl}/restaurantes/usuarios/${idUsuario}`)
  }
}