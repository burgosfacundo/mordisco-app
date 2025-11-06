import ImagenUpdate from "../imagen/imagen-update"

export default interface RestauranteUpdate {
    id : number
    razonSocial: string
    activo: boolean
    logo : ImagenUpdate
}