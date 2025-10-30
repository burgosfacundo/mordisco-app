import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RestauranteService } from '../../../../services/restaurante/restaurante-service';
import RestauranteForCard from '../../../../models/restaurante/restaurante-for-card';
import { RestauranteCard } from '../../../../shared/components/restaurante-card-component/restaurante-card-component';
import { MatPaginator, PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'app-home-cliente-component',
  imports: [RestauranteCard, MatPaginator],
  templateUrl: './home-cliente-component.html',
  styleUrl: './home-cliente-component.css'
})
export class HomeClienteComponent  implements OnInit , OnDestroy{
  private eventListeners: (() => void)[] = [];
  private _snackBar = inject(MatSnackBar);
  private restauranteService = inject(RestauranteService);
  private originalRestaurantes?: RestauranteForCard[];
  restaurantes?: RestauranteForCard[];
  restaurantesPromociones?: RestauranteForCard[];
  ciudadSeleccionada?: string = 'Mar del Plata';


  sizePromocion : number = 10;
  pagePromocion : number = 0;
  lengthPromocion : number = 10;

  sizeRestaurantes : number = 10;
  pageRestaurantes : number = 0;
  lengthRestaurantes : number = 10;

  isLoadingRestaurantes = true;
  isLoadingPromocion = true;

  ngOnInit(): void {
    this.setupEventListeners();
    this.loadRestaurantesData('Mar del Plata');
    this.loadRestaurantesPromocionesData('Mar del Plata');
  }

    ngOnDestroy(): void {
    this.eventListeners.forEach(cleanup => cleanup());
  }

  private setupEventListeners(): void {
    const ciudadListener = (event: Event) => {
      const { detail } = event as CustomEvent<any>;
      this.ciudadSeleccionada = typeof detail === 'string' ? detail : detail?.nombre;
      if (this.ciudadSeleccionada) {
        this.loadRestaurantesData(this.ciudadSeleccionada);
        this.loadRestaurantesPromocionesData(this.ciudadSeleccionada);
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
    this.restauranteService.getAllActivosByCiudad(ciudad,this.pageRestaurantes,this.sizeRestaurantes).subscribe({
      next: data => { 
        this.originalRestaurantes = data.content;
        this.restaurantes = [...data.content];
        this.lengthRestaurantes = data.totalElements;
        this.isLoadingRestaurantes = false;
        
      },
      error: () => {
        this._snackBar.open('❌ Error al cargar los restaurantes','Cerrar' , { duration: 3000 });
      }
    });
  }

  private loadRestaurantesPromocionesData(ciudad : string): void {
    this.restauranteService.getAllWithPromocionActivaByCiudad(ciudad,this.pagePromocion,this.sizePromocion).subscribe({
      next: data => {
       this.restaurantesPromociones = data.content
        this.lengthPromocion = data.totalElements;
        this.isLoadingPromocion = false;
      },
      error: () => {
        this._snackBar.open('❌ Error al cargar los restaurantes con promociones', 'Cerrar' , { duration: 3000 });
      }
    });
  }


  onPageChangeRestaurante(event: PageEvent): void {
    this.pageRestaurantes = event.pageIndex
    this.sizeRestaurantes = event.pageSize;
    if (this.ciudadSeleccionada){
      this.loadRestaurantesData(this.ciudadSeleccionada);
    }
  }

    onPageChangePromocion(event: PageEvent): void {
    this.pagePromocion = event.pageIndex
    this.sizePromocion = event.pageSize;
    if (this.ciudadSeleccionada){
      this.loadRestaurantesPromocionesData(this.ciudadSeleccionada);
    }
  }
}
