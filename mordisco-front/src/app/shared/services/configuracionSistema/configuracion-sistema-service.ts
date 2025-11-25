import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import ConfiguracionSistemaResponseDTO from '../../models/configuracion/ConfiguracionSistemaResponseDTO';
import { environment } from '../../../../environments/environment';
import ConfiguracionSistemaRequestDTO from '../../models/configuracion/ConfiguracionSistemaRequestDTO';

@Injectable({
  providedIn: 'root'
})
export class ConfiguracionSistemaService {
  
  private http : HttpClient = inject(HttpClient)
  
  getConfiguracion() {
    return this.http.get<ConfiguracionSistemaResponseDTO>(`${environment.apiUrl}/configuraciones`)
  }
 
  actualizarConfiguracion(config : ConfiguracionSistemaRequestDTO){
    return this.http.put<ConfiguracionSistemaResponseDTO>(`${environment.apiUrl}/configuraciones`,config)
  }

  calcularCostoDelivery(distanciaKM : number){
    const params = new HttpParams().set('distanciaKm', distanciaKM)
    return this.http.get<number>(`${environment.apiUrl}/configuraciones/calcular-delivery`, {params})
  }

  getMontoMinimo(){
    return this.http.get<number>(`${environment.apiUrl}/configuraciones/monto-minimo`)
  }

  isEnMantenimiento(){
    return this.http.get<boolean>(`${environment.apiUrl}/configuraciones/mantenimiento`) 
  }

}