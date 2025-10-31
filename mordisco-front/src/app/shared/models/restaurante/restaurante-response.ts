import DireccionResponse from "../direccion/direccion-response"
import CalificacionRestauranteReponse from "../calificacion/calificacion-restaurante-response"
import ImagenResponse from "../imagen/imagen-response"
import HorarioAtencion from "./horario-atencion"
import PromocionResponse from "./promocion-response"

export default interface RestauranteResponse {
    id: number
    razonSocial: string
    activo: boolean
    logo : ImagenResponse
    menuId : number
    promociones : PromocionResponse[]
    horariosDeAtencion : HorarioAtencion[]
    calificacionRestaurante : CalificacionRestauranteReponse[]
    estrellas : number
    direccion : DireccionResponse

}