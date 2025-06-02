import { ImagenResponse } from "./ImagenResponse .model";
import { PromocionResponse } from "./PromocionResponse .model";
import { HorarioAtencionResponse } from "./HorarioAtencionResponse .model";
import { CalificacionRestauranteResponse } from "./CalificacionRestauranteResponse .model";
import { DireccionResponse } from "./DireccionResponse .model";

export interface RestauranteResponse {
    id: number;
    razonSocial: string;
    activo: boolean;
    logo: ImagenResponse;
    menuId: number;
    promociones: PromocionResponse[];
    hoariosDeAtencion: HorarioAtencionResponse[];
    calificacionRestaurante: CalificacionRestauranteResponse[];
    direccion: DireccionResponse;
}