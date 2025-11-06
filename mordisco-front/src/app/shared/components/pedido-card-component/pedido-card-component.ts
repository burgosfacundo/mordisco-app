import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import PedidoResponse from '../../models/pedido/pedido-response';

@Component({
  selector: 'app-pedido-card-component',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './pedido-card-component.html'
})
export class PedidoCardComponent {
  @Input() pedido?: PedidoResponse;
  @Input() isRestaurante: boolean = false;
  
  @Output() aceptarPedido = new EventEmitter<number>();
  @Output() rechazarPedido = new EventEmitter<number>();
  @Output() marcarEnCamino = new EventEmitter<number>();

  getEstadoBadgeClass(): string {
    const estado = this.pedido?.estado;
    
    const baseClasses = 'inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold uppercase tracking-wide';
    
    switch (estado) {
      case 'PENDIENTE':
        return `${baseClasses} bg-yellow-100 text-yellow-700`;
      case 'EN_PROCESO':
        return `${baseClasses} bg-blue-100 text-blue-700`;
      case 'EN_CAMINO':
        return `${baseClasses} bg-purple-100 text-purple-700`;
      case 'RECIBIDO':
        return `${baseClasses} bg-green-100 text-green-700`;
      case 'CANCELADO':
        return `${baseClasses} bg-red-100 text-red-700`;
      default:
        return `${baseClasses} bg-gray-100 text-gray-700`;
    }
  }

  getEstadoLabel(): string {
    const estado = this.pedido?.estado;
    
    switch (estado) {
      case 'PENDIENTE':
        return 'Pendiente';
      case 'EN_PROCESO':
        return 'En Proceso';
      case 'EN_CAMINO':
        return 'En Camino';
      case 'RECIBIDO':
        return 'Recibido';
      case 'CANCELADO':
        return 'Cancelado';
      default:
        return 'Desconocido';
    }
  }

  onAceptarPedido(): void {
    if (this.pedido?.id) {
      this.aceptarPedido.emit(this.pedido.id);
    }
  }

  onRechazarPedido(): void {
    if (this.pedido?.id) {
      this.rechazarPedido.emit(this.pedido.id);
    }
  }

  onMarcarEnCamino(): void {
    if (this.pedido?.id) {
      this.marcarEnCamino.emit(this.pedido.id);
    }
  }
}