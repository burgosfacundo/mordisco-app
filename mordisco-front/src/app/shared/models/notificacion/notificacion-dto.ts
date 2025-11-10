import { TipoNotificacion } from "./tipo-notificacion";

export interface Notificacion {
  tipo: TipoNotificacion
  mensaje: string
  pedidoId: number
  estado?: string
  timestamp: Date
  leida: boolean
}