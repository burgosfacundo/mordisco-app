import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import User from '../../models/user';
import ProfileUser from '../../models/profileUser';

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
      ]],
      email: ['', [
        Validators.required,
        Validators.email,
        Validators.maxLength(100)
      ]]
    });
  }

  private cargarDatosUsuario(): void {
    const userData = localStorage.getItem('user');
    if (userData) {
      const user: User = JSON.parse(userData);

      // ✅ Rellenamos todos los campos del formulario
      this.editarPerfil.patchValue({
        nombre: user.nombre || '',
        apellido: user.apellido || '',
        telefono: user.telefono || '',
        email: user.email || '',
        password: '' // nunca traemos la real, se deja vacío
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

    const userActualizado : ProfileUser =  {
      nombre: raw.nombre,
      apellido: raw.apellido,
      telefono: raw.telefono,
      email: raw.email
    };

 /*   this.authService.updateProfile(userActualizado).subscribe({
      next: () => {
        this.openSnackBar('✅ Perfil actualizado correctamente');
      },
      error: () => {
        console.error();
        this.openSnackBar('❌ Ocurrió un error al actualizar el perfil');
      }
    });*/
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

  private eliminarCuenta(): void {
  /*  this.authService.deleteAccount().subscribe({
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
  */
}
verDirecciones(){
  this.router.navigate(['/direcciones']);
}
}
