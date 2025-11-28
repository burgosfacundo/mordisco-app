import { Component, inject, input, Input } from '@angular/core';
import PedidoResponse from '../../models/pedido/pedido-response';
import { ActivatedRoute } from '@angular/router';
import DireccionResponse from '../../models/direccion/direccion-response';
import { EstadoPedido } from '../../models/enums/estado-pedido';
import { TipoEntrega } from '../../models/enums/tipo-entrega';
import { PedidoService } from '../../services/pedido/pedido-service';
import { ToastService } from '../../../core/services/toast-service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-pedido-activo-component',
  imports: [FormsModule],
  templateUrl: './pedido-activo-component.html',
  styleUrl: './pedido-activo-component.css'
})
export class PedidoActivoComponent {
  pedidoId? = input<number>();
  private route = inject(ActivatedRoute);
  private pedidoService = inject(PedidoService);
  private toastService = inject(ToastService);

  
  pedido: PedidoResponse | null = null;
  isLoading = false;
  error: string | null = null;

  // Control de modales
  showModalEntregado = false;
  showModalCancelado = false;
  
  // Cancelación
  motivoCancelacion = '';
  otroMotivo = '';
  mostrarCampoOtroMotivo = false;
  
  // Estados para validación
  isProcessingEntrega = false;
  isProcessingCancelacion = false;

  // Enums para usar en el template
  readonly EstadoPedido = EstadoPedido;
  readonly TipoEntrega = TipoEntrega;

  // ============================================
  // MOTIVOS DE CANCELACIÓN PREDEFINIDOS
  // ============================================
  
  readonly MOTIVOS_CANCELACION = [
    'Cliente no responde',
    'Dirección incorrecta',
    'Cliente canceló el pedido',
    'Producto no disponible',
    'Problema con el pago',
    'Condiciones climáticas adversas',
    'Otro motivo'
  ];


  // ============================================
  // CICLO DE VIDA - INICIALIZACIÓN
  // ============================================
  
  ngOnInit(): void {
    // Obtener ID del pedido desde la ruta o desde el @Input
    const idFromRoute = this.route.snapshot.paramMap.get('id');
    const pedidoIdToLoad = this.pedidoId?.() || (idFromRoute ? +idFromRoute : null);

    if (pedidoIdToLoad) {
      this.cargarPedido(pedidoIdToLoad);
    } else {
      this.error = 'No se proporcionó un ID de pedido válido';
    }
  }

  // ============================================
  // CARGA DE DATOS
  // ============================================
  
