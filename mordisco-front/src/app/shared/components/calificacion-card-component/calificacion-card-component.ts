import { Component, inject, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import CalificacionRequestDTO from '../../models/calificacion/calificacion-request-dto';

@Component({
  selector: 'app-calificacion-card-component',
  imports: [CommonModule],
  templateUrl: './calificacion-card-component.html',
  styleUrl: './calificacion-card-component.css'
})
export class CalificacionCardComponent {
  @Input() calificacionRequest? : CalificacionRequestDTO

getInitials(): string {
  const usuario = this.calificacionRequest?.calificacionDTO?.usuario || '';
  const palabras = usuario.trim().split(' ');
  
  if (palabras.length >= 2) {
    return (palabras[0][0] + palabras[1][0]).toUpperCase();
  }
  return palabras[0]?.substring(0, 2).toUpperCase() || '?';
}

}
