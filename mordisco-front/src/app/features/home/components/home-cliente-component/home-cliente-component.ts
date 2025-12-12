import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { RestauranteCardComponent } from '../../../../shared/components/restaurante-card-component/restaurante-card-component';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import RestauranteForCard from '../../../../shared/models/restaurante/restaurante-for-card';
import { CarritoFlotanteComponent } from "../../../../shared/components/carrito-flotante-component/carrito-flotante-component";
import { ConfirmationService } from '../../../../core/services/confirmation-service';
import { AuthService } from '../../../../shared/services/auth-service';
import { Router } from '@angular/router';
import { GeolocationService } from '../../../../shared/services/geolocation/geolocation-service';
import { Ubicacion } from '../../../../shared/models/ubicacion/ubicacion.model';
import { ToastService } from '../../../../core/services/toast-service';

@Component({
  selector: 'app-home-cliente-component',
  imports: [MatPaginator, CarritoFlotanteComponent, RestauranteCardComponent],
  templateUrl: './home-cliente-component.html'
})
export class HomeClienteComponent  implements OnInit , OnDestroy{
  private eventListeners: (() => void)[] = [];
  private restauranteService = inject(RestauranteService);
  private geolocationService = inject(GeolocationService);
  private toastService = inject(ToastService);
  private confirmationService = inject(ConfirmationService);
  private authService = inject(AuthService)
  private router = inject(Router)

  // Ubicación del usuario o fallback
  private ubicacionActual?: Ubicacion;
  private readonly MAR_DEL_PLATA_CENTER: Ubicacion = { latitud: -38.0055, longitud: -57.5426 };
  private readonly RADIO_KM = 40;

  // Término de búsqueda actual
  private currentSearchTerm: string = '';

  // Restaurantes mostrados
  restaurantes?: RestauranteForCard[];
  restaurantesPromociones?: RestauranteForCard[];

  // Paginación para restaurantes regulares
  sizeRestaurantes: number = this.getPageSizeForScreenSize();
  pageRestaurantes: number = 0;
  lengthRestaurantes: number = 0;

  // Paginación para promociones
  sizePromocion: number = this.getPageSizeForScreenSize();
  pagePromocion: number = 0;
  lengthPromocion: number = 0;

  isLoadingRestaurantes = true;
  isLoadingPromocion = true;

  ngOnInit(): void {
    this.setupEventListeners();
    this.obtenerUbicacionYCargarRestaurantes();
  }

  ngOnDestroy(): void {
    this.eventListeners.forEach(cleanup => cleanup());
  }

  private getPageSizeForScreenSize(): number {
    const width = window.innerWidth;

    if (width >= 1280) {
      return 7; // xl
    } else if (width >= 1024) {
      return 6; // lg
    } else if (width >= 640) {
      return 4; // sm
    } else {
      return 1; // mobile
    }
  }

  private setupEventListeners(): void {
    const searchListener = (event: Event) => {
      const { detail } = event as CustomEvent<any>;
      const term = detail?.term ?? (typeof detail === 'string' ? detail : '');
      this.currentSearchTerm = term;
      this.pageRestaurantes = 0;
      this.loadRestaurantes();
    };

    const resizeListener = () => {
      const newSize = this.getPageSizeForScreenSize();
      if (newSize !== this.sizeRestaurantes || newSize !== this.sizePromocion) {
        this.sizeRestaurantes = newSize;
        this.sizePromocion = newSize;
        this.pageRestaurantes = 0;
        this.pagePromocion = 0;
        this.loadRestaurantes();
        this.loadRestaurantesPromociones();
      }
    };

    window.addEventListener('search-changed', searchListener);
    window.addEventListener('resize', resizeListener);

    this.eventListeners.push(
      () => window.removeEventListener('search-changed', searchListener),
      () => window.removeEventListener('resize', resizeListener)
    );
  }

  private obtenerUbicacionYCargarRestaurantes(): void {
    this.geolocationService.obtenerUbicacionActual().subscribe({
      next: (ubicacion) => {
        this.ubicacionActual = ubicacion;
        this.loadRestaurantes();
        this.loadRestaurantesPromociones();
      },
      error: (error) => {
        this.toastService.info(
          'No se pudo obtener tu ubicación. Se mostrarán restaurantes desde Mar del Plata.'
        );
        this.ubicacionActual = this.MAR_DEL_PLATA_CENTER;
        this.loadRestaurantes();
        this.loadRestaurantesPromociones();
      }
    });
  }

  private loadRestaurantes(): void {
    if (!this.ubicacionActual) return;

    this.isLoadingRestaurantes = true;
    
    this.restauranteService.findByLocation(
      this.ubicacionActual.latitud,
      this.ubicacionActual.longitud,
      this.RADIO_KM,
      this.currentSearchTerm || null,
      this.pageRestaurantes,
      this.sizeRestaurantes
    ).subscribe({
      next: (data) => {
        this.restaurantes = data.content;
        this.lengthRestaurantes = data.totalElements;
        this.isLoadingRestaurantes = false;
      },
      error: (error) => {
        this.toastService.error('Error al cargar restaurantes');
        this.isLoadingRestaurantes = false;
      }
    });
  }

  private loadRestaurantesPromociones(): void {
    if (!this.ubicacionActual) return;


    // Cargar restaurantes con promociones usando el endpoint de ubicación
    this.restauranteService.findWithPromocionByLocation(
      this.ubicacionActual.latitud,
      this.ubicacionActual.longitud,
      this.RADIO_KM,
      this.pagePromocion,
      this.sizePromocion
    ).subscribe({
      next: (data) => {
        this.restaurantesPromociones = data.content;
        this.lengthPromocion = data.totalElements;
        this.isLoadingPromocion = false;
      },
      error: (error) => {
        this.isLoadingPromocion = false;
      }
    });
  }

  onPageChangeRestaurante(event: PageEvent): void {
    this.pageRestaurantes = event.pageIndex;
    this.sizeRestaurantes = event.pageSize;
    this.loadRestaurantes();
  }

  onPageChangePromocion(event: PageEvent): void {
    this.pagePromocion = event.pageIndex;
    this.sizePromocion = event.pageSize;
    this.loadRestaurantesPromociones();
  }

  cuentaDesactivada() {
    this.confirmationService.confirm({
      title: 'Su cuenta esta bloqueada',
      message: 'Motivo: tal. Para ser desbloqueado envia un mail a mordisco@gmail.com',
      confirmText: 'Ok',
      type: 'danger',
      showCancelButton: false
    }).subscribe((confirmed) => {
      if (confirmed) this.authService.logout(); 
    });
  }

  verMenu(id : number){
    this.router.navigate(['/cliente/restaurante', id])
  }
}
