import { Component, inject, OnInit, signal } from '@angular/core';
import { AuthService } from '../../../../shared/services/auth-service';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ToastService } from '../../../../core/services/toast-service';

@Component({
  selector: 'app-edit-password-component',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './edit-password-component.html',
})
export class EditPasswordComponent implements OnInit {
  private fb : FormBuilder = inject(FormBuilder)
  private toastService = inject(ToastService)
  private router : Router = inject(Router)
  private authService : AuthService = inject(AuthService)
  private validationService : FormValidationService = inject(FormValidationService)

  isSubmitting = signal(false)
  editarPassword!: FormGroup;


  ngOnInit(): void {
    this.editarPassword = this.fb.group({
      passwordActual: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(8),Validators.maxLength(50), Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).+$/)]],
      confirmarPasswordNueva: ['', [Validators.required,Validators.minLength(8),Validators.maxLength(50), Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).+$/)]]
    });
  }

  manejarModificacionPassword(): void {
    if (this.editarPassword.invalid) return;

    this.isSubmitting.set(true)
    const { passwordActual, password , confirmarPasswordNueva } = this.editarPassword.value;

    // Validación del lado del cliente
    if (password !== confirmarPasswordNueva) {
      this.isSubmitting.set(false)
      this.toastService.error('❌ Las contraseñas nuevas no coinciden');
      return;
    }

    this.authService.updatePassword({currentPassword : passwordActual, newPassword : password}).subscribe({
      next: () => {
        this.toastService.success('✅ Contraseña actualizada correctamente');
        this.router.navigate(['/profile']);
      },
      error: () => {
        this.isSubmitting.set(false)
      }
    });
  
  }

  getError(fieldName: string): string | null {
    return this.validationService.getErrorMessage(this.editarPassword.get(fieldName),fieldName);
  }
}
