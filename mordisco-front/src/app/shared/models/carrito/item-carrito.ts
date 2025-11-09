import ImagenRequest from "../imagen/imagen-request";

export interface ItemCarrito {
  productoId: number
  nombre: string
  descripcion: string
  precio: number
  cantidad: number
  imagen: ImagenRequest
  restauranteId: number
  restauranteNombre: string
  disponible: boolean
}