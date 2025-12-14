import ImagenResponse from "../imagen/imagen-response"

export default interface ProductoResponse {
  id: number;
  idMenu: number;
  nombre: string;
  descripcion: string;
  precio: number;
  precioConDescuento?: number;
  porcentajeDescuento?: number;
  tienePromocion?: boolean;
  descripcionPromocion?: string;
  disponible: boolean;
  imagen: ImagenResponse;
}