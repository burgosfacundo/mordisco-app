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
  private http = inject(HttpClient);

  getMisDirecciones(): Observable<DireccionResponse[]> {
    return this.http.get<DireccionResponse[]>(`${environment.apiUrl}/usuarios/me/direcciones`);
  }

  getByUsuarioId(id: number): Observable<DireccionResponse[]> {
    return this.http.get<DireccionResponse[]>(`${environment.apiUrl}/usuarios/${id}/direcciones`);
  }

  save(direccion: DireccionRequest): Observable<DireccionResponse> {
    return this.http.post<DireccionResponse>(`${environment.apiUrl}/usuarios/me/direcciones`, direccion);
  }

  update(id: number, direccion: DireccionRequest): Observable<DireccionResponse> {
    return this.http.put<DireccionResponse>(`${environment.apiUrl}/usuarios/me/direcciones/${id}`, direccion);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/usuarios/me/direcciones/${id}`);
  }
}