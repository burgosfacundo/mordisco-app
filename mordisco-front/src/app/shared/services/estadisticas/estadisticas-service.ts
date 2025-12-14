import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { AdminEstadisticas } from '../../models/estadisticas/admin-estadisticas';
import { RestauranteEstadisticas } from '../../models/estadisticas/restaurante-estadisticas';
import { RepartidorEstadisticas } from '../../models/estadisticas/repartidor-estadisticas';

@Injectable({
  providedIn: 'root'
})
export class EstadisticasService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  getEstadisticasAdmin(): Observable<AdminEstadisticas> {
    return this.http.get<AdminEstadisticas>(`${this.apiUrl}/estadisticas/admin`);
  }

  getEstadisticasRestaurante(): Observable<RestauranteEstadisticas> {
    return this.http.get<RestauranteEstadisticas>(`${this.apiUrl}/estadisticas/restaurante`);
  }

  getEstadisticasRepartidor(): Observable<RepartidorEstadisticas> {
    return this.http.get<RepartidorEstadisticas>(`${this.apiUrl}/estadisticas/repartidor`);
  }
}
