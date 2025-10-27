import { Component } from '@angular/core';
import { DireccionForm } from "../../components/direccion-form/direccion-form";

@Component({
  selector: 'app-form-address',
  imports: [DireccionForm],
  templateUrl: './form-address.html',
  styleUrl: './form-address.css'
})
export class FormAddress {

}
