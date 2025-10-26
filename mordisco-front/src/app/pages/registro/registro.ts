import { Component } from '@angular/core';
import { UserFormComponent } from "../../components/user-form/user-form-component";
import { Footer } from "../../components/footer/footer";

@Component({
  selector: 'app-registro',
  imports: [UserFormComponent, Footer],
  templateUrl: './registro.html',
  styleUrl: './registro.css'
})
export class Registro {

}
