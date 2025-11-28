import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { GananciaRepartidor, TotalesGanancia } from '../../models/ganancia/ganancia-repartidor.model';
import PaginationResponse from '../../models/pagination/pagination-response';

@Injectable({
  providedIn: 'root'
})
export class GananciaRepartidorService {

  constructor(private http: HttpClient) {}

  /**
   * Obtiene las ganancias de un repartidor
   */
  getGanancias(repartidorId: number, page: number = 0, size: number = 10): Observable<PaginationResponse<GananciaRepartidor>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PaginationResponse<GananciaRepartidor>>(
      `${environment.apiUrl}/ganancias/repartidor/${repartidorId}`,
      { params }
    );
  }

  /**
   * Obtiene ganancias en un rango de fechas
   */
  getGananciasEnRango(
    repartidorId: number,
    fechaInicio: Date,
    fechaFin: Date,
    page: number = 0,
    size: number = 10
  ): Observable<PaginationResponse<GananciaRepartidor>> {
    const params = new HttpParams()
      .set('fechaInicio', fechaInicio.toISOString())
      .set('fechaFin', fechaFin.toISOString())
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PaginationResponse<GananciaRepartidor>>(
      `${environment.apiUrl}/ganancias/repartidor/${repartidorId}/rango`,
      { params }
    );
  }

  /**
   * Obtiene los totales de ganancias de un repartidor
   */
  getTotales(repartidorId: number): Observable<TotalesGanancia> {
    return this.http.get<TotalesGanancia>(
      `${environment.apiUrl}/ganancias/repartidor/${repartidorId}/totales`
    );
  }

  /**
   * Obtiene las ganancias del repartidor autenticado
   */
  getMisGanancias(page: number = 0, size: number = 10): Observable<PaginationResponse<GananciaRepartidor>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PaginationResponse<GananciaRepartidor>>(
      `${environment.apiUrl}/ganancias/mis-ganancias`,
      { params }
    );
  }

  /**
   * Obtiene los totales del repartidor autenticado
   */
  getMisTotales(): Observable<TotalesGanancia> {
    return this.http.get<TotalesGanancia>(
      `${environment.apiUrl}/ganancias/mis-totales`
    );
  }
}
