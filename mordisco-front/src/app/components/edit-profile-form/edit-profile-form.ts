import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { UserService } from '../../services/user/user-service';
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
      nombre: ['', [Validators.required, Validators.maxLength(50)]],
      apellido: ['', [Validators.required, Validators.maxLength(50)]],
      telefono: ['', [
        Validators.required,
        Validators.maxLength(15),
        Validators.pattern(/^[0-9]{8,15}$/)
      ]]
    });
  }

  private cargarDatosUsuario(): void {
    this.userService.getMe().subscribe({
      next: u => this.user = u ,
      error: () => {
        this.openSnackBar('❌ Ocurrió un error al cargar los datos del perfil')
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

  private openSnackBar(message: string, action: string = 'Cerrar'): void {
    this._snackBar.open(message, action, { duration: 3000 });
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
        this.openSnackBar('✅ Perfil actualizado correctamente');
      },
      error: () => {
        console.error();
        this.openSnackBar('❌ Ocurrió un error al actualizar el perfil');
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
        this.openSnackBar('🗑️ Cuenta eliminada correctamente');
        localStorage.removeItem('user');
        this.router.navigate(['/home']);
      },
      error: () => {
        console.error();
        this.openSnackBar('❌ No se pudo eliminar la cuenta');
      }
    });
}

verDirecciones(){
  this.router.navigate(['/direcciones']);
}
}
