export interface HorarioAtencionResponse {
    id: number;
    dia: string; // Usá "DayOfWeek" como string ("MONDAY", "TUESDAY", etc.)
    horaApertura: string; // formato HH:mm:ss o HH:mm
    horaCierre: string;   // formato HH:mm:ss o HH:mm
}