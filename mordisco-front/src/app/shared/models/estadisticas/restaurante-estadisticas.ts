export interface IngresosPorPeriodo {
  periodo: string;  // "2024-01", "2024-02", etc.
  ingresos: number;
}

export interface ProductoMasVendido {
  productoId: number | null;  // null si el producto fue eliminado
  nombre: string;
  cantidadVendida: number;
  ingresoGenerado: number;
}

export interface RestauranteEstadisticas {
  ingresosTotales: number;
  ingresosPorPeriodo: IngresosPorPeriodo[];
  productosMasVendidos: ProductoMasVendido[];
}
