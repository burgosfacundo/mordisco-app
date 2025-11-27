import { Component, inject, signal } from '@angular/core';
import { UserService } from '../../../registro/services/user-service';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NotificationService } from '../../../../core/services/notification-service';

@Component({
  selector: 'app-edit-profile-form-component',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './edit-profile-form-component.html',
})
export class EditProfileFormComponent {
  private fb = inject(FormBuilder);
  private validationService : FormValidationService = inject(FormValidationService)
  private notificationService = inject(NotificationService);
  private router = inject(Router);
  private userService = inject(UserService)

  isSubmitting = signal(false)
  editarPerfil!: FormGroup;

   ngOnInit(): void {
    this.inicializarFormulario();
    this.cargarDatosUsuario();
  }

 private inicializarFormulario(): void {
    this.editarPerfil = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(2),Validators.maxLength(50), Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/)]],
      apellido: ['', [Validators.required, Validators.minLength(2),Validators.maxLength(50), Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/)]],
      telefono: ['', [
        Validators.required,
        Validators.minLength(10),
        Validators.maxLength(15),
        Validators.pattern(/^\+\d{1,3}(?:\s?\d){6,14}$/)
      ]]
    });
  }

  private cargarDatosUsuario(): void {
    this.userService.getMe().subscribe({
      next: u => {
        this.editarPerfil.patchValue({
          nombre: u.nombre,
          apellido: u.apellido,
          telefono: u.telefono
        });
      },
      error: () => {
        this.router.navigate(['/'])
      }
    });
  }

  manejarModificacion(): void {
    if (this.editarPerfil.invalid) {
      this.editarPerfil.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true)

    this.userService.updateMe(this.editarPerfil.value).subscribe({
      next: () => {
        this.notificationService.success('✅ Perfil actualizado correctamente')
        this.router.navigate(['/profile'])
      }
    });
  }
  
  cambiarContrasenia() {
    this.router.navigate(['/edit-password']);
  }

  verDirecciones(){
    this.router.navigate(['/profile/my-address']);
  }

  getError(fieldName: string): string | null {
      return this.validationService.getErrorMessage(
      this.editarPerfil.get(fieldName),
      fieldName);
  }
}
