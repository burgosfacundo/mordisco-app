import { Component, inject } from '@angular/core';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import { HorarioFormComponent } from "../horario-form-component/horario-form-component";
import { HorarioService } from '../../../../shared/services/horario/horario-service';

@Component({
  selector: 'app-horario-form-page',
  imports: [HorarioFormComponent],
  templateUrl: './horario-form-page.html',
  styleUrl: './horario-form-page.css'
})
export class HorarioFormPage {
  hService : HorarioService = inject(HorarioService)
  modoEdicion = false;

  ngOnInit() {
    this.hService.currentHor.subscribe(hor => {//mover el behavior al service horario
        this.modoEdicion = !!hor;
    });
  }
}
