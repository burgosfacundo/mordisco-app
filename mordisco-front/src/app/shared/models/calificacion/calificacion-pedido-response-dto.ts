import { ICalificacionBase } from "./ICalificacionBase";

export default interface CalificacionPedidoResponseDTO extends ICalificacionBase{
    puntajeComida : number,
    puntajeTiempo : number,
    puntajePackaging : number,
}