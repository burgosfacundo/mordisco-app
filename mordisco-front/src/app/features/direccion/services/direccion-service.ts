import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import DireccionRequest from '../../../shared/models/direccion/direccion-request';
import { Observable } from 'rxjs';
import DireccionResponse from '../../../shared/models/direccion/direccion-response';

@Injectable({
  providedIn: 'root'
})
export class DireccionService {
  private http : HttpClient = inject(HttpClient)

  getAll(id : number) : Observable<DireccionResponse[]> {
    return this.http.get<DireccionResponse[]>(`${environment.apiUrl}/usuarios/${id}/direcciones`)
  }

  createDireccion(id : number, direccion : DireccionRequest){
    return this.http.post<DireccionRequest>(`${environment.apiUrl}/usuarios/${id}/direcciones`, direccion)
  }

  updateDireccion(idUsuario : number, direccionUpd : DireccionRequest){
    return this.http.put<DireccionRequest>(`${environment.apiUrl}/usuarios/${idUsuario}/direcciones`, direccionUpd)
  }

  deleteDireccion (id : number, idDir : number){
    return this.http.delete<void>(`${environment.apiUrl}/usuarios/${id}/direcciones/${idDir}`)
  }
}
  


