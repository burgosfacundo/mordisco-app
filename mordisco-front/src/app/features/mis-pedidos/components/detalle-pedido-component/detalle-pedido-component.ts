import { Component,input,output } from '@angular/core';
import PedidoResponse from '../../../../shared/models/pedido/pedido-response';

import { TipoEntrega } from '../../../../shared/models/enums/tipo-entrega';
import { CommonModule } from '@angular/common';
import { MatIcon } from "@angular/material/icon";
import { 
  EstadoPedido, 
  ESTADO_PEDIDO_LABELS, 
  ESTADO_PEDIDO_COLORS, 
  ESTADO_PEDIDO_ICONS,
  getSiguienteEstado
} from '../../../../shared/models/enums/estado-pedido';
@Component({
  selector: 'app-detalle-pedido-component',
  imports: [CommonModule, MatIcon],
  templateUrl: './detalle-pedido-component.html'
})
export class DetallePedidoComponent {
  pedidoResponse = input<PedidoResponse>();
  onCancelar = output<number>();
  onVerDetalles = output<number>();

  readonly tipoEntregaEnum = TipoEntrega;
  readonly estadoPedidoEnum = EstadoPedido;

  getEstadoClasses(): string {
    const estado = this.pedidoResponse()?.estado as EstadoPedido;
    if (!estado) return 'bg-gray-500 text-white';
    
    const colorClasses = ESTADO_PEDIDO_COLORS[estado] || 'bg-gray-100 text-gray-700';

    return colorClasses.replace('bg-', 'bg-').replace('-100', '-500').replace('text-', 'text-').replace('-700', '-white');
  }

  getEstadoBadgeClass(): string {
    const estado = this.pedidoResponse()?.estado as EstadoPedido;
    if (!estado) return 'inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold uppercase tracking-wide bg-gray-100 text-gray-700';
    
    const baseClasses = 'inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold uppercase tracking-wide';
    const colorClasses = ESTADO_PEDIDO_COLORS[estado] || 'bg-gray-100 text-gray-700';
    
    return `${baseClasses} ${colorClasses}`;
  }

  getEstadoLabel(): string {
    const estado = this.pedidoResponse()?.estado as EstadoPedido;
    return estado ? ESTADO_PEDIDO_LABELS[estado] : 'Desconocido';
  }

  getEstadoIcon(): string {
    const estado = this.pedidoResponse()?.estado as EstadoPedido;
    return estado ? ESTADO_PEDIDO_ICONS[estado] : 'help_outline';
  }

  formatearEntrega(pedidoResponse : PedidoResponse){
    if(pedidoResponse.tipoEntrega === TipoEntrega.DELIVERY){
      return 'Delivery'
    } else{
      return 'Retiro en el local'
    }
  }

  /**
   * Construye la dirección completa como string
   */
  obtenerDireccionCompleta(): string {
    const dir = this.pedidoResponse()?.direccionEntrega;
    if (!dir) return '';

    let direccion = `${dir.calle} ${dir.numero}`;
    
    if (dir.piso) {
      direccion += `, Piso ${dir.piso}`;
    }
    
    if (dir.depto) {
      direccion += ` ${dir.depto}`;
    }
    
    direccion += ` - ${dir.ciudad} (CP: ${dir.codigoPostal})`;
    
    return direccion;
  }

  /**
   * Determina si debe mostrar el botón de cancelar
   */
  mostrarBotonCancelar(): boolean {
    const estado = this.pedidoResponse()?.estado;
    return estado === EstadoPedido.PENDIENTE || estado === EstadoPedido.EN_PREPARACION;
  }

  /**
   * Determina si debe mostrar el botón de ver detalles
   */
  mostrarBotonDetalles(): boolean {
    return true;
  }

  /**
   * Emite evento para cancelar pedido
   */
  cancelarPedido(): void {
    if (this.pedidoResponse()?.id) {
      this.onCancelar.emit(this.pedidoResponse()!.id);
    }
  }

  /**
   * Emite evento para ver detalles
   */
  verDetalles(): void {
    if (this.pedidoResponse()?.id) {
      this.onVerDetalles.emit(this.pedidoResponse()!.id);
    }
  }
}
