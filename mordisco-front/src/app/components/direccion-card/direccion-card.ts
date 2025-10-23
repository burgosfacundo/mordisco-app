import { Component, Input } from '@angular/core';
import Address from '../../models/address';

@Component({
  selector: 'app-direccion-card',
  imports: [],
  templateUrl: './direccion-card.html',
  styleUrl: './direccion-card.css'
})
export class DireccionCard {

  @Input() direccion? : Address

}
