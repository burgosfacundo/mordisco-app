import { Injectable } from '@angular/core';
import { Observable, from } from 'rxjs';
import { Ubicacion } from '../../models/ubicacion/ubicacion.model';

@Injectable({
  providedIn: 'root'
})
export class GeolocationService {

  /**
   * Obtiene la ubicación actual del dispositivo
   * @returns Observable con las coordenadas actuales
   */
  obtenerUbicacionActual(): Observable<Ubicacion> {
    return from(
      new Promise<Ubicacion>((resolve, reject) => {
        if (!navigator.geolocation) {
          reject(new Error('La geolocalización no está soportada por este navegador'));
          return;
        }

        navigator.geolocation.getCurrentPosition(
          (position) => {
            resolve({
              latitud: position.coords.latitude,
              longitud: position.coords.longitude
            });
          },
          (error) => {
            let errorMessage = 'Error al obtener la ubicación';
            
            switch (error.code) {
              case error.PERMISSION_DENIED:
                errorMessage = 'Permiso de ubicación denegado. Por favor, habilita los permisos de ubicación.';
                break;
              case error.POSITION_UNAVAILABLE:
                errorMessage = 'Información de ubicación no disponible.';
                break;
              case error.TIMEOUT:
                errorMessage = 'Tiempo de espera agotado al obtener la ubicación.';
                break;
            }
            
            reject(new Error(errorMessage));
          },
          {
            enableHighAccuracy: true,
            timeout: 10000,
            maximumAge: 0
          }
        );
      })
    );
  }

  /**
   * Calcula la distancia entre dos puntos geográficos usando la fórmula de Haversine
   * @param punto1 Primera ubicación
   * @param punto2 Segunda ubicación
   * @returns Distancia en kilómetros
   */
  calcularDistancia(punto1: Ubicacion, punto2: Ubicacion): number {
    const R = 6371; // Radio de la Tierra en kilómetros
    
    const dLat = this.toRadians(punto2.latitud - punto1.latitud);
    const dLon = this.toRadians(punto2.longitud - punto1.longitud);
    
    const a = 
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(this.toRadians(punto1.latitud)) * 
      Math.cos(this.toRadians(punto2.latitud)) *
      Math.sin(dLon / 2) * Math.sin(dLon / 2);
    
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    const distancia = R * c;
    
    return Math.round(distancia * 100) / 100; // Redondear a 2 decimales
  }

  /**
   * Convierte grados a radianes
   */
  private toRadians(grados: number): number {
    return grados * (Math.PI / 180);
  }

  /**
   * Verifica si el navegador soporta geolocalización
   */
  soportaGeolocation(): boolean {
    return 'geolocation' in navigator;
  }

  /**
   * Ordena un array de objetos por distancia desde una ubicación
   * @param items Array de objetos que tienen una propiedad de ubicación
   * @param ubicacionActual Ubicación desde donde calcular la distancia
   * @param getUbicacion Función para extraer la ubicación de cada item
   * @returns Array ordenado por distancia (más cercano primero)
   */
  ordenarPorDistancia<T>(
    items: T[],
    ubicacionActual: Ubicacion,
    getUbicacion: (item: T) => Ubicacion | null
  ): T[] {
    return items.sort((a, b) => {
      const ubicacionA = getUbicacion(a);
      const ubicacionB = getUbicacion(b);
      
      if (!ubicacionA && !ubicacionB) return 0;
      if (!ubicacionA) return 1;
      if (!ubicacionB) return -1;
      
      const distanciaA = this.calcularDistancia(ubicacionActual, ubicacionA);
      const distanciaB = this.calcularDistancia(ubicacionActual, ubicacionB);
      
      return distanciaA - distanciaB;
    });
  }

  /**
   * Formatea la distancia para mostrar en UI
   * @param distanciaKm Distancia en kilómetros
   * @returns String formateado (ej: "1.5 km" o "500 m")
   */
  formatearDistancia(distanciaKm: number): string {
    if (distanciaKm < 1) {
      const metros = Math.round(distanciaKm * 1000);
      return `${metros} m`;
    }
    return `${distanciaKm.toFixed(1)} km`;
  }
}
