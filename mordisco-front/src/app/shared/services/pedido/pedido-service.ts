import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Observable } from 'rxjs';
import PaginationResponse from '../../models/pagination/pagination-response';
import { EstadoPedido } from '../../models/enums/estado-pedido';
import PedidoResponse from '../../models/pedido/pedido-response';

@Injectable({
  providedIn: 'root'
})
export class PedidoService {
  private http: HttpClient = inject(HttpClient);

  getAll(page : number, size : number): Observable<PaginationResponse<PedidoResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PaginationResponse<PedidoResponse>>(`${environment.apiUrl}/pedidos`, { params });
  }

  fildAllByRestaurante_Id(idRest : number, page: number, size: number){
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PaginationResponse<PedidoResponse>>(`${environment.apiUrl}/pedidos/restaurantes/${idRest}`, { params });
  }

  getAllByRestaurante_IdAndEstado(idRest : number, estado : string, page : number,size : number) : Observable<PaginationResponse<PedidoResponse>> {
    const params = new HttpParams().set('estado',estado).set('page', page).set('size', size);
    return this.http.get<PaginationResponse<PedidoResponse>>(`${environment.apiUrl}/pedidos/restaurantes/${idRest}/state`, { params });
  }

  changeState(id : number,estado : EstadoPedido) : Observable<string>{
    return this.http.put<string>(`${environment.apiUrl}/pedidos/state/${id}?nuevoEstado=${estado}`,{});
  }

  cancel(id : number) : Observable<string>{
    return this.http.put<string>(`${environment.apiUrl}/pedidos/cancelar/${id}`,null)
  }
}
