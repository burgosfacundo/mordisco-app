import { Component, EventEmitter, inject, input, Input, output, Output } from '@angular/core';
import PedidoResponse from '../../../../shared/models/pedido/pedido-response';
import { EstadoPedido } from '../../../../shared/models/enums/estado-pedido';
import { TipoEntrega } from '../../../../shared/models/enums/tipo-entrega';
import { CommonModule } from '@angular/common';
import { MatIcon } from "@angular/material/icon";
import { CalificacionService } from '../../../../shared/services/calificacion/calificacion-service';

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
    const estado = this.pedidoResponse()?.estado;
    
    // Comparar con el enum correctamente
    if (estado === EstadoPedido.PENDIENTE) {
      return 'bg-yellow-500 text-white';
    } else if (estado === EstadoPedido.EN_PROCESO) {
      return 'bg-blue-500 text-white';
    } else if (estado === EstadoPedido.EN_CAMINO) {
      return 'bg-purple-500 text-white';
    } else if (estado === EstadoPedido.RECIBIDO) {
      return 'bg-green-500 text-white';
    } else if (estado === EstadoPedido.CANCELADO) {
      return 'bg-red-500 text-white';
    } else {
      return 'bg-gray-500 text-white';
    }
  }

  fromatearEstado(pedidoResponse : PedidoResponse){
    if (pedidoResponse.estado === EstadoPedido.PENDIENTE) {
      return 'Pendiente';
    } else if (pedidoResponse.estado === EstadoPedido.EN_PROCESO) {
      return 'En Proceso';
    } else if (pedidoResponse.estado === EstadoPedido.EN_CAMINO) {
      return 'En Camino';
    } else if (pedidoResponse.estado === EstadoPedido.RECIBIDO) {
      return 'Recibido';
    } else if (pedidoResponse.estado === EstadoPedido.CANCELADO) {
      return 'Cancelado';
    } else {
      return String(pedidoResponse.estado);
    }
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
    return estado === EstadoPedido.PENDIENTE || estado === EstadoPedido.EN_PROCESO;
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
