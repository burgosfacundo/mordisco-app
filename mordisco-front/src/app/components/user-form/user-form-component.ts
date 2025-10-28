import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../auth/services/auth-service';
import User from '../../models/user/user-register';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-form',
  imports: [ReactiveFormsModule],
  templateUrl: './user-form-component.html',
  styleUrl: './user-form-component.css'
})
export class UserFormComponent implements OnInit{
  private router = inject(Router)
  private service : AuthService = inject(AuthService)
  private fb : FormBuilder = inject(FormBuilder)
  userForm! : FormGroup
  private _snackBar = inject(MatSnackBar);

  ngOnInit(): void {
    this.inicializarFormulario()
  }

  inicializarFormulario(){
     this.userForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(50),Validators.pattern(/^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]+$/)]],
      apellido: ['', [Validators.required, Validators.maxLength(50),Validators.pattern(/^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]+$/)]],
      telefono: ['', [Validators.required, Validators.maxLength(50),Validators.pattern(/^\+\d{1,3}(?:\s?\d){6,14}$/)]],
      email: ['', [Validators.required, Validators.email,Validators.maxLength(100)]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).+$/)]],
      rolId: ['', Validators.required]
    });
  }

  onSubmit(){
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }

    const raw = this.userForm.getRawValue();


    const user: User = {
      nombre: raw.nombre,
      apellido: raw.apellido,
      telefono: raw.telefono,
      email: raw.email,
      password: raw.password,
      rolId: raw.rolId
    };

    console.log(user)

    this.service.register(user).subscribe({
      next : () => {
        this._snackBar.open('✅ Usuario registrado correctamente', '',{duration: 3000})
        this.router.navigate(['/login']);
      },
      error:(e) => {
        console.error(e);
        
        this._snackBar.open('❌ Ocurrió un error. Intentelo en unos minutos', '',{duration: 3000})
      }
    })
  }
}
