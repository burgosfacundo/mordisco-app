import { Component, input } from '@angular/core';
import HorarioAtencionResponse from '../../models/horario/horario-atencion-response';

@Component({
  selector: 'app-horario-card-component',
  imports: [],
  templateUrl: './horario-card-component.html'
})
export class HorarioCardComponent {

    horario = input<HorarioAtencionResponse>()

  private readonly DIAS: { [key: string]: string } = {
    'MONDAY': 'Lunes',
    'TUESDAY': 'Martes',
    'WEDNESDAY': 'Miércoles',
    'THURSDAY': 'Jueves',
    'FRIDAY': 'Viernes',
    'SATURDAY': 'Sábado',
    'SUNDAY': 'Domingo'
  };

  getDiaNombre() : string {
    return this.DIAS[this.horario()!.dia] || this.horario()!.dia;
  }

    formatHora(hora: string): string {
    // Formato: HH:mm:ss -> HH:mm
    const [h, m] = hora.split(':');
    return `${h.padStart(2, '0')}:${m.padStart(2, '0')}`;
  }

}
