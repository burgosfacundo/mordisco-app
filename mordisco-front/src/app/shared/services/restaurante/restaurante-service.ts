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

  findById(id : number){
    return this.http.get<RestauranteResponse>(`${environment.apiUrl}/restaurantes/${id}`)
  }

  getAll(page : number ,size : number) : Observable<PaginationResponse<RestauranteForCard>>{
    const params = new HttpParams().set('page',page).set('size',size);
    return this.http.get<PaginationResponse<RestauranteForCard>>(`${environment.apiUrl}/restaurantes`, {params})
  }

  findByLocation(latitud: number, longitud: number, radioKm: number, searchTerm: string | null, page: number, size: number): Observable<PaginationResponse<RestauranteForCard>> {
    let params = new HttpParams()
      .set('latitud', latitud)
      .set('longitud', longitud)
      .set('radioKm', radioKm)
      .set('page', page)
      .set('size', size);
    
    if (searchTerm && searchTerm.trim()) {
      params = params.set('searchTerm', searchTerm.trim());
    }
    
    return this.http.get<PaginationResponse<RestauranteForCard>>(`${environment.apiUrl}/restaurantes/ubicacion`, {params});
  }

  findWithPromocionByLocation(latitud: number, longitud: number, radioKm: number, page: number, size: number): Observable<PaginationResponse<RestauranteForCard>> {
    const params = new HttpParams()
      .set('latitud', latitud)
      .set('longitud', longitud)
      .set('radioKm', radioKm)
      .set('page', page)
      .set('size', size);
    
    return this.http.get<PaginationResponse<RestauranteForCard>>(`${environment.apiUrl}/restaurantes/ubicacion/promociones`, {params});
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


  filtrarRestaurantes(
    search: string,
    activo: string,
    page: number,
    size: number
  ): Observable<PaginationResponse<RestauranteForCard>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (search && search.trim() !== '') {
      params = params.set('search', search.trim());
    }
    if (activo && activo !== '') {
      params = params.set('activo', activo);
    }

    return this.http.get<PaginationResponse<RestauranteForCard>>(
      `${environment.apiUrl}/restaurantes/buscar`, 
      { params }
    );
  }  
}