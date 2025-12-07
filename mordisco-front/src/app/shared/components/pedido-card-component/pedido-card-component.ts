import { Component, inject, input, OnInit, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import PedidoResponse from '../../models/pedido/pedido-response';
import { 
  EstadoPedido, 
  ESTADO_PEDIDO_LABELS, 
  ESTADO_PEDIDO_COLORS, 
  ESTADO_PEDIDO_ICONS,
  getSiguienteEstado
} from '../../models/enums/estado-pedido';
import { TipoEntrega } from '../../models/enums/tipo-entrega';
import { ConfiguracionSistemaService } from '../../services/configuracionSistema/configuracion-sistema-service';
import ConfiguracionSistemaGeneralResponseDTO from '../../models/configuracion/configuracion-sistema-general-response-DTO';

@Component({
  selector: 'app-pedido-card-component',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pedido-card-component.html'
})
export class PedidoCardComponent implements OnInit{

  private configService = inject(ConfiguracionSistemaService)


  pedido = input<PedidoResponse>();
  isUsuario = input<string>();
  
  aceptarPedido = output<number>();
  rechazarPedido = output<number>();
  cambiarEstado = output<{ pedidoId: number, nuevoEstado: EstadoPedido }>();
  marcarRecibido = output<PedidoResponse>();
  verDetalles = output<number>();
  cancelar = output<number>()
  deshacerCancelacion = output<number>()
  configSistema? : ConfiguracionSistemaGeneralResponseDTO
  EstadoPedido = EstadoPedido;
  TipoEntrega = TipoEntrega;

  ngOnInit(): void {
    this.obtenerConfiguraciones()
  }

  obtenerConfiguraciones(){
    if(this.pedido()){
      this.configService.getConfiguracionGeneral().subscribe({
        next: (cs) => this.configSistema = cs,
        error: (e) => console.log("Error al cargar las configuraciones", e)
      })
    }
  }

  getEstadoBadgeClass(): string {
    const estado = this.pedido()?.estado as EstadoPedido;
    if (!estado) return 'inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold uppercase tracking-wide bg-gray-100 text-gray-700';
    
    const baseClasses = 'inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold uppercase tracking-wide';
    const colorClasses = ESTADO_PEDIDO_COLORS[estado] || 'bg-gray-100 text-gray-700';
    
    return `${baseClasses} ${colorClasses}`;
  }

  getEstadoLabel(): string {
    const estado = this.pedido()?.estado as EstadoPedido;
    return estado ? ESTADO_PEDIDO_LABELS[estado] : 'Desconocido';
  }

  getEstadoIcon(): string {
    const estado = this.pedido()?.estado as EstadoPedido;
    return estado ? ESTADO_PEDIDO_ICONS[estado] : 'help_outline';
  }

  getSiguienteEstadoLabel(): string | null {
    const pedido = this.pedido();
    if (!pedido) return null;
    
    const siguienteEstado = getSiguienteEstado(
      pedido.estado as EstadoPedido,
      pedido.tipoEntrega as TipoEntrega
    );
    return siguienteEstado ? ESTADO_PEDIDO_LABELS[siguienteEstado] : null;
  }

  onAceptarPedido(): void {
    if (this.pedido()?.id) {
      this.aceptarPedido.emit(this.pedido()!.id);
    }
  }

  onRechazarPedido(): void {
    if (this.pedido()?.id) {
      this.rechazarPedido.emit(this.pedido()!.id);
    }
  }

  onAvanzarEstado(): void {
    const pedido = this.pedido();
    if (!pedido?.id) return;
    
    const siguienteEstado = getSiguienteEstado(
      pedido.estado as EstadoPedido,
      pedido.tipoEntrega as TipoEntrega
    );
    
    if (siguienteEstado) {
      this.cambiarEstado.emit({ 
        pedidoId: pedido.id, 
        nuevoEstado: siguienteEstado 
      });
    }
  }

  onMarcarRecibido(): void {
    if (this.pedido()) {
      this.marcarRecibido.emit(this.pedido()!);
    }
  }

  onVerDetalles(): void {
    if (this.pedido()?.id) {
      this.verDetalles.emit(this.pedido()!.id);
    }
  }

  onCancelar(): void {
    if (this.pedido()?.id) {
      this.cancelar.emit(this.pedido()!.id);
    }
  }

  onDeshacerCancelacion(){
    if (this.pedido()?.id) {
      this.deshacerCancelacion.emit(this.pedido()!.id);
    }
  }

  mostrarBotonAvanzar(): boolean {
    const pedido = this.pedido();
    if (!pedido || this.isUsuario() !== 'ROLE_RESTAURANTE') return false;
    
    const estado = pedido.estado as EstadoPedido;
    return estado === EstadoPedido.EN_PREPARACION;
  }
}