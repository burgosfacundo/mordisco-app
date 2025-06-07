import { ImagenResponse } from "./ImagenResponse .model";

export interface ProductoResponse {
    id: number,
    nombre: string,
    descripcion: string,
    precio: number,
    disponible: boolean,
    imagen: ImagenResponse
}

export interface ProductoPedidoDTO{
    cantidad: number,
    productoId: number
}
