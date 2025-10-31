import { Component, inject } from '@angular/core';
import { DireccionFormComponent } from "../direccion-form-component/direccion-form-component";
import { DireccionService } from '../../services/direccion-service';

@Component({
  selector: 'app-form-address-page',
  imports: [DireccionFormComponent],
  templateUrl: './form-address-page.html',
})
export class FormAddressPage {

  dService : DireccionService = inject(DireccionService)
  modoEdicion = false;

    ngOnInit() {
    // Se suscribe una vez al BehaviorSubject
    this.dService.currentDir.subscribe(dir => {
      this.modoEdicion = !!dir;
    });
    }
}
