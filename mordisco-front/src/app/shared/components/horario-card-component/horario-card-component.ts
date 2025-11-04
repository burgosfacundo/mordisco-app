import { Component, Input } from '@angular/core';
import HorarioAtencionResponse from '../../models/horario/horario-atencion-response';

@Component({
  selector: 'app-horario-card-component',
  imports: [],
  templateUrl: './horario-card-component.html',
  styleUrl: './horario-card-component.css'
})
export class HorarioCardComponent {

    @Input() horario? : HorarioAtencionResponse

}
