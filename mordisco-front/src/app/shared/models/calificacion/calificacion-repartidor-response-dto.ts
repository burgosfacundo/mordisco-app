import { ICalificacionBase } from "./ICalificacionBase";

export default interface CalificacionRepartidorResponseDTO extends ICalificacionBase{
    repartidorId : number,
    repartidorNombre : string,
    puntajeAtencion : string,
    puntajeComunicacion : number,
    puntajeProfesionalismo : number,

}      
