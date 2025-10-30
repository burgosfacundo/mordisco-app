import CalificacionRestauranteReponse from "../calificacion/calificacion-restaurante-response"
import Direccion from "../../features/direccion/models/direccion"
import ImagenResponse from "../imagen/imagen-response"
import HorarioAtencionResponse from "./horario-atencion-response"
import PromocionResponse from "./promocion-response"

export default interface Restaurante {
    id: number
    razonSocial: string
    activo: boolean
    logo : ImagenResponse
    menuId : number
    promociones : PromocionResponse[]
    horariosDeAtencion : HorarioAtencionResponse[]
    calificacionRestaurante : CalificacionRestauranteReponse[]
    estrellas : number
    direccion : Direccion

}