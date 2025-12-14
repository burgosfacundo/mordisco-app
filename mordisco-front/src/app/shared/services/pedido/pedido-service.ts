import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Observable } from 'rxjs';
import PaginationResponse from '../../models/pagination/pagination-response';
import { EstadoPedido } from '../../models/enums/estado-pedido';
import PedidoResponse from '../../models/pedido/pedido-response';
import { CrearPedidoRequest } from '../../models/pedido/crear-pedido-request';
import { MercadoPagoPreferenceResponse } from '../../models/pago/mercado-pago-preference-response';
import BajaLogisticaDTO from '../../models/BajaLogisticaRequestDTO';

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

  cancel(id : number, motivo?: string) : Observable<PedidoResponse>{
    return this.http.put<PedidoResponse>(`${environment.apiUrl}/pedidos/cancelar/${id}`, { motivo })
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
  
  marcarComoEntregado(pedidoId: number): Observable<void> {
    return this.http.put<void>(`${environment.apiUrl}/pedidos/${pedidoId}/entregar`, {});
  }

  getPedidosDisponibles(latitud: number, longitud: number, page: number = 0, size: number = 10): Observable<PaginationResponse<PedidoResponse>> {
    const params = new HttpParams()
      .set('latitud', latitud.toString())
      .set('longitud', longitud.toString())
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<PaginationResponse<PedidoResponse>>(
      `${environment.apiUrl}/pedidos/disponibles-repartidor`,
      { params }
    );
  }

  aceptarPedido(pedidoId: number): Observable<void> {
    return this.http.post<void>(
      `${environment.apiUrl}/pedidos/${pedidoId}/aceptar-repartidor`,
      {}
    );
  }

  darDeBaja(dto : BajaLogisticaDTO, idPedido : number){
    return this.http.post<PedidoResponse>(`${environment.apiUrl}/pedidos/${idPedido}/baja`, dto)
  }
  
  reactivar(idPedido : number){
    return this.http.post<void>(`${environment.apiUrl}/pedidos/${idPedido}/reactivar`, null)
  }

  filtrarPedidos(
    search: string,
    estado: string,
    tipoEntrega: string,
    fechaInicio: string,
    fechaFin: string,
    page: number,
    size: number
  ): Observable<PaginationResponse<PedidoResponse>> {

    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (search && search.trim() !== '') {
      params = params.set('search', search.trim());
    }
    if (estado && estado !== '') {
      params = params.set('estado', estado);
    }
    if (tipoEntrega && tipoEntrega !== '') {
      params = params.set('tipoEntrega', tipoEntrega);
    }
    if (fechaInicio && fechaInicio !== '') {
      params = params.set('fechaInicio', fechaInicio);
    }
    if (fechaFin && fechaFin !== '') {
      params = params.set('fechaFin', fechaFin);
    }
    return this.http.get<PaginationResponse<PedidoResponse>>(`${environment.apiUrl}/pedidos/buscar`, { params });
  }

filtrarPedidosRestaurante(
  id: number,
  search: string,
  estado: string,
  tipoEntrega: string,
  fechaInicio: string,
  fechaFin: string,
  page: number,
  size: number
): Observable<PaginationResponse<PedidoResponse>> {
  let params = new HttpParams()
    .set('page', page.toString())
    .set('size', size.toString());

  if (search && search.trim() !== '') {
    params = params.set('search', search.trim());
  }
  if (estado && estado !== '') {
    params = params.set('estado', estado);
  }
  if (tipoEntrega && tipoEntrega !== '') {
    params = params.set('tipoEntrega', tipoEntrega);
  }
  if (fechaInicio && fechaInicio !== '') {
    params = params.set('fechaInicio', fechaInicio);
  }
  if (fechaFin && fechaFin !== '') {
    params = params.set('fechaFin', fechaFin);
  }
  
  return this.http.get<PaginationResponse<PedidoResponse>>(
    `${environment.apiUrl}/pedidos/buscar-by-restaurante/${id}`, 
    { params }
  );
}

  filtrarPedidosClientes(
    id : number,
    search: string,
    estado: string,
    tipoEntrega: string,
    fechaInicio: string,
    fechaFin: string,
    page: number,
    size: number
  ): Observable<PaginationResponse<PedidoResponse>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (search && search.trim() !== '') {
      params = params.set('search', search.trim());
    }
    if (estado && estado !== '') {
      params = params.set('estado', estado);
    }    
    if (tipoEntrega && tipoEntrega !== '') {
      params = params.set('tipoEntrega', tipoEntrega);
    }
    if (fechaInicio && fechaInicio !== '') {
      params = params.set('fechaInicio', fechaInicio);
    }
    if (fechaFin && fechaFin !== '') {
      params = params.set('fechaFin', fechaFin);
    }
    return this.http.get<PaginationResponse<PedidoResponse>>(`${environment.apiUrl}/pedidos/buscar-by-cliente/${id}`, { params });
  }

  filtrarPedidosRepartidor(
    id : number,
    search: string,
    estado: string,
    fechaInicio: string,
    fechaFin: string,
    page: number,
    size: number
  ): Observable<PaginationResponse<PedidoResponse>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (search && search.trim() !== '') {
      params = params.set('search', search.trim());
    }
    if (estado && estado !== '') {
      params = params.set('estado', estado);
    }
    if (fechaInicio && fechaInicio !== '') {
      params = params.set('fechaInicio', fechaInicio);
    }
    if (fechaFin && fechaFin !== '') {
      params = params.set('fechaFin', fechaFin);
    }
    return this.http.get<PaginationResponse<PedidoResponse>>(`${environment.apiUrl}/pedidos/buscar-by-repartidor/${id}`, { params });
  }  



}
