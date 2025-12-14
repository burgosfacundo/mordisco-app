export interface GananciaRepartidor {
  id: number;
  pedidoId: number;
  numeroPedido: string;
  costoDelivery: number;
  comisionPlataforma: number;
  gananciaRepartidor: number;
  porcentajeAplicado: number;
  fechaRegistro: string;
}

export interface TotalesGanancia {
  totalAcumulado: number;
  totalMes: number;
  totalSemana: number;
  promedioEntrega: number;
  cantidadEntregas: number;
}
