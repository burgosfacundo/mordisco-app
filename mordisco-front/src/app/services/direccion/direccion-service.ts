import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import Direccion from '../../models/direccion/direccion';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DireccionService {

  private http : HttpClient = Inject(HttpClient)
  private direccionObservable = new BehaviorSubject<Direccion | null>(null)
  currentDir = this.direccionObservable.asObservable()

  getAll(id : number) : Observable<Direccion[]> {
    return this.http.get<Direccion[]>(`${environment.apiUrl}/usuarios/${id}/direcciones`)
  }

  createDireccion(id : number, direccion : Direccion){
    return this.http.post<Direccion>(`${environment.apiUrl}/usuarios/${id}/direcciones`, direccion)
  }

  updateDireccion(id : number, direccionUpd : Direccion){
    return this.http.put<Direccion>(`${environment.apiUrl}/usuarios/${id}/direcciones`, direccionUpd)
  }

  deleteDireccion (id : number, idDir : number){
    return this.http.delete<void>(`${environment.apiUrl}/usuarios/${id}/direcciones/${idDir}`)
  }


  /*Los dos metodos siguientes son para pasar informacion de un componente a otro
    de una forma mas robusta y escalable, porque los @input/@output no son tan eficientes */
 
  setDireccionToEdit( direccionAEditar : Direccion){
    this.direccionObservable.next(direccionAEditar)
  }

  clearDireccionToEdit(): void{
    this.direccionObservable.next(null)
  }
  
  
}
  


