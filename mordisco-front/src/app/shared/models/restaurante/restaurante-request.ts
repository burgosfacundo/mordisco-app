import DireccionRequest from "../direccion/direccion-request"
import ImagenRequest from "../imagen/imagen-request"


export default interface RestauranteRequest {
    razonSocial: string
    activo: boolean
    logo : ImagenRequest
    idUsuario : number
    direccion : DireccionRequest

}