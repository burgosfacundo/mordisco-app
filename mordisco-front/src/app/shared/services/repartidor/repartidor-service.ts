import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import RepartidorResponseDTO from '../../models/repartidor/repartidor-response-dto';
import RepartidorEstadisticasDTO from '../../models/repartidor/repartidor-estadisticas-dto';
import PedidoResponse from '../../models/pedido/pedido-response';
import PaginationResponse from '../../models/pagination/pagination-response';

@Injectable({
  providedIn: 'root'
})
export class RepartidorService{

  private http : HttpClient = inject(HttpClient)

  getDisponiblesCercanos(latitud : number, longitud : number, radioKm : number) : Observable<RepartidorResponseDTO[]>{
    const params = new HttpParams().set('latitud', latitud).set('longitud', longitud).set('radioKm', radioKm);
    return this.http.get<RepartidorResponseDTO[]>(`${environment.apiUrl}/repartidores/disponibles-cercanos`,{ params });
  }

  getAllPedidosRepartidor(id : number, page : number, size : number) : Observable<PaginationResponse<PedidoResponse>>{
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PaginationResponse<PedidoResponse>>(`${environment.apiUrl}/repartidores/pedidos/${id}`, {params});
  }
  
  getPedidosAEntregar(id : number, page : number, size : number) : Observable<PaginationResponse<PedidoResponse>>{
    const params = new HttpParams().set('page', page).set('size', size);  
    return this.http.get<PaginationResponse<PedidoResponse>>(`${environment.apiUrl}/repartidores/a-entregar/${id}`, {params});
  }
  

  getEstadisticas(id : number) : Observable<RepartidorEstadisticasDTO>{
    return this.http.get<RepartidorEstadisticasDTO>(`${environment.apiUrl}/repartidores/${id}/estadisticas`)
  }

}  
  