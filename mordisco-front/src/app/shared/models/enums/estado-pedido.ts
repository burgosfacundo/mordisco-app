import { TipoEntrega } from "./tipo-entrega";

export enum EstadoPedido {
  PENDIENTE = 'PENDIENTE',
  EN_PREPARACION = 'EN_PREPARACION',
  LISTO_PARA_RETIRAR = 'LISTO_PARA_RETIRAR',
  LISTO_PARA_ENTREGAR = 'LISTO_PARA_ENTREGAR',
  EN_CAMINO = 'EN_CAMINO',
  COMPLETADO = 'COMPLETADO',
  CANCELADO = 'CANCELADO'
}

export const ESTADO_PEDIDO_LABELS: Record<EstadoPedido, string> = {
  [EstadoPedido.PENDIENTE]: 'Pendiente',
  [EstadoPedido.EN_PREPARACION]: 'En Preparación',
  [EstadoPedido.LISTO_PARA_RETIRAR]: 'Listo para Retirar',
  [EstadoPedido.LISTO_PARA_ENTREGAR]: 'Listo para Entregar',
  [EstadoPedido.EN_CAMINO]: 'En Camino',
  [EstadoPedido.COMPLETADO]: 'Completado',
  [EstadoPedido.CANCELADO]: 'Cancelado'
};

export const ESTADO_PEDIDO_COLORS: Record<EstadoPedido, string> = {
  [EstadoPedido.PENDIENTE]: 'bg-yellow-100 text-yellow-700',
  [EstadoPedido.EN_PREPARACION]: 'bg-blue-100 text-blue-700',
  [EstadoPedido.LISTO_PARA_RETIRAR]: 'bg-green-100 text-green-700',
  [EstadoPedido.LISTO_PARA_ENTREGAR]: 'bg-purple-100 text-purple-700',
  [EstadoPedido.EN_CAMINO]: 'bg-indigo-100 text-indigo-700',
  [EstadoPedido.COMPLETADO]: 'bg-emerald-100 text-emerald-700',
  [EstadoPedido.CANCELADO]: 'bg-red-100 text-red-700'
};

export const ESTADO_PEDIDO_ICONS: Record<EstadoPedido, string> = {
  [EstadoPedido.PENDIENTE]: 'schedule',
  [EstadoPedido.EN_PREPARACION]: 'restaurant',
  [EstadoPedido.LISTO_PARA_RETIRAR]: 'shopping_bag',
  [EstadoPedido.LISTO_PARA_ENTREGAR]: 'inventory_2',
  [EstadoPedido.EN_CAMINO]: 'local_shipping',
  [EstadoPedido.COMPLETADO]: 'check_circle',
  [EstadoPedido.CANCELADO]: 'cancel'
};

/**
 * Obtiene los estados válidos según el tipo de entrega
 */
export function getEstadosValidosPorTipo(tipoEntrega: TipoEntrega): EstadoPedido[] {
  if (tipoEntrega === TipoEntrega.RETIRO_POR_LOCAL) {
    return [
      EstadoPedido.PENDIENTE,
      EstadoPedido.EN_PREPARACION,
      EstadoPedido.LISTO_PARA_RETIRAR,
      EstadoPedido.COMPLETADO,
      EstadoPedido.CANCELADO
    ];
  } else {
    return [
      EstadoPedido.PENDIENTE,
      EstadoPedido.EN_PREPARACION,
      EstadoPedido.LISTO_PARA_ENTREGAR,
      EstadoPedido.EN_CAMINO,
      EstadoPedido.COMPLETADO,
      EstadoPedido.CANCELADO
    ];
  }
}

/**
 * Obtiene el siguiente estado lógico según el tipo de entrega
 */
