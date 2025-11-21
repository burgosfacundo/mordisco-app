import { Component, input, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import CalificacionPedidoResponseDTO from '../../models/calificacion/calificacion-pedido-response-dto';

@Component({
  selector: 'app-calificacion-component',
  standalone: true,
  imports: [CommonModule],
  templateUrl : './calificacion-component.html'
})
export class CalificacionComponent {
  calificacion = input<CalificacionPedidoResponseDTO>();
}