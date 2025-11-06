import ImagenResponse from "../imagen/imagen-response"

export default interface ProductoUpdate{
    nombre : string
    descripcion : string
    precio : number
    disponible : boolean
    imagen : ImagenResponse
}