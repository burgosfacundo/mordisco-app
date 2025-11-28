import { HttpClient, HttpParams } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../../environments/environment";
import PaginationResponse from "../../models/pagination/pagination-response";
import EstadisticasRepartidorDTO from "../../models/calificacion/estadisticas-repartidor-dto";
import CalificacionRepartidorResponseDTO from "../../models/calificacion/calificacion-repartidor-response-dto";
import CalificacionRepartidorRequestDTO from "../../models/calificacion/calificacion-repartidor-request-dto";
import EstadisticasRestauranteDTO from "../../models/calificacion/estadisticas-restaurante-dto";
import CalificacionPedidoResponseDTO from "../../models/calificacion/calificacion-pedido-response-dto";
import CalificacionPedidoRequestDTO from "../../models/calificacion/calificacion-pedido-request-dto";

@Injectable({
  providedIn: 'root'
})
export class CalificacionService {
 private http : HttpClient = inject(HttpClient)

    calificarPedido(dto : CalificacionPedidoRequestDTO) : Observable<CalificacionPedidoResponseDTO>{
        return this.http.post<CalificacionPedidoResponseDTO>(`${environment.apiUrl}/calificaciones/pedido`, dto)
    }

    getCalificacionPedido(id : number) : Observable<CalificacionPedidoResponseDTO>{
        return this.http.get<CalificacionPedidoResponseDTO>(`${environment.apiUrl}/calificaciones/pedido/${id}`)
    }

    getCalificacionesRestaurante(id : number, page : number, size : number) : Observable<PaginationResponse<CalificacionPedidoResponseDTO>>{
        const params = new HttpParams().set('page', page).set('size', size)
        return this.http.get<PaginationResponse<CalificacionPedidoResponseDTO>>(`${environment.apiUrl}/calificaciones/restaurante/${id}`, {params})
    }

    getEstadisticasRestaurante(id: number) : Observable<EstadisticasRestauranteDTO>{
        return this.http.get<EstadisticasRestauranteDTO>(`${environment.apiUrl}/calificaciones/restaurante/${id}/estadisticas`)
    }
    
    eliminarCalificacionPedido(id: number) : Observable<void>{
        return this.http.delete<void>(`${environment.apiUrl}/calificaciones/pedido/delete/${id}`)
    }
    
    calificarRepartidor(dto : CalificacionRepartidorRequestDTO) : Observable<CalificacionRepartidorRequestDTO>{
        return this.http.post<CalificacionRepartidorRequestDTO>(`${environment.apiUrl}/calificaciones/repartidor`, dto)
    }

    getCalificacionesRepartidor(id : number, page : number, size : number) : Observable<PaginationResponse<CalificacionRepartidorResponseDTO>>{
        const params = new HttpParams().set('page', page).set('size', size)
        return this.http.get<PaginationResponse<CalificacionRepartidorResponseDTO>>(`${environment.apiUrl}/calificaciones/repartidor/${id}`, {params})
    }

    getCalificacionRepartidor(id : number) : Observable<CalificacionRepartidorResponseDTO>{
        return this.http.get<CalificacionRepartidorResponseDTO>(`${environment.apiUrl}/calificaciones/repartidor/pedido/${id}`)
    }

    getEstadisticasRepartidor(id : number) : Observable<EstadisticasRepartidorDTO>{
        return this.http.get<EstadisticasRepartidorDTO>(`${environment.apiUrl}/calificaciones/repartidor/${id}/estadisticas`)
    }

    eliminarCalificacionRepartidor(id : number) : Observable<void>{
        return this.http.delete<void>(`${environment.apiUrl}/calificaciones/repartidor/delete/${id}`)
    }

    getCalificacionesPedidosCliente(page : number, size : number, clienteId : number) : Observable<PaginationResponse<CalificacionPedidoResponseDTO>>{
        const params = new HttpParams().set('page', page).set('size', size)
        return this.http.get<PaginationResponse<CalificacionPedidoResponseDTO>>(`${environment.apiUrl}/calificaciones/cliente/pedido/${clienteId}`, {params})
    }

    getCalificacionesRepartidorCliente(page:number, size:number, clienteId:number) : Observable<PaginationResponse<CalificacionRepartidorResponseDTO>>{
        const params = new HttpParams().set('page', page).set('size', size)
        return this.http.get<PaginationResponse<CalificacionRepartidorResponseDTO>>(`${environment.apiUrl}/calificaciones/cliente/repartidor/${clienteId}`, {params})
    }

}