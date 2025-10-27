import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Ciudad } from '../../models/ciudad/ciudad';

@Injectable({
  providedIn: 'root'
})
export class CiudadService {
  private http = inject(HttpClient);
  private readonly STORAGE_KEY = 'mordisco_ciudad_seleccionada';

  ciudadSeleccionada = signal<Ciudad>({
    id: 1,
    nombre: 'Mar del Plata',
    provincia: 'Buenos Aires'
  });

  nombreCiudadSeleccionada = computed(() => 
    this.ciudadSeleccionada().nombre
  );

  constructor() {
    this.loadSavedCity();
  }

getCiudades(): Observable<Ciudad[]> {
    const url = 'https://apis.datos.gob.ar/georef/api/localidades-censales';
    const params = new HttpParams()
      .set('aplanar', 'true')
      .set('campos', 'id,nombre,provincia.nombre')
      .set('orden', 'nombre')
      .set('max', '5000');
    return this.http.get<any>(url, { params }).pipe(
      map(response =>
        (response?.localidades_censales || []).map((loc: any) => ({
          id: loc.id,
          nombre: loc.nombre,
          provincia: loc.provincia?.nombre || ''
        })) as Ciudad[]
      )
    );
  }

  setCiudad(ciudad: Ciudad): void {
    this.ciudadSeleccionada.set(ciudad);
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(ciudad));
    window.dispatchEvent(new CustomEvent('ciudad-changed', { detail: ciudad }));
  }

  private loadSavedCity(): void {
    const savedCity = localStorage.getItem(this.STORAGE_KEY);
    if (savedCity) {
      try {
        this.ciudadSeleccionada.set(JSON.parse(savedCity));
      } catch (error) {
        console.error('Error loading saved city:', error);
      }
    }
  }
}