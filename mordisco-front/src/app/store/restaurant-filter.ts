import { Injectable, signal } from '@angular/core';
import { LocalidadCensal } from '../models/ciudad/localidad-censal';

export interface RestaurantFilters {
  name: string;
  city: LocalidadCensal | null;
}

const LS_KEY = 'mordisco.nav.filters';

@Injectable({ providedIn: 'root' })
export class RestaurantFilterStore {
  private _filters = signal<RestaurantFilters>({ name: '', city: null });

  filters = this._filters.asReadonly();

  constructor() {
    const raw = localStorage.getItem(LS_KEY);
    if (raw) {
      try {
        const parsed = JSON.parse(raw) as RestaurantFilters;
        this._filters.set({
          name: typeof parsed.name === 'string' ? parsed.name : '',
          city: parsed.city && typeof parsed.city === 'object' ? parsed.city : null
        });
      } catch {
        // Ignore parse errors
      }
    }
    this.persist();
  }

  setName(name: string) {
    this._filters.update(f => ({ ...f, name }));
    this.persist();
  }

  setCity(city: LocalidadCensal | null) {
    this._filters.update(f => ({ ...f, city }));
    this.persist();
  }

  private persist() {
    localStorage.setItem(LS_KEY, JSON.stringify(this._filters()));
  }
}
