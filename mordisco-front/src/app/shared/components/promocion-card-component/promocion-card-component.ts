import { Component, Input, Output, EventEmitter } from '@angular/core';
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
  @Input() promocion!: PromocionResponse;
  @Input() showActions: boolean = false;
  
  @Output() eliminar = new EventEmitter<number>();
  @Output() editar = new EventEmitter<number>();

  getEstadoVigenciaClass(): string {
    const baseClasses = 'inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold uppercase tracking-wide';
    
    const hoy = new Date();
    const inicio = new Date(this.promocion.fechaInicio);
    const fin = new Date(this.promocion.fechaFin);
    
    if (hoy < inicio) {
      return `${baseClasses} bg-blue-100 text-blue-700`;
    } else if (hoy > fin) {
      return `${baseClasses} bg-gray-100 text-gray-700`;
    } else {
      return `${baseClasses} bg-green-100 text-green-700`;
    }
  }

  getEstadoVigenciaLabel(): string {
    const hoy = new Date();
    const inicio = new Date(this.promocion.fechaInicio);
    const fin = new Date(this.promocion.fechaFin);
    
    if (hoy < inicio) {
      return 'Próximamente';
    } else if (hoy > fin) {
      return 'Finalizada';
    } else {
      return 'Activa';
    }
  }

  onEliminar(): void {
    this.eliminar.emit(this.promocion.id);
  }
}