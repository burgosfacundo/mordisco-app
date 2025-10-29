import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { UserService } from '../../features/registro/services/user-service';
import UserProfileEdit from '../../models/user/user-profile-edit';

@Component({
  selector: 'app-edit-profile-form',
  imports: [ReactiveFormsModule],
  templateUrl: './edit-profile-form.html',
  styleUrl: './edit-profile-form.css'
})
export class EditProfileForm {
private fb = inject(FormBuilder);
  private _snackBar = inject(MatSnackBar);
  private router = inject(Router);
  private userService = inject(UserService)
  private user? : UserProfileEdit

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
      next: u => this.user = u ,
      error: () => {
        this._snackBar.open('❌ Ocurrió un error al cargar los datos del perfil','',{duration: 3000})
        this.router.navigate(['/'])
      }
    })
    if (this.user) {
      this.editarPerfil.patchValue({
        nombre: this.user.nombre || '',
        apellido: this.user.apellido || '',
        telefono: this.user.telefono || ''
      });
    }
  }

  manejarModificacion(): void {
    if (this.editarPerfil.invalid) {
      this.editarPerfil.markAllAsTouched();
      return;
    }

    const raw = this.editarPerfil.getRawValue();

    const userActualizado : UserProfileEdit =  {
      nombre: raw.nombre,
      apellido: raw.apellido,
      telefono: raw.telefono
    };

    this.userService.updateMe(userActualizado).subscribe({
      next: () => {
        this._snackBar.open('✅ Perfil actualizado correctamente','',{duration: 3000});
      },
      error: () => {
        console.error();
        this._snackBar.open('❌ Ocurrió un error al actualizar el perfil','',{duration: 3000});
      }
    });
  }

 confirmarEliminacion(): void {
    const confirmado = confirm(
      '⚠️ ¿Estás segura/o de que querés eliminar tu cuenta? Esta acción no se puede deshacer.'
    );

    if (confirmado) {
      this.eliminarCuenta();
    }
  }
  
  cambiarContrasenia() {
    this.router.navigate(['/edit-password']);
  }

  eliminarCuenta(): void {
    this.userService.deleteMe().subscribe({
      next: () => {
        this._snackBar.open('🗑️ Cuenta eliminada correctamente','',{duration: 3000});
        localStorage.removeItem('user');
        this.router.navigate(['/home']);
      },
      error: () => {
        console.error();
        this._snackBar.open('❌ No se pudo eliminar la cuenta','',{duration: 3000});
      }
    });
}

verDirecciones(){
  this.router.navigate(['/profile/my-address']);
}
}
