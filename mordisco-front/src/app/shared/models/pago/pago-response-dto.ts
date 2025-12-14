import { MetodoPago } from "../enums/metodo-pago";

export default interface PagoResponseDTO{
    id  : number,
    pedidoId : number,
    metodoPago : MetodoPago,
    monto : number,
    mercadoPagoPaymentId : string,
    mercadoPagoStus : string,
    fechaCreacion : string
}