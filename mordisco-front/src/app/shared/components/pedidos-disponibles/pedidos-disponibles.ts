import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, OnDestroy, OnInit, output, Output, signal } from '@angular/core';
import { interval, startWith, Subscription, switchMap, catchError, of } from 'rxjs';
import PedidoResponse from '../../models/pedido/pedido-response';
import { PedidoService } from '../../services/pedido/pedido-service';
import { GeolocationService } from '../../services/geolocation/geolocation-service';
import { Ubicacion } from '../../models/ubicacion/ubicacion.model';
import { ConfirmationService } from '../../../core/services/confirmation-service';
import { ToastService } from '../../../core/services/toast-service';
import { ConfiguracionSistemaService } from '../../services/configuracionSistema/configuracion-sistema-service';
import ConfiguracionSistemaGeneralResponseDTO from '../../models/configuracion/configuracion-sistema-general-response-DTO';

interface PedidoConDistancia extends PedidoResponse {
  distanciaKm?: number;
  distanciaFormateada?: string;
}

@Component({
  selector: 'app-pedidos-disponibles',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pedidos-disponibles.html'
})
export class PedidosDisponiblesComponent implements OnInit, OnDestroy {
  private pedidoService = inject(PedidoService);
  private geolocationService = inject(GeolocationService);
  private toastService = inject(ToastService);
  private confirmationService = inject(ConfirmationService);
  private configService = inject(ConfiguracionSistemaService)
  pedidosDisponibles = signal<PedidoConDistancia[]>([]);
  isLoading = signal<boolean>(true);
  lastUpdate = signal<Date>(new Date());
  ubicacionActual = signal<Ubicacion | null>(null);
  errorUbicacion = signal<string | null>(null);
  configuracionGeneral? : ConfiguracionSistemaGeneralResponseDTO
  private refreshSubscription?: Subscription;

  @Output() pedidoAceptado = new EventEmitter<number>();
  verDetalles = output<number>();


  ngOnInit(): void {
    this.obtenerUbicacionYCargarPedidos();
  }

  ngOnDestroy(): void {
    this.refreshSubscription?.unsubscribe();
  }

  /**
   * Obtiene la ubicación del repartidor y luego carga los pedidos
   */
  private obtenerUbicacionYCargarPedidos(): void {
    if (!this.geolocationService.soportaGeolocation()) {
      this.errorUbicacion.set('Tu navegador no soporta geolocalización');
      this.isLoading.set(false);
      this.toastService.error('Tu navegador no soporta geolocalización');
      return;
    }

    this.geolocationService.obtenerUbicacionActual().subscribe({
      next: (ubicacion) => {
        this.ubicacionActual.set(ubicacion);
        this.errorUbicacion.set(null);
        this.iniciarAutoRefresh();
      },
      error: (error) => {
        this.errorUbicacion.set(error.message);
        this.isLoading.set(false);
        this.toastService.error(error.message);
      }
    });
  }

  /**
   * Inicia el auto-refresh cada 30 segundos
   */
  private iniciarAutoRefresh(): void {
    const ubicacion = this.ubicacionActual();
    if (!ubicacion) return;

    this.refreshSubscription = interval(30000) 
      .pipe(
        startWith(0),
        switchMap(() => this.cargarPedidosDisponibles(ubicacion)),
        catchError(error => {
          console.error('Error al cargar pedidos:', error);
          this.toastService.error('Error al cargar pedidos disponibles');
          return of({ content: [], totalElements: 0 });
        })
      )
      .subscribe({
        next: (response) => {
          const pedidosConDistancia = this.calcularDistancias(response.content, ubicacion);
          this.pedidosDisponibles.set(pedidosConDistancia);
          this.obtenerConfiguracionSistema();
          this.lastUpdate.set(new Date());
          this.isLoading.set(false);
        }
      });
  }

  /**
   * Carga los pedidos disponibles desde el servicio
   */
  private cargarPedidosDisponibles(ubicacion: Ubicacion) {
    return this.pedidoService.getPedidosDisponibles(
      ubicacion.latitud,
      ubicacion.longitud,
      0,
      20
    );
  }

