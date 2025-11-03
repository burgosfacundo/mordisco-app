import ImagenUpdate from "../imagen/imagen-update"

export default interface RestauranteUpdate {
    razonSocial: string
    activo: boolean
    logo : ImagenUpdate
    idUsuario : number
}