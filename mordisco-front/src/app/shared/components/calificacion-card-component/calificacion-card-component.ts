import { Component, Input } from '@angular/core';
import CalificacionDTO from '../../models/calificacion/calificacion-response';
import CalificacionRestauranteReponse from '../../models/calificacion/calificacion-restaurante-response';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-calificacion-card-component',
  imports: [CommonModule],
  templateUrl: './calificacion-card-component.html',
  styleUrl: './calificacion-card-component.css'
})
export class CalificacionCardComponent {
  @Input() calificacionResponse? : CalificacionRestauranteReponse

getInitials(): string {
  const usuario = this.calificacionResponse?.calificacion?.usuario || '';
  const palabras = usuario.trim().split(' ');
  
  if (palabras.length >= 2) {
    return (palabras[0][0] + palabras[1][0]).toUpperCase();
  }
  return palabras[0]?.substring(0, 2).toUpperCase() || '?';
}

}
