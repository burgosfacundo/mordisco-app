import { Component, Input } from '@angular/core';
import DireccionResponse from '../../../../shared/models/direccion/direccion-response';

@Component({
  selector: 'app-direccion-card-component',
  imports: [],
  templateUrl: './direccion-card-compontent.html',
})
export class DireccionCardComponent {

  @Input() direccion? : DireccionResponse

}
