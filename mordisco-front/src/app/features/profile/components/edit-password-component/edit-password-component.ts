import { Component, inject, OnInit, signal } from '@angular/core';
import { AuthService } from '../../../../shared/services/auth-service';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-edit-password-component',
  imports: [ReactiveFormsModule, RouterLink],
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
      password: ['', [Validators.required, Validators.minLength(8)],Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).+$/)],
      confirmarPasswordNueva: ['', Validators.required]
    });
  }

  manejarModificacionPassword(): void {
    if (this.editarPassword.invalid) return;

    this.isSubmitting.set(true)
    const { passwordActual, password , confirmarPasswordNueva } = this.editarPassword.value;

    if (password !== confirmarPasswordNueva) {
      this.isSubmitting.set(false)
      this.snackBar.open('❌ Las contraseñas nuevas no coinciden', 'Cerrar', { duration: 3000 });
      return;
    }


    this.authService.updatePassword({currentPassword : passwordActual, newPassword : password}).subscribe({
      next: () => {
        this.snackBar.open('Contraseña actualizada correctamente', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/profile']);
      },
      error: (err) => {
        if(err.error.message.includes('Contraseña actual incorrecta')) {
          this.isSubmitting.set(false)
          this.snackBar.open('❌ La contraseña actual no es correcta', 'Cerrar', { duration: 3000 });
        }else if (err.error.newPassword.includes('debe tener')) {
          this.isSubmitting.set(false)
          this.snackBar.open('❌ Formato de la contraseña nueva incorrecto', 'Cerrar', { duration: 3000 });
        }else{
          console.log(err);
          
           this.isSubmitting.set(false)
          this.snackBar.open('❌ Error al actualizar la contraseña', 'Cerrar', { duration: 3000 });
        }
      }
    });
  
  }

  getError(fieldName: string): string | null {
    return this.validationService.getErrorMessage(this.editarPassword.get(fieldName),fieldName);
  }
}
