import { MetodoPago } from "../enums/metodo-pago";

export type EstadoPago = 'PENDIENTE' | 'APROBADO' | 'RECHAZADO' | 'CANCELADO' | 'REEMBOLSADO';

export default interface PagoResponseDTO{
    id  : number,
    pedidoId : number,
    metodoPago : MetodoPago,
    monto : number,
    estado : EstadoPago,
    mercadoPagoPaymentId : string | null,
    mercadoPagoStatus : string | null,
    fechaCreacion : string
}