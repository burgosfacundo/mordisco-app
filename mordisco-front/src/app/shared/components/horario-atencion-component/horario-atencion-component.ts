import { Component, input } from '@angular/core';
import HorarioAtencionResponse from '../../models/horario/horario-atencion-response';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-horario-restaurante-component',
  standalone: true,
  imports: [CommonModule],
  templateUrl : './horario-atencion-component.html'
})
export class HorarioRestauranteComponent {
 horario = input<HorarioAtencionResponse>();

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
    return this.DIAS[this.horario()!.dia] || this.horario()!.dia;
  }

  formatHora(hora: string): string {
    // Formato: HH:mm:ss -> HH:mm
    const [h, m] = hora.split(':');
    return `${h.padStart(2, '0')}:${m.padStart(2, '0')}`;
  }

  isAbierto(): boolean {
    const ahora = new Date();
    const diaActual = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'][ahora.getDay()];

    // Primero verifica si es el día correcto
    if (this.horario()!.dia !== diaActual) {
      return false;
    }

    // Luego verifica si está dentro del horario
    const open = this.toMinutes(this.horario()!.horaApertura);
    const close = this.toMinutes(this.horario()!.horaCierre);
    const now = this.nowInMinutes();

    if (open === close) return false;

    // Si cruza medianoche: abierto si ahora >= apertura O ahora < cierre
    if (this.horario()!.cruzaMedianoche) {
      return now >= open || now < close;
    }

    // Horario normal: abierto si ahora está entre apertura y cierre
    return now >= open && now < close;
  }

  private nowInMinutes(): number {
    const now = new Date();
    return now.getHours() * 60 + now.getMinutes();
  }

  private toMinutes(time: string): number {
    const [h, m, s] = time.split(':').map(Number);
    return (h || 0) * 60 + (m || 0) + Math.floor((s || 0) / 60);
  }

  getEstadoBadgeClass(): string {
    const baseClasses = 'inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold uppercase tracking-wide';
    
    if (this.isAbierto()) {
      return `${baseClasses} bg-green-100 text-green-700`;
    }
    return `${baseClasses} bg-gray-100 text-gray-700`;
  }
}