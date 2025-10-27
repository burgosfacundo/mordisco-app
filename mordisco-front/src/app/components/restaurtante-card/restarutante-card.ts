import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-restarutante-card',
  imports: [],
  templateUrl: './restarutante-card.html',
  styleUrl: './restarutante-card.css'
})
export class RestarutanteCard {
  @Input() restaurante!: RestarutanteCard;
imagen: any;
nombre: any;
calificacion: any;
horario: any;
}
