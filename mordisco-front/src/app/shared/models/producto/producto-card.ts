import ImagenResponse from "../imagen/imagen-response";

export default interface ProductoCard {
    id: number,
    nombre: string,
    imagen: ImagenResponse,
    precio: number
}