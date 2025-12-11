import ImagenResponse from "../imagen/imagen-response";

export default interface ProductoCard {
    id: number,
    nombre: string,
    disponible : boolean
    imagen: ImagenResponse,
    precio: number,
    precioConDescuento?: number,
    porcentajeDescuento?: number,
    tienePromocion?: boolean,
    descripcionPromocion?: string
}