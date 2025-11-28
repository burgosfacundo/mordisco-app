import Restaurante from "../restaurante/restaurante-response"
import UserPedido from "../user/user-pedido"
import { TipoEntrega } from "../enums/tipo-entrega"
import { EstadoPedido } from "../enums/estado-pedido"
import DireccionResponse from "../direccion/direccion-response"
import ProductoPedidoResponse from "../producto/producto-pedido-response"


export default interface PedidoResponse{
    id : number,
    cliente : UserPedido
    restaurante : Restaurante,
    productos : ProductoPedidoResponse[],
    tipoEntrega : TipoEntrega,
    estado : EstadoPedido,
    fechaHora : string,
    total: number,
    direccionEntrega?: DireccionResponse,
    direccionSnapshot?: string,
    costoDelivery?: number,
    distanciaKm?: number,
    subtotalProductos?: number
}