import DireccionResponse from "../direccion/direccion-response"
import ImagenResponse from "../imagen/imagen-response"

export default interface RestauranteResponse {
    id: number
    razonSocial: string
    activo: boolean
    logo : ImagenResponse
    menuId : number
    estrellas : number
    direccion : DireccionResponse
}