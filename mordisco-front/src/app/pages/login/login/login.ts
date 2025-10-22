import { Component } from '@angular/core';
import { LoginFormComponent } from "../../../components/login-form/login-form-component";
import { Footer } from "../../../components/footer/footer";

@Component({
  selector: 'app-login',
  imports: [LoginFormComponent, Footer],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

}
