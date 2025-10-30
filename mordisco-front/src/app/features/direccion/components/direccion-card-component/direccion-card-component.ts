import { Component, Input } from '@angular/core';
import Direccion from '../../models/direccion';

@Component({
  selector: 'app-direccion-card-component',
  imports: [],
  templateUrl: './direccion-card-compontent.html',
  styleUrl: './direccion-card-component.css'
})
export class DireccionCardComponent {

  @Input() direccion? : Direccion

}
