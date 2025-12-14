import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import PagoResponseDTO from '../../models/pago/pago-response-dto';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PagoService {

  private http : HttpClient = inject(HttpClient)

  getPagoByPedidoId(idPedido : number){
    return this.http.get<PagoResponseDTO>(`${environment.apiUrl}/pedidos/pagos/${idPedido}`)
  }
  
}
