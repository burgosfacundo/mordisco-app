import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import Pedido from '../../models/pedido';

@Injectable({
  providedIn: 'root'
})
export class PedidoService {

  /**  readonly API_URL ="http://localhost:3000/api/pedido" /*API */

  readonly API_URL ="http://localhost:3000/pedido" /*JSON SERVER */

  constructor(private http:HttpClient){}

  getAllByRestaurante_IdAndEstado(idRest : number, estado : string){
   /* return this.http.get<Pedido[]>(`${this.API_URL}/${idRest}/${estado}`) API*/
   
  const url = `${this.API_URL}?estado=${estado}&restaurante.id=${idRest}`; /*JSON SERVER */
    return this.http.get<Pedido[]>(url);
  }
  
}