  /**
   * Carga los datos del pedido desde el servicio
   */
  cargarPedido(id: number): void {
    this.isLoading = true;
    this.error = null;

    this.pedidoService.getById(id).subscribe({
      next: (pedido) => {
        this.pedido = pedido;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  /**
   * Recarga los datos del pedido
   */
  recargarPedido(): void {
    if (this.pedido) {
      this.cargarPedido(this.pedido.id);
    }
  }

  // ============================================
  // ACCIONES PRINCIPALES
  // ============================================
  
  /**
   * Abre el marcador telefónico del dispositivo para llamar al cliente
   */
  llamarCliente(): void {
    if (!this.pedido) return;

    const telefono = this.pedido.cliente.telefono.replace(/[^\d+]/g, '');
    window.location.href = `tel:${telefono}`;
  }

  /**
   * Abre Google Maps con la ubicación de la dirección de entrega
   */
  abrirEnGoogleMaps(): void {
    if (!this.pedido?.direccionEntrega) {
      this.toastService.info('No hay dirección de entrega disponible');
      return;
    }

    const { latitud, longitud } = this.pedido.direccionEntrega;
    const url = `https://www.google.com/maps/search/?api=1&query=${latitud},${longitud}`;
    
    window.open(url, '_blank');
    console.log('Abriendo Google Maps:', url);
  }

  // ============================================
  // MARCAR COMO ENTREGADO
  // ============================================
  
  /**
   * Abre el modal de confirmación de entrega
   */
  abrirModalEntregado(): void {
    if (!this.pedido) return;

    // Validar que el pedido esté en un estado válido para entrega
    if (this.pedido.estado === EstadoPedido.COMPLETADO) {
      this.toastService.info('Este pedido ya fue entregado');
      return;
    }

    if (this.pedido.estado === EstadoPedido.CANCELADO) {
      this.toastService.error('No se puede entregar un pedido cancelado');
      return;
    }

    this.showModalEntregado = true;
  }

  /**
   * Cierra el modal de confirmación de entrega
   */
  cerrarModalEntregado(): void {
    this.showModalEntregado = false;
  }

  /**
   * Confirma la entrega del pedido y actualiza el estado
   */
  confirmarEntrega(): void {
    if (!this.pedido || this.isProcessingEntrega) return;

    this.isProcessingEntrega = true;

    this.pedidoService.marcarComoEntregado(this.pedido.id).subscribe({
      next: () => {
        this.isProcessingEntrega = false;
        this.showModalEntregado = false;
        
        this.toastService.success('✅ Pedido marcado como entregado exitosamente');
      },
      error: () => {
        this.isProcessingEntrega = false;
      }
    });
  }

  // ============================================
  // MARCAR COMO CANCELADO
  // ============================================
  
  /**
   * Abre el modal de cancelación
   */
  abrirModalCancelado(): void {
    if (!this.pedido) return;

    // Validar que el pedido se pueda cancelar
    if (this.pedido.estado === EstadoPedido.COMPLETADO) {
      this.toastService.error('No se puede cancelar un pedido ya entregado');
      return;
    }

    if (this.pedido.estado === EstadoPedido.CANCELADO) {
      this.toastService.info('Este pedido ya está cancelado');
      return;
    }

    this.showModalCancelado = true;
  }

  /**
   * Cierra el modal de cancelación y resetea los valores
   */
  cerrarModalCancelado(): void {
    this.showModalCancelado = false;
    this.motivoCancelacion = '';
    this.otroMotivo = '';
    this.mostrarCampoOtroMotivo = false;
  }

  /**
   * Detecta cuando se selecciona "Otro motivo" para mostrar el campo de texto
   */
  onMotivoChange(): void {
    this.mostrarCampoOtroMotivo = this.motivoCancelacion === 'Otro motivo';
    if (!this.mostrarCampoOtroMotivo) {
      this.otroMotivo = '';
    }
  }

  /**
   * Confirma la cancelación del pedido
   */
  confirmarCancelacion(): void {
    if (!this.pedido || this.isProcessingCancelacion) return;

    // Validación del motivo
    const motivoFinal = this.motivoCancelacion === 'Otro motivo' 
      ? this.otroMotivo.trim() 
      : this.motivoCancelacion;

    if (!motivoFinal) {
      this.toastService.error('Por favor selecciona un motivo de cancelación');
      return;
    }

    this.isProcessingCancelacion = true;

    this.pedidoService.cancel(this.pedido.id, motivoFinal).subscribe({
      next: (pedidoActualizado) => {
        this.pedido = pedidoActualizado;
        this.isProcessingCancelacion = false;
        this.cerrarModalCancelado();
        
        this.toastService.success(`Pedido cancelado: ${motivoFinal}`);
        console.log('Pedido cancelado:', pedidoActualizado, 'Motivo:', motivoFinal);
      },
      error: () => {
        this.isProcessingCancelacion = false;
      }
    });
  }

  // ============================================
  // VALIDACIONES
  // ============================================
  
  /**
   * Verifica si el botón de Google Maps debe estar deshabilitado
   */
  isMapsButtonDisabled(): boolean {
    return !this.pedido?.direccionEntrega || 
           this.pedido.tipoEntrega === TipoEntrega.RETIRO_POR_LOCAL;
  }

  /**
   * Verifica si se puede confirmar la cancelación
   */
  puedeConfirmarCancelacion(): boolean {
    if (this.motivoCancelacion === 'Otro motivo') {
      return this.otroMotivo.trim().length > 0;
    }
    return this.motivoCancelacion.length > 0;
  }

  // ============================================
  // FUNCIONES DE FORMATEO
  // ============================================
  
  /**
   * Formatea una fecha ISO a formato local legible
   */
  formatearFecha(fecha: string): string {
    const date = new Date(fecha);
    return date.toLocaleString('es-AR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  /**
   * Formatea un número como precio en pesos argentinos
   */
 formatearPrecio(precio: number | undefined): string {
  if (!precio && precio !== 0) {
    return '$ 0,00';
  }
  
  return new Intl.NumberFormat('es-AR', {
    style: 'currency',
    currency: 'ARS',
    minimumFractionDigits: 0,
    maximumFractionDigits: 2
  }).format(precio);
}
  /**
   * Formatea una dirección completa con todos sus componentes
   */
  formatearDireccion(direccion: DireccionResponse | undefined): string {
    let direccionCompleta = `${direccion?.calle} ${direccion?.numero}`;
    
    if (direccion?.piso) {
      direccionCompleta += `, Piso ${direccion?.piso}`;
    }
    
    if (direccion?.depto) {
      direccionCompleta += ` Depto ${direccion?.depto}`;
    }
    
    return direccionCompleta;
  }

  /**
   * Obtiene el texto legible del estado del pedido
   */
obtenerTextoEstado(estado: EstadoPedido | null | undefined): string {
  if (!estado) {
    return 'Sin estado';
  }
  return estado.replace(/_/g, ' ');
}
  /**
   * Obtiene la clase CSS según el estado del pedido
   */
obtenerClaseEstado(estado?: EstadoPedido | null): string {
  // Validación ANTES de usar como índice
  if (!estado) {
    return 'bg-gray-100 text-gray-700';
  }
  
  const clases: Record<EstadoPedido, string> = {
    [EstadoPedido.PENDIENTE]: 'bg-yellow-100 text-yellow-700',
    [EstadoPedido.EN_PREPARACION]: 'bg-blue-100 text-blue-700',
    [EstadoPedido.LISTO_PARA_RETIRAR]: 'bg-green-100 text-green-700',
    [EstadoPedido.LISTO_PARA_ENTREGAR]: 'bg-purple-100 text-purple-700',
    [EstadoPedido.EN_CAMINO]: 'bg-indigo-100 text-indigo-700',
    [EstadoPedido.COMPLETADO]: 'bg-emerald-100 text-emerald-700',
    [EstadoPedido.CANCELADO]: 'bg-red-100 text-red-700'
  };
  
  return clases[estado] || 'bg-gray-100 text-gray-700';
}
  /**
   * Obtiene el emoji según el tipo de entrega
   */
  obtenerEmojiTipoEntrega(tipo: TipoEntrega | undefined): string {
    return tipo === TipoEntrega.DELIVERY ? '🚚' : '🏪';
  }

  /**
   * Obtiene el texto legible del tipo de entrega
   */
  obtenerTextoTipoEntrega(tipo: TipoEntrega | undefined): string {
    return tipo === TipoEntrega.DELIVERY ? 'Delivery' : 'Retiro en Local';
  }

  // ============================================
  // UTILIDADES
  // ============================================
  
  /**
   * Calcula el subtotal de un producto
   */
  calcularSubtotal(cantidad: number, precioUnitario: number): number {
    return cantidad * precioUnitario;
  }
}
