import { ICalificacionBase } from "./calificacion-base";

export default interface CalificacionPedidoResponseDTO extends ICalificacionBase{
    puntajeComida : number,
    puntajeTiempo : number,
    puntajePackaging : number,
}