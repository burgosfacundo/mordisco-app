export interface PromocionResponse {
    id: number;
    descripcion: string;
    descuento: number; // 0.20 por ejemplo
    fechaInicio: string; // formato ISO: "2023-10-01"
    fechaFin: string;    // formato ISO: "2023-10-31"
}