export function getSiguienteEstado(
  estadoActual: EstadoPedido, 
  tipoEntrega: TipoEntrega
): EstadoPedido | null {
  if (tipoEntrega === TipoEntrega.RETIRO_POR_LOCAL) {
    switch (estadoActual) {
      case EstadoPedido.PENDIENTE: return EstadoPedido.EN_PREPARACION;
      case EstadoPedido.EN_PREPARACION: return EstadoPedido.LISTO_PARA_RETIRAR;
      case EstadoPedido.LISTO_PARA_RETIRAR: return EstadoPedido.COMPLETADO;
      default: return null;
    }
  } else {
    switch (estadoActual) {
      case EstadoPedido.PENDIENTE: return EstadoPedido.EN_PREPARACION;
      case EstadoPedido.EN_PREPARACION: return EstadoPedido.LISTO_PARA_ENTREGAR;
      case EstadoPedido.LISTO_PARA_ENTREGAR: return EstadoPedido.EN_CAMINO;
      case EstadoPedido.EN_CAMINO: return EstadoPedido.COMPLETADO;
      default: return null;
    }
  }
}

/**
 * Verifica si se puede cambiar de un estado a otro
 */
export function puedeCambiarEstado(
  estadoActual: EstadoPedido,
  nuevoEstado: EstadoPedido,
  tipoEntrega: TipoEntrega
): boolean {
  // No puede cambiar si ya está completado o cancelado
  if (estadoActual === EstadoPedido.COMPLETADO || 
      estadoActual === EstadoPedido.CANCELADO) {
    return false;
  }

  // Puede cancelar en cualquier momento (excepto si ya está completado o cancelado, verificado arriba)
  if (nuevoEstado === EstadoPedido.CANCELADO) {
    return true;
  }

  // Solo puede avanzar al siguiente estado lógico
  const siguienteEstado = getSiguienteEstado(estadoActual, tipoEntrega);
  return nuevoEstado === siguienteEstado;
}

/**
 * Obtiene la descripción del estado según el contexto
 */
export function getEstadoDescripcion(
  estado: EstadoPedido,
  tipoEntrega: TipoEntrega,
  rol: 'CLIENTE' | 'RESTAURANTE' | 'REPARTIDOR'
): string {
  const descripciones: Record<string, Record<string, string>> = {
    [EstadoPedido.PENDIENTE]: {
      CLIENTE: 'Esperando confirmación del restaurante',
      RESTAURANTE: 'Nuevo pedido - requiere tu confirmación',
      REPARTIDOR: 'Pedido pendiente de preparación'
    },
    [EstadoPedido.EN_PREPARACION]: {
      CLIENTE: 'Tu pedido se está preparando',
      RESTAURANTE: 'Preparando pedido',
      REPARTIDOR: 'El restaurante está preparando el pedido'
    },
    [EstadoPedido.LISTO_PARA_RETIRAR]: {
      CLIENTE: '¡Tu pedido está listo! Puedes pasar a retirarlo',
      RESTAURANTE: 'Pedido listo - esperando que el cliente lo retire',
      REPARTIDOR: 'N/A'
    },
    [EstadoPedido.LISTO_PARA_ENTREGAR]: {
      CLIENTE: 'Tu pedido está listo para ser entregado',
      RESTAURANTE: 'Pedido listo - esperando repartidor',
      REPARTIDOR: 'Pedido listo para retirar del restaurante'
    },
    [EstadoPedido.EN_CAMINO]: {
      CLIENTE: '¡Tu pedido está en camino!',
      RESTAURANTE: 'El repartidor está llevando el pedido',
      REPARTIDOR: 'Entregando pedido al cliente'
    },
    [EstadoPedido.COMPLETADO]: {
      CLIENTE: tipoEntrega === TipoEntrega.RETIRO_POR_LOCAL 
        ? 'Pedido retirado exitosamente' 
        : 'Pedido entregado exitosamente',
      RESTAURANTE: 'Pedido completado',
      REPARTIDOR: 'Pedido entregado exitosamente'
    },
    [EstadoPedido.CANCELADO]: {
      CLIENTE: 'Pedido cancelado',
      RESTAURANTE: 'Pedido cancelado',
      REPARTIDOR: 'Pedido cancelado'
    }
  };

  return descripciones[estado]?.[rol] || 'Estado desconocido';
}