import { Component, Input, Output, EventEmitter } from '@angular/core';
import HorarioAtencionResponse from '../../models/horario/horario-atencion-response';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-horario-restaurante-component',
  standalone: true,
  imports: [CommonModule],
  templateUrl : './horario-atencion-component.html'
})
export class HorarioRestauranteComponent {
  @Input() horario!: HorarioAtencionResponse;

  private readonly DIAS: { [key: string]: string } = {
    'MONDAY': 'Lunes',
    'TUESDAY': 'Martes',
    'WEDNESDAY': 'Miércoles',
    'THURSDAY': 'Jueves',
    'FRIDAY': 'Viernes',
    'SATURDAY': 'Sábado',
    'SUNDAY': 'Domingo'
  };

  getDiaNombre(): string {
    return this.DIAS[this.horario.dia] || this.horario.dia;
  }

  formatHora(hora: string): string {
    // Formato: HH:mm:ss -> HH:mm
    const [h, m] = hora.split(':');
    return `${h.padStart(2, '0')}:${m.padStart(2, '0')}`;
  }

  isAbierto(): boolean {
    const ahora = new Date();
    const diaActual = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'][ahora.getDay()];
    return this.horario.dia === diaActual;
  }

  getEstadoBadgeClass(): string {
    const baseClasses = 'inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold uppercase tracking-wide';
    
    if (this.isAbierto()) {
      return `${baseClasses} bg-green-100 text-green-700`;
    }
    return `${baseClasses} bg-gray-100 text-gray-700`;
  }
}