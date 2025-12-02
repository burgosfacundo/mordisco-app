export interface IngresosPorPeriodo {
  periodo: string;  // "2024-01", "2024-02", etc.
  ingresos: number;
}

export interface ProductoMasVendido {
  productoId: number;
  nombre: string;
  cantidadVendida: number;
  ingresoGenerado: number;
}

export interface RestauranteEstadisticas {
  ingresosTotales: number;
  ingresosPorPeriodo: IngresosPorPeriodo[];
  productosMasVendidos: ProductoMasVendido[];
  tiempoPromedioPreparacion: number;  // en minutos
}
