export default interface ConfiguracionSistemaResponseDTO{
        id : number,
        comisionPlataforma : number,
        radioMaximoEntrega : number,
        tiempoMaximoEntrega : number,
        costoBaseDelivery : number,
        costoPorKilometro : number,
        montoMinimoPedido: number,
        porcentajeGananciasRepartidor : number,
        modoMantenimiento : boolean,
        mensajeMantenimiento : string,
        fechaActualizacion : string,
        emailUsuarioModificacion : string
}