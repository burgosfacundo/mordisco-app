
export default interface CalificacionPedidoResponseDTO{
    id : number,
    pedidoId : number,
    puntajeComida : number,
    puntajeTiempo : number,
    puntajePackaging : number,
    puntajePromedio : number,
    comentario : string,
    fechaHora : string,
    clienteNombre : string
}