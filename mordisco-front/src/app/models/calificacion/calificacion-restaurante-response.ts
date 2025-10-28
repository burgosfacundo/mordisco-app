import CalificacionDTO from "./calificacion-dto"

export default interface CalificacionRestauranteReponse {
    id : number
    fechaHora : string
    calificacion : CalificacionDTO
}