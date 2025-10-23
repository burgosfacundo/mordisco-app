import Producto from "./producto"
import Restaurante from "./restaurante"
import Address from "./address"
import User from "./user"

export default interface Pedido{
    id : number,
    cliente : User
    restaurante : Restaurante,
    productos : Producto[],
    tipoEntrega : string,
    estado : string,
    fechaHora : Date,
    total : number,
    direccionEntrega : Address
}