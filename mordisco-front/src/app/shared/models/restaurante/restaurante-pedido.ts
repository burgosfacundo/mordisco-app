import DireccionResponse from "../direccion/direccion-response"
import ImagenResponse from "../imagen/imagen-response"

export default interface RestaurantePedido {
    id: number
    razonSocial: string
    activo: boolean
    direccion : DireccionResponse
    logo : ImagenResponse
}