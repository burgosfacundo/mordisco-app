import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import PaginationResponse from '../../models/pagination/pagination-response';
import { environment } from '../../../../environments/environment';
import RestauranteForCard from '../../models/restaurante/restaurante-for-card';
import RestauranteResponse from '../../models/restaurante/restaurante-response';
import RestauranteRequest from '../../models/restaurante/restaurante-request';
import RestauranteUpdate from '../../models/restaurante/restaurante-update';

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

  getByUsuario(idUsuario : number): Observable<RestauranteResponse> {
    return this.http.get<RestauranteResponse>(`${environment.apiUrl}/restaurantes/usuarios/${idUsuario}`)
  }

  save(restaurante : RestauranteRequest) : Observable<string>{
    return this.http.post<string>(`${environment.apiUrl}/restaurantes/save`,restaurante)
  }

  put(restaurante : RestauranteUpdate) : Observable<string>{
    return this.http.put<string>(`${environment.apiUrl}/restaurantes/update`,restaurante)
  }

  delete(id : number) : Observable<string>{
    return this.http.delete<string>(`${environment.apiUrl}/restaurantes/delete/${id}`)
  }
}