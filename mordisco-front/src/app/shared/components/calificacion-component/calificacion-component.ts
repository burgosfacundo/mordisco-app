import { Component, input, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import CalificacionPedidoResponseDTO from '../../models/calificacion/calificacion-pedido-response-dto';
import CalificacionRepartidorResponseDTO from '../../models/calificacion/calificacion-repartidor-response-dto';
import { ICalificacionBase } from '../../models/calificacion/ICalificacionBase';

@Component({
  selector: 'app-calificacion-component',
  standalone: true,
  imports: [CommonModule],
  templateUrl : './calificacion-component.html'
})
export class CalificacionComponent{
  calificacion = input<ICalificacionBase>();

  calificacionPromedio1Dec(): number {
    const prom = this.calificacion()?.puntajePromedio
    return prom ? Number(prom.toFixed(1)) : 0;
  }

} 