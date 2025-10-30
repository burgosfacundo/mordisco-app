import { Component } from '@angular/core';
import { DireccionFormComponent } from "../direccion-form-component/direccion-form-component";

@Component({
  selector: 'app-form-address-page',
  imports: [DireccionFormComponent],
  templateUrl: './form-address-page.html',
  styleUrl: './form-address-page.css'
})
export class FormAddressPage {
  modoEdicion = false;

}
