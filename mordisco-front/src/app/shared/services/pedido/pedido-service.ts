import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Observable } from 'rxjs';
import PaginationResponse from '../../models/pagination/pagination-response';
import { EstadoPedido } from '../../models/enums/estado-pedido';
import PedidoResponse from '../../models/pedido/pedido-response';
import { CrearPedidoRequest } from '../../models/pedido/crear-pedido-request';
import { MercadoPagoPreferenceResponse } from '../../models/pago/mercado-pago-preference-response';

@Injectable({
  providedIn: 'root'
})
export class PedidoService {
  private http: HttpClient = inject(HttpClient);

  getAll(page : number, size : number): Observable<PaginationResponse<PedidoResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PaginationResponse<PedidoResponse>>(`${environment.apiUrl}/pedidos`, { params });
  }


  findAllByRestaurante_Id(idRest : number, page: number, size: number){
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PaginationResponse<PedidoResponse>>(`${environment.apiUrl}/pedidos/restaurantes/${idRest}`, { params });
  }

  getAllByRestaurante_IdAndEstado(idRest : number, estado : string, page : number,size : number) : Observable<PaginationResponse<PedidoResponse>> {
    const params = new HttpParams().set('estado',estado).set('page', page).set('size', size);
    return this.http.get<PaginationResponse<PedidoResponse>>(`${environment.apiUrl}/pedidos/restaurantes/${idRest}/state`, { params });
  }

  getById(idPedido : number) : Observable<PedidoResponse>{ 
    return this.http.get<PedidoResponse>(`${environment.apiUrl}/pedidos/${idPedido}`)
  }

  changeState(id : number,estado : EstadoPedido) : Observable<string>{
    return this.http.put<string>(`${environment.apiUrl}/pedidos/state/${id}?nuevoEstado=${estado}`,{});
  }

  cancel(id : number) : Observable<string>{
    return this.http.put<string>(`${environment.apiUrl}/pedidos/cancelar/${id}`,null)
  }

  crearPedido(request: CrearPedidoRequest): Observable<MercadoPagoPreferenceResponse> {
    return this.http.post<MercadoPagoPreferenceResponse>(`${environment.apiUrl}/pedidos/save`,request)
  }

  getAllByCliente(clienteId: number, page: number, size: number): Observable<PaginationResponse<PedidoResponse>> {
    const params = new HttpParams().set('page', page).set('size', size)
    return this.http.get<PaginationResponse<PedidoResponse>>(`${environment.apiUrl}/pedidos/clientes/${clienteId}`,{ params })
  }

    getAllByClienteYEstado(clienteId: number, estado : EstadoPedido,page: number, size: number): Observable<PaginationResponse<PedidoResponse>> {
    const params = new HttpParams().set('estado', estado).set('page', page).set('size', size)
    return this.http.get<PaginationResponse<PedidoResponse>>(`${environment.apiUrl}/pedidos/clientes/${clienteId}/state`,{ params })
  }
}
