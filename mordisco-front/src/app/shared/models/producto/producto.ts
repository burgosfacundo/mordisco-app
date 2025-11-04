import ImagenResponse from "../imagen/imagen-response"

export default interface Producto{
    id : number
    nombreProducto : string
    descripcion : string
    precioUnitario : number
    disponible: boolean
    imagen : ImagenResponse
}