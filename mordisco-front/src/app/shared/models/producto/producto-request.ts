import ImagenRequest from "../imagen/imagen-request"

export default interface ProductoRequest{
    idMenu : number
    nombre : string
    descripcion : string
    precio : number
    disponible : boolean
    imagen : ImagenRequest
}