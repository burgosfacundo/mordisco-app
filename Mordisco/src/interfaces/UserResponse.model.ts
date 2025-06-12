import { DireccionResponse } from "./DireccionResponse .model";
import { RolDTO } from "./RolDTO.model";

export interface UserResponse {
    id: number;
    nombre: string;
    apellido:string;
    telefono: string;
    email: string;
    rol: RolDTO
    direcciones: DireccionResponse[]
}
