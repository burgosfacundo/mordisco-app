import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { interval, startWith, Subscription, switchMap } from 'rxjs';
import PedidoResponse from '../../models/pedido/pedido-response';
import { PedidoService } from '../../services/pedido/pedido-service';
import { EstadoPedido } from '../../models/enums/estado-pedido';

@Component({
  selector: 'app-pedidos-disponibles',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './pedidos-disponibles.html'

})
export class PedidosDisponiblesComponent implements OnInit, OnDestroy {
  pedidosDisponibles = signal<PedidoResponse[]>([]);
  isLoading = signal<boolean>(true);
  lastUpdate = signal<Date>(new Date());
  private refreshSubscription?: Subscription;

  constructor( private pService : PedidoService
    // private ubicacionService: UbicacionService
  ) {}

  ngOnInit(): void {
    this.iniciarAutoRefresh();
  }

  ngOnDestroy(): void {
    this.refreshSubscription?.unsubscribe();
  }

  /**
   * Inicia el auto-refresh cada 30 segundos
   */
  private iniciarAutoRefresh(): void {
    this.refreshSubscription = interval(30000) 
      .pipe(
        startWith(0),
        switchMap(() => this.cargarPedidosDisponibles())
      )
      .subscribe({
        next: (pedidos) => {
          this.pedidosDisponibles.set(pedidos);
          this.lastUpdate.set(new Date());
          this.isLoading.set(false);
        },
        error: (error) => {
          console.error('Error al cargar pedidos:', error);
          this.isLoading.set(false);
        }
      });
  }

  /**
   * Carga los pedidos disponibles desde el servicio
   */
  private async cargarPedidosDisponibles(): Promise<PedidoResponse[]> {
    // llamada real al servicio
    // const ubicacionActual = await this.ubicacionService.obtenerUbicacionActual();
    // return this.pService.obtenerPedidosDisponibles(ubicacionActual);

    // Datos de ejemplo para demostración
    return this.obtenerPedidosMock();
  }

  /**
   * Recarga manual de pedidos
   */
  recargarPedidos(): void {
    this.isLoading.set(true);
    this.cargarPedidosDisponibles().then(pedidos => {
      this.pedidosDisponibles.set(pedidos);
      this.lastUpdate.set(new Date());
      this.isLoading.set(false);
    });
  }

  /**
   * Acepta un pedido disponible
   */
  onAceptarPedido(pedido: PedidoResponse): void {
    if (confirm(`¿Deseas aceptar el pedido #${pedido.id}?`)) {
      // Aquí irá la llamada al servicio
      // this.pedidosService.aceptarPedido(pedido.id).subscribe({
      //   next: () => {
      //     this.recargarPedidos();
      //   }
      // });
      
      console.log('Pedido aceptado:', pedido.id);
      // Recargar lista después de aceptar
      this.recargarPedidos();
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

  private obtenerPedidosMock(): PedidoResponse[] {
    return [
   
    ];
  }
}