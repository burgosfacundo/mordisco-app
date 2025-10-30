import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import Pedido from '../../../models/pedido/pedido';
import { environment } from '../../../../environments/environment';
import { Observable } from 'rxjs';
import PaginationResponse from '../../../models/pagination/pagination-response';

@Injectable({
  providedIn: 'root'
})
export class PedidoService {
  private http: HttpClient = inject(HttpClient);

  getAll(page : number, size : number): Observable<PaginationResponse<Pedido>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PaginationResponse<Pedido>>(`${environment.apiUrl}/pedidos`, { params });
  }

  getAllByRestaurante_IdAndEstado(idRest : number, estado : string, page : number,size : number) : Observable<PaginationResponse<Pedido>> {
    const params = new HttpParams().set('estado',estado).set('page', page).set('size', size);
    return this.http.get<PaginationResponse<Pedido>>(`${environment.apiUrl}/pedidos/restaurantes/${idRest}/state`, { params });
  }
}
