import { Component, input, Input } from '@angular/core';
import CalificacionRestauranteReponse from '../../models/calificacion/calificacion-restaurante-response';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-calificacion-component',
  standalone: true,
  imports: [CommonModule],
  templateUrl : './calificacion-component.html'
})
export class CalificacionComponent {
  calificacion = input<CalificacionRestauranteReponse>();
}