import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ICalificacionBase } from '../../models/calificacion/calificacion-base';

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