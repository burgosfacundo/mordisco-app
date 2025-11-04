import { Component, inject } from '@angular/core';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import { HorarioFormComponent } from "../horario-form-component/horario-form-component";

@Component({
  selector: 'app-horario-form-page',
  imports: [HorarioFormComponent],
  templateUrl: './horario-form-page.html',
  styleUrl: './horario-form-page.css'
})
export class HorarioFormPage {
  rService : RestauranteService = inject(RestauranteService)
  modoEdicion = false;
/*
    ngOnInit() {
    // Se suscribe una vez al BehaviorSubject
    this.hService.currentHor.subscribe(hor => {//mover el behavior al service horario
      this.modoEdicion = !!hor;
    });
    }*/
}
