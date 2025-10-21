import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../auth/services/auth-service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-login-form',
  imports: [ReactiveFormsModule],
  templateUrl: './login-form-component.html',
  styleUrl: './login-form-component.css'
})
export class LoginFormComponent implements OnInit{
  private fb : FormBuilder = inject(FormBuilder)
  protected email : FormControl
  protected password : FormControl
  private service : AuthService = inject(AuthService)
  private router : Router = inject(Router)
  inicioSesion! : FormGroup 

  private _snackBar = inject(MatSnackBar);

  openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action);
  }

  errorMessage = signal('');

  constructor(){
    this.email = new FormControl('',[Validators.required,Validators.email])
    this.password = new FormControl('',Validators.required)
  }


  ngOnInit(): void {
    this.inicioSesion = this.fb.group({
      email : this.email,
      password : this.password
    })
  }

  ///metodo iniciar sesion y validador si esta bien, redirige a la pagina principal
  iniciarSesion(){
  // 3️⃣ Llamar al servicio para iniciar sesión
  this.service.login(this.inicioSesion.value).subscribe({
    next: () => {
      this.router.navigate(['']);
    },
    error: e => {
      console.error(e);
      
      this.openSnackBar('❌ Email o contraseña incorrectos','Continuar')
    }
  });
  }
}
