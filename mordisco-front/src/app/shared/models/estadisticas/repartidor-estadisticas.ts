export interface GananciasPorPeriodo {
  periodo: string;  // "2024-01", "2024-02", etc.
  ganancias: number;
}

export interface PedidosPorPeriodo {
  periodo: string;
  cantidad: number;
}

export interface RepartidorEstadisticas {
  gananciasTotales: number;
  gananciasPorPeriodo: GananciasPorPeriodo[];
  tiempoPromedioEntrega: number;  // en minutos
  pedidosPorDia: PedidosPorPeriodo[];
  pedidosPorSemana: PedidosPorPeriodo[];
  pedidosPorMes: PedidosPorPeriodo[];
}
