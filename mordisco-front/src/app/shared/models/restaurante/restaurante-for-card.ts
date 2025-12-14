
import ImagenResponse from "../imagen/imagen-response";
import HorarioAtencion from "../horario/horario-atencion-request";

export default interface RestauranteForCard {
    id : number
    razonSocial : string
    activo : boolean
    logo : ImagenResponse
    horariosDeAtencion : HorarioAtencion[]
    estrellas: number
    latitud?: number
    longitud?: number
    estaAbierto?: boolean
}