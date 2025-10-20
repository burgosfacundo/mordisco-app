import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login implements OnInit{
  inicioSesion! : FormGroup 

  constructor(private fb : FormBuilder, public uService : Service, private router : Router){}

  ngOnInit(): void {
    this.inicioSesion = this.fb.group({
      email : ['',[Validators.required,Validators.email]],
      contraseña : ['', Validators.required, Validators.pattern(/^(?=.*[A-Z])(?=.*\d)[A-Za-z\d]{8,}$/)]
    })
  }

  ///metodo iniciar sesion y validador si esta bien, redirige a la pagina principal
  iniciarSesion(){
  // 3️⃣ Llamar al servicio para iniciar sesión
  this.uService.login(this.inicioSesion.value).subscribe({
    next: (res: any) => {
      ///localStorage.setItem('usuario', JSON.stringify(res));

      // 4️⃣ Redirigir a la página principal
      this.router.navigate(['/principal']);
    },
    error: () => {
      // Manejo de error
      alert('Email o contraseña incorrectos');
    }
  });
  }


}
