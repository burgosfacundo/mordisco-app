import { MetodoPago } from "../enums/metodo-pago";
import { TipoEntrega } from "../enums/tipo-entrega";
import ProductoPedidoRequest from "../producto/producto-pedido-request";

export interface CrearPedidoRequest {
  idCliente: number
  idRestaurante: number
  idDireccion?: number
  productos: ProductoPedidoRequest[]
  tipoEntrega: TipoEntrega
  metodoPago: MetodoPago
  comentarios?: string
}