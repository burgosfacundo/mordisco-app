import Restaurante from "../restaurante/restaurante-response"
import UserPedido from "../user/user-pedido"
import ProductoResponse from "../producto/producto-response"
import { TipoEntrega } from "../enums/tipo-entrega"
import { EstadoPedido } from "../enums/estado-pedido"
import DireccionResponse from "../direccion/direccion-response"


export default interface PedidoResponse{
    id : number,
    cliente : UserPedido
    restaurante : Restaurante,
    productos : ProductoResponse[],
    tipoEntrega : TipoEntrega,
    estado : EstadoPedido,
    fechaHora : string,
    total : number,
    direccionEntrega : DireccionResponse
}