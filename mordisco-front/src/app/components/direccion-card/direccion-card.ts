import { Component, Input } from '@angular/core';
import Direccion from '../../models/direccion/direccion';

@Component({
  selector: 'app-direccion-card',
  imports: [],
  templateUrl: './direccion-card.html',
  styleUrl: './direccion-card.css'
})
export class DireccionCard {

  @Input() direccion? : Direccion

}
