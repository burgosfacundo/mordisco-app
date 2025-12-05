export enum TipoDescuento {
  PORCENTAJE = 'PORCENTAJE',
  MONTO_FIJO = 'MONTO_FIJO'
}

export enum AlcancePromocion {
  TODO_MENU = 'TODO_MENU',
  PRODUCTOS_ESPECIFICOS = 'PRODUCTOS_ESPECIFICOS'
}

export default interface PromocionRequest {
  descripcion: string;
  descuento: number;
  tipoDescuento: TipoDescuento;
  alcance: AlcancePromocion;
  fechaInicio: string;
  fechaFin: string;
  activa?: boolean;
  productosIds?: number[];
  restauranteId: number;
}