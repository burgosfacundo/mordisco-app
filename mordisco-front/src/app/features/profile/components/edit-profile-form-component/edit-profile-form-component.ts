import { Component, inject, signal } from '@angular/core';
import { UserService } from '../../../registro/services/user-service';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ToastService } from '../../../../core/services/toast-service';
import {
  NOMBRE_PATTERN, NOMBRE_MIN_LENGTH, NOMBRE_MAX_LENGTH,
  TELEFONO_PATTERN, TELEFONO_MIN_LENGTH, TELEFONO_MAX_LENGTH
} from '../../../../shared/validators/validation-constants';

@Component({
  selector: 'app-edit-profile-form-component',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './edit-profile-form-component.html',
})
export class EditProfileFormComponent {
  private fb = inject(FormBuilder);
  private validationService : FormValidationService = inject(FormValidationService)
  private toastService = inject(ToastService);
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
      nombre: ['', [Validators.required, Validators.minLength(NOMBRE_MIN_LENGTH), Validators.maxLength(NOMBRE_MAX_LENGTH), Validators.pattern(NOMBRE_PATTERN)]],
      apellido: ['', [Validators.required, Validators.minLength(NOMBRE_MIN_LENGTH), Validators.maxLength(NOMBRE_MAX_LENGTH), Validators.pattern(NOMBRE_PATTERN)]],
      telefono: ['', [
        Validators.required,
        Validators.minLength(TELEFONO_MIN_LENGTH),
        Validators.maxLength(TELEFONO_MAX_LENGTH),
        Validators.pattern(TELEFONO_PATTERN)
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
        this.toastService.success('✅ Perfil actualizado correctamente')
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
