import { Component, Input } from '@angular/core';
import Direccion from '../../../../shared/models/direccion/direccion-response';

@Component({
  selector: 'app-direccion-card-component',
  imports: [],
  templateUrl: './direccion-card-compontent.html',
})
export class DireccionCardComponent {

  @Input() direccion? : Direccion

}
