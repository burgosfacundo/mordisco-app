import { Component, inject, input, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import CalificacionPedidoResponseDTO from '../../models/calificacion/calificacion-pedido-response-dto';

@Component({
  selector: 'app-calificacion-card-component',
  imports: [CommonModule],
  templateUrl: './calificacion-card-component.html'
})
export class CalificacionCardComponent {
   calificacionResp = input<CalificacionPedidoResponseDTO>()

  getInitials(): string {
    
    const nombre = this.calificacionResp()?.clienteNombre || '';
    const primerNombre = nombre.trim().split(' ')[0] || '';
    let iniciales = '';

    if (primerNombre.length > 0) {
      iniciales += primerNombre[0];
    }

    return iniciales.toUpperCase() || '?';
  }
}
