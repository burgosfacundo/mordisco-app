import { ItemCarrito } from "./item-carrito";

export interface CarritoResumen {
  items: ItemCarrito[]
  subtotal: number
  costoEnvio: number
  total: number
  cantidadItems: number
  restauranteId: number | null
  restauranteNombre: string | null
}