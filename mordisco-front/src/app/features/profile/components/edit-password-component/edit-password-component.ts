import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { AuthService } from '../../../../shared/services/auth-service';
import { FormValidationService } from '../../../../shared/services/form-validation-service';

@Component({
  selector: 'app-edit-password-component',
  imports: [ReactiveFormsModule],
  templateUrl: './edit-password-component.html',
})
export class EditPasswordComponent implements OnInit {
  private fb : FormBuilder = inject(FormBuilder)
  private snackBar : MatSnackBar = inject(MatSnackBar)
  private router : Router = inject(Router)
  private authService : AuthService = inject(AuthService)
  private validationService : FormValidationService = inject(FormValidationService)

  isSubmitting = signal(false)
  editarPassword!: FormGroup;


  ngOnInit(): void {
    this.editarPassword = this.fb.group({
      passwordActual: ['', Validators.required],
      passwordNueva: ['', [Validators.required, Validators.minLength(8)],Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).+$/)],
      confirmarPasswordNueva: ['', Validators.required]
    });
  }

  manejarModificacionPassword(): void {
    if (this.editarPassword.invalid) return;

    this.isSubmitting.set(true)
    const { passwordActual, confirmarPasswordNueva, passwordNueva } = this.editarPassword.value;

    if (passwordActual !== confirmarPasswordNueva) {
      this.snackBar.open('Las contraseñas nuevas no coinciden', 'Cerrar', { duration: 3000 });
      return;
    }


    this.authService.updatePassword({passwordActual, passwordNueva}).subscribe({
      next: () => {
        this.snackBar.open('Contraseña actualizada correctamente', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/perfil']);
      },
      error: (err) => {
        console.error(err);
        this.snackBar.open('Error al actualizar la contraseña', 'Cerrar', { duration: 3000 });
      }
    });
  
  }

  getError(fieldName: string): string | null {
    return this.validationService.getErrorMessage(
    this.editarPassword.get(fieldName),
    fieldName);
}
}
