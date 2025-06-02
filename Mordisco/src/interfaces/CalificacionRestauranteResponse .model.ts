import { Calificacion } from "./Calificacion.model";

export interface CalificacionRestauranteResponse {
    id: number;
    fechaHora: string; // formato ISO: "2023-10-01T12:00:00"
    calificacion: Calificacion;
}