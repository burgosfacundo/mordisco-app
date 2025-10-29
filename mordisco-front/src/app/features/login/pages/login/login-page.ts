import { Component } from '@angular/core';
import { LoginForm } from '../../components/login-form/login-form';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-login-page',
  imports: [LoginForm],
  templateUrl: './login-page.html',
  styleUrl: './login-page.css'
})
export class LoginPage {

}
