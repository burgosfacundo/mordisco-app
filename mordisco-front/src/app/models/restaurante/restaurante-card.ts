import HorarioAtencion from "./horario-atencion";

export default interface RestauranteCard {
    nombre : string,
    imagen : string,
    calificacion: number,
    horario : HorarioAtencion

}