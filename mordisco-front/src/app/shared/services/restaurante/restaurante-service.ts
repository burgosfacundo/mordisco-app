import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import RestauranteForCard from '../../models/restaurante/restaurante-for-card';
import Restaurante from '../../models/restaurante/restaurante';
import PaginationResponse from '../../models/pagination/pagination-response';

@Injectable({
  providedIn: 'root'
})
export class RestauranteService {
  private http : HttpClient = inject(HttpClient)

  getAll(page : number ,size : number) : Observable<PaginationResponse<RestauranteForCard>>{
    const params = new HttpParams().set('page',page).set('size',size);
    return this.http.get<PaginationResponse<RestauranteForCard>>(`${environment.apiUrl}/restaurantes`, {params})
  }

  getAllActivosByCiudad(ciudad : string, page : number, size : number) : Observable<PaginationResponse<RestauranteForCard>>{
    const params = new HttpParams().set('page',page).set('size',size).set('ciudad',ciudad);
    return this.http.get<PaginationResponse<RestauranteForCard>>(`${environment.apiUrl}/restaurantes/ciudades`, {params})
  }

  getAllWithPromocionActivaByCiudad(ciudad : string, page : number , size : number) : Observable<PaginationResponse<RestauranteForCard>>{
    const params = new HttpParams().set('page',page).set('size',size).set('ciudad',ciudad);
    return this.http.get<PaginationResponse<RestauranteForCard>>(`${environment.apiUrl}/restaurantes/promociones`, {params})
  }

  getByUsuario(idUsuario : number): Observable<Restaurante> {
    return this.http.get<Restaurante>(`${environment.apiUrl}/restaurantes/usuarios/${idUsuario}`)
  }
}