
import ImagenResponse from "../imagen/imagen-response";
import HorarioAtencion from "./horario-atencion-response";

export default interface RestauranteForCard {
    id : number
    razonSocial : string
    activo : boolean
    logo : ImagenResponse
    horariosDeAtencion : HorarioAtencion
    estrellas: number
}