import { TipoDescuento, AlcancePromocion } from './promocion-request';

export default interface PromocionResponse {
  id: number;
  descripcion: string;
  descuento: number;
  tipoDescuento: TipoDescuento;
  alcance: AlcancePromocion;
  fechaInicio: string;
  fechaFin: string;
  activa: boolean;
  productosIds: number[];
}