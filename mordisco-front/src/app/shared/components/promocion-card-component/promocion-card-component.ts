import { Component, output, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import PromocionResponse from '../../models/promocion/promocion-response';

@Component({
  selector: 'app-promocion-card-component',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './promocion-card-component.html'
})
export class PromocionCardComponent {
  promocion = input<PromocionResponse>();
  showActions = input<boolean>(false);
  
  eliminar = output<number>();
  editar = output<number>();

  private normalizarFecha(fecha: Date | string): number {
    let d: Date;
    if (typeof fecha === 'string') {
      // Parsear manualmente para evitar problemas de timezone
      // El backend envía fechas en formato "YYYY-MM-DD"
      const [year, month, day] = fecha.split('-').map(Number);
      d = new Date(year, month - 1, day);  // month es 0-indexed en JS
    } else {
      d = new Date(fecha);
    }
    d.setHours(0, 0, 0, 0);
    return d.getTime();
  }

  getEstadoVigenciaClass(): string {
    const baseClasses = 'inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold uppercase tracking-wide';

    const hoyTime = this.normalizarFecha(new Date());
    const inicioTime = this.normalizarFecha(this.promocion()!.fechaInicio);
    const finTime = this.normalizarFecha(this.promocion()!.fechaFin);

    if (hoyTime < inicioTime) {
      return `${baseClasses} bg-blue-100 text-blue-700`;
    } else if (hoyTime > finTime) {
      return `${baseClasses} bg-gray-100 text-gray-700`;
    } else {
      return `${baseClasses} bg-green-100 text-green-700`;
    }
  }

  getEstadoVigenciaLabel(): string {
    const hoyTime = this.normalizarFecha(new Date());
    const inicioTime = this.normalizarFecha(this.promocion()!.fechaInicio);
    const finTime = this.normalizarFecha(this.promocion()!.fechaFin);

    if (hoyTime < inicioTime) {
      return 'Próximamente';
    } else if (hoyTime > finTime) {
      return 'Finalizada';
    } else {
      return 'Activa';
    }
  }

  onEliminar(): void {
    this.eliminar.emit(this.promocion()!.id);
  }
}