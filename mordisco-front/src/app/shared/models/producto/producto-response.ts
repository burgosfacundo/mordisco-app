import ImagenResponse from "../imagen/imagen-response"

export default interface ProductoResponse{
    id : number
    nombre : string
    descripcion : string
    precio : number
    disponible : boolean
    imagen : ImagenResponse
}