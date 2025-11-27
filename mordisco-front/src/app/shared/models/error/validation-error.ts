export interface ValidationError {
  type: 'PEDIDOS_ACTIVOS' | 'GENERIC';
  cantidad?: number;
  message: string;
  entityType?: 'RESTAURANTE' | 'PRODUCTO' | 'USUARIO';
  entityId?: number;
}

export class ValidationErrorParser {
  
  static parse(error: any): ValidationError {
    const message = error?.error?.message || error?.message || 'Error desconocido';
    
    if (message.includes('pedido') && message.includes('activo')) {
      const match = message.match(/(\d+)\s+pedido/);
      const cantidad = match ? parseInt(match[1]) : 0;
      
      let entityType: 'RESTAURANTE' | 'PRODUCTO' | 'USUARIO' = 'RESTAURANTE';
      if (message.includes('producto') || message.includes('Producto')) {
        entityType = 'PRODUCTO';
      } else if (message.includes('cuenta') || message.includes('usuario')) {
        entityType = 'USUARIO';
      }
      
      return {
        type: 'PEDIDOS_ACTIVOS',
        cantidad,
        message,
        entityType
      };
    }
    
    return {
      type: 'GENERIC',
      message
    };
  }
}