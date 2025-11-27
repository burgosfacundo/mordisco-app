import { ICalificacionBase } from "./calificacion-base";

export default interface CalificacionRepartidorResponseDTO extends ICalificacionBase{
    repartidorId : number,
    repartidorNombre : string,
    puntajeAtencion : number,
    puntajeComunicacion : number,
    puntajeProfesionalismo : number,

}      
