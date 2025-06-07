import { ProductoPedidoDTO } from "./ProductoResponse.model";
import { TipoEntrega } from "./TipoEntrega.model";

export interface PedidoRequest{
    idCliente: number | undefined,
    idRestaurante: number,
    idDireccion: number,
    tipoEntrega: TipoEntrega,
    productos: ProductoPedidoDTO[]
}