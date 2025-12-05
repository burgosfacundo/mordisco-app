import ImagenRequest from "../imagen/imagen-request";

export interface ItemCarrito {
  productoId: number
  nombre: string
  descripcion: string
  precio: number
  precioConDescuento?: number  // Precio con descuento si hay promoción
  cantidad: number
  imagen: ImagenRequest
  restauranteId: number
  restauranteNombre: string
  disponible: boolean
}