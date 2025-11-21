export default interface CalificacionRepartidorResponseDTO{
    id : number,
    pedidoId : number,
    repartidorId : number,
    repartidorNombre : string,
    puntajeAtencion : string,
    puntajeComunicacion : number,
    puntajeProfesionalismo : number,
    puntajePromedio : number,
    comentario : string,
    fechaHora : string,
    clienteNombre : string
}      
