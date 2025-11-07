import { Component, inject, signal } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserService } from '../../../registro/services/user-service';
import UserProfileEdit from '../../../../shared/models/user/user-profile-edit';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-edit-profile-form-component',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './edit-profile-form-component.html',
})
export class EditProfileFormComponent {
  private fb = inject(FormBuilder);
  private validationService : FormValidationService = inject(FormValidationService)
  private _snackBar = inject(MatSnackBar);
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
      nombre: ['', [Validators.required, Validators.maxLength(50), Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/)]],
      apellido: ['', [Validators.required, Validators.maxLength(50), Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/)]],
      telefono: ['', [
        Validators.required,
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
        this._snackBar.open('❌ Ocurrió un error al cargar los datos del perfil','',{duration: 3000})
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
    const raw = this.editarPerfil.getRawValue();

    const userActualizado : UserProfileEdit =  {
      nombre: raw.nombre,
      apellido: raw.apellido,
      telefono: raw.telefono
    };

    this.userService.updateMe(userActualizado).subscribe({
      next: () => {
        this._snackBar.open('✅ Perfil actualizado correctamente','',{duration: 3000});
        this.router.navigate(['/profile'])
      },
      error: (e) => {
        console.error(e);
        this._snackBar.open('❌ Ocurrió un error al actualizar el perfil','',{duration: 3000});
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
