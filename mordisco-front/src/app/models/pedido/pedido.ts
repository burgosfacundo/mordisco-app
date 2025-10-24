import Producto from "../producto/producto"
import Restaurante from "../restaurante/restaurante"
import Address from "../direccion/direccion"
import User from "../user/user-register"

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