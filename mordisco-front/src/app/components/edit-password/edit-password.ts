import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

@Component({
  selector: 'app-edit-password',
  imports: [ReactiveFormsModule],
  templateUrl: './edit-password.html',
  styleUrls: ['./edit-password.css']
})
export class EditPasswordComponent implements OnInit {

  editarPassword!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.editarPassword = this.fb.group({
      passwordActual: ['', Validators.required],
      passwordNueva: ['', [Validators.required, Validators.minLength(8)]],
      confirmarPasswordNueva: ['', Validators.required]
    });
  }

  manejarModificacionPassword(): void {
    if (this.editarPassword.invalid) return;

    const { passwordActual, confirmarPasswordNueva, passwordNueva } = this.editarPassword.value;

    if (passwordActual !== confirmarPasswordNueva) {
      this.snackBar.open('Las contraseñas nuevas no coinciden', 'Cerrar', { duration: 3000 });
      return;
    }

    /*
    this.authService.actualizarPassword(passwordActual, passwordNueva).subscribe({
      next: () => {
        this.snackBar.open('Contraseña actualizada correctamente', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/perfil']);
      },
      error: (err) => {
        console.error(err);
        this.snackBar.open('Error al actualizar la contraseña', 'Cerrar', { duration: 3000 });
      }
    });
    */
  }
}
