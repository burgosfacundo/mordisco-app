export default interface ConfiguracionSistemaRequestDTO{
    comisionPlataforma : number,
    radioMaximoEntrega : number,
    tiempoMaximoEntrega : number,
    costoBaseDelivery :  number,
    costoPorKilometro : number,
    montoMinimoPedido : number,
    porcentajeGananciasRepartidor : number,
    modoMantenimiento : boolean,
    mensajeMantenimiento : string
}