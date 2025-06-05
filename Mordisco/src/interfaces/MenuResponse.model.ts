import { ProductoResponse } from "./ProductoResponse.model";

export interface MenuResponse{
    id:number,
    nombre: string,
    productos: ProductoResponse[];
}
