import ProductoResponse from "../producto/producto-response"

export default interface Menu{
    id?: number
    nombre : string
    productos : ProductoResponse[]
}