  /**
   * Calcula las distancias y ordena los pedidos
   * Distancia TOTAL = (Repartidor → Restaurante) + (Restaurante → Dirección Entrega)
   */
  private calcularDistancias(pedidos: PedidoResponse[], ubicacionActual: Ubicacion): PedidoConDistancia[] {
    const pedidosConDistancia: PedidoConDistancia[] = pedidos.map(pedido => {
      let distanciaTotal = 0;

      // Validar que tengamos todas las coordenadas necesarias
      const tieneCoordRestaurante = pedido.restaurante?.direccion?.latitud && pedido.restaurante?.direccion?.longitud;
      const tieneCoordEntrega = pedido.direccionEntrega?.latitud && pedido.direccionEntrega?.longitud;

      if (tieneCoordRestaurante && tieneCoordEntrega) {
        // Distancia 1: Repartidor → Restaurante
        const distanciaAlRestaurante = this.geolocationService.calcularDistancia(
          ubicacionActual,
          {
            latitud: pedido.restaurante!.direccion!.latitud,
            longitud: pedido.restaurante!.direccion!.longitud
          }
        );

        // Distancia 2: Restaurante → Dirección de Entrega
        const distanciaEntrega = this.geolocationService.calcularDistancia(
          {
            latitud: pedido.restaurante!.direccion!.latitud,
            longitud: pedido.restaurante!.direccion!.longitud
          },
          {
            latitud: pedido.direccionEntrega!.latitud!,
            longitud: pedido.direccionEntrega!.longitud!
          }
        );

        // Distancia TOTAL del recorrido
        distanciaTotal = distanciaAlRestaurante + distanciaEntrega;
      }

      return {
        ...pedido,
        distanciaKm: distanciaTotal,
        distanciaFormateada: this.geolocationService.formatearDistancia(distanciaTotal)
      };
    });

    // Ordenar por distancia (más cercanos primero)
    return pedidosConDistancia.sort((a, b) => {
      if (!a.distanciaKm && !b.distanciaKm) return 0;
      if (!a.distanciaKm) return 1;
      if (!b.distanciaKm) return -1;
      return a.distanciaKm - b.distanciaKm;
    });
  }

  /**
   * Recarga manual de pedidos
   */
  recargarPedidos(): void {
    const ubicacion = this.ubicacionActual();
    if (!ubicacion) {
      this.obtenerUbicacionYCargarPedidos();
      return;
    }

    this.isLoading.set(true);
    this.cargarPedidosDisponibles(ubicacion).subscribe({
      next: (response) => {
        const pedidosConDistancia = this.calcularDistancias(response.content, ubicacion);
        this.pedidosDisponibles.set(pedidosConDistancia);
        this.lastUpdate.set(new Date());
        this.isLoading.set(false);
        this.toastService.success('Pedidos actualizados');
      },
      error: (error) => {
        console.error('Error al recargar pedidos:', error);
        this.isLoading.set(false);
      }
    });
  }

  /**
   * Acepta un pedido disponible
   */
  onAceptarPedido(pedido: PedidoConDistancia): void {
    const distanciaInfo = pedido.distanciaFormateada 
      ? `\n\nDistancia: ${pedido.distanciaFormateada}`
      : '';

    this.confirmationService.confirm({
      title: 'Aceptar Pedido',
      message: `¿Deseas aceptar el pedido #${pedido.id}?${distanciaInfo}`,
      type: 'info'
    }).subscribe(confirmed => {
      if (confirmed) {
        this.aceptarPedido(pedido.id);
      }
    });
  }

  /**
   * Llama al servicio para aceptar el pedido
   */
  private aceptarPedido(pedidoId: number): void {
    this.pedidoService.aceptarPedido(pedidoId).subscribe({
      next: () => {
        this.toastService.success(`Pedido #${pedidoId} aceptado exitosamente`);
        
        // Emitir evento para que el componente padre actualice
        this.pedidoAceptado.emit(pedidoId);
        
        // Recargar lista de pedidos disponibles
        this.recargarPedidos();
      },
      error: (error) => {
        console.error('Error al aceptar pedido:', error);
        // El error ya se maneja en el interceptor
      }
    });
  }
  
  onVerDetalles(pedido: PedidoConDistancia): void {
    if (pedido.id) {
      this.verDetalles.emit(pedido.id);
    }
  }

  /**
   * Calcula el tiempo transcurrido desde la última actualización
   */
  getTiempoDesdeActualizacion(): string {
    const ahora = new Date();
    const diff = Math.floor((ahora.getTime() - this.lastUpdate().getTime()) / 1000);
    
    if (diff < 60) return `Hace ${diff}s`;
    const minutos = Math.floor(diff / 60);
    return `Hace ${minutos}m`;
  }

  obtenerConfiguracionSistema(){
    this.configService.getConfiguracionGeneral().subscribe({
      next: (c)=> this.configuracionGeneral = c
     })
  }
}