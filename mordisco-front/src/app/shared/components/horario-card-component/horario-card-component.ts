import { Component, Input } from '@angular/core';
import HorarioAtencion from '../../models/restaurante/horario-atencion';

@Component({
  selector: 'app-horario-card-component',
  imports: [],
  templateUrl: './horario-card-component.html',
  styleUrl: './horario-card-component.css'
})
export class HorarioCardComponent {

    @Input() horario? : HorarioAtencion

}
