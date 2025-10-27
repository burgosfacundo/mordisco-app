import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RestauranteService } from '../../../services/restaurante/restaurante-service';
import RestauranteForCard from '../../../models/restaurante/restaurante-for-card';
import { RestauranteCard } from '../../restaurante-card/restaurante-card';

@Component({
  selector: 'app-home-cliente',
  imports: [RestauranteCard],
  templateUrl: './home-cliente.html',
  styleUrl: './home-cliente.css'
})
export class HomeCliente  implements OnInit , OnDestroy{
  private eventListeners: (() => void)[] = [];
  private _snackBar = inject(MatSnackBar);
  private restauranteService = inject(RestauranteService);
  private originalRestaurantes?: RestauranteForCard[];
  restaurantes?: RestauranteForCard[];
  restaurantesPromociones?: RestauranteForCard[];

  isLoading = true;

  ngOnInit(): void {
    this.setupEventListeners();
    this.loadRestaurantesData('Mar del Plata');
  }

    ngOnDestroy(): void {
    this.eventListeners.forEach(cleanup => cleanup());
  }

  private setupEventListeners(): void {
    const ciudadListener = (event: Event) => {
      const { detail } = event as CustomEvent<any>;
      console.log('Ciudad cambiada:', detail);
      const ciudadNombre = typeof detail === 'string' ? detail : detail?.nombre;
      if (ciudadNombre) {
        this.loadRestaurantesData(ciudadNombre);
      }
    };

    const searchListener = (event: Event) => {
      const { detail } = event as CustomEvent<any>;
      const term = detail?.term ?? (typeof detail === 'string' ? detail : '');
      this.filterRestaurantes(term);
    };

    window.addEventListener('ciudad-changed', ciudadListener);
    window.addEventListener('search-changed', searchListener);

    this.eventListeners.push(
      () => window.removeEventListener('ciudad-changed', ciudadListener),
      () => window.removeEventListener('search-changed', searchListener)
    );
  }

  private filterRestaurantes(searchTerm: string): void {
    if (!searchTerm || !searchTerm.trim()) {
      if (this.originalRestaurantes) {
        this.restaurantes = [...this.originalRestaurantes];
      }
      return;
    }
    const term = searchTerm.toLowerCase();
    const baseList = this.originalRestaurantes ?? this.restaurantes ?? [];
    this.restaurantes = baseList.filter(r =>
      r.razonSocial.toLowerCase().includes(term)
    );
  }


  private loadRestaurantesData(ciudad : string): void {
    this.restauranteService.getAllActivosByCiudad(ciudad).subscribe({
      next: data => { 
        this.originalRestaurantes = data;
        this.restaurantes = [...data];
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error al cargar los restaurantes:', err);
        this.openSnackBar('❌ Error al cargar los restaurantes');
        this.isLoading = false;
      }
    });

    this.restauranteService.getAllWithPromocionActivaByCiudad(ciudad).subscribe({
      next: data => {
       this.restaurantesPromociones = data
      },
      error: (err) => {
        console.error('Error al cargar los restaurantes con promociones:', err);
        this.openSnackBar('❌ Error al cargar los restaurantes con promociones');
      }
    });
  }

  private openSnackBar(message: string, action: string = 'Cerrar'): void {
    this._snackBar.open(message, action, { duration: 3000 });
  }

}
