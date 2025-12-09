export interface UsuariosTotales {
  clientes: number;
  restaurantes: number;
  repartidores: number;
  total: number;
}

export interface MetodoPagoEstadistica {
  metodoPago: string;
  cantidad: number;
  porcentaje: number;
}

export interface RestauranteMasActivo {
  restauranteId: number;
  nombre: string;
  pedidosCompletados: number;
  ingresoGenerado: number;
}

export interface RepartidorMasActivo {
  repartidorId: number;
  nombre: string;
  entregasRealizadas: number;
  gananciaGenerada: number;
}

export interface AdminEstadisticas {
  usuariosTotales: UsuariosTotales;
  totalPedidos: number;
  comisionRestaurantes: number;
  comisionDelivery: number;
  ingresosTotalesPlataforma: number;
  metodosPagoMasUsados: MetodoPagoEstadistica[];
  restaurantesMasActivos: RestauranteMasActivo[];
  repartidoresMasActivos: RepartidorMasActivo[];
}
