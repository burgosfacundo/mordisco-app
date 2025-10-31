import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserService } from '../../../registro/services/user-service';
import UserProfile from '../../../../shared/models/user/user-profile';

@Component({
  selector: 'app-view-profile',
  imports: [],
  templateUrl: './view-profile-component.html',
})
export class ViewProfileComponent {
  private userService : UserService = inject(UserService)
  private router : Router = inject(Router)
  private _snackBar : MatSnackBar = inject(MatSnackBar)
  usuario? : UserProfile


  ngOnInit(): void {
    this.userService.getMe().subscribe({
      next: u => this.usuario = u,
      error: () => {
        this.openSnackBar('❌ Ocurrió un error al cargar el perfil')
        this.router.navigate(['/'])
      }
    })
  }

 confirmarEliminacion(): void {
    const confirmado = confirm(
      '⚠️ ¿Estás segura/o de que querés eliminar tu cuenta? Esta acción no se puede deshacer.'
    );

    if (confirmado) {
      this.eliminarCuenta();
    }
  }
  
  eliminarCuenta(): void {
    this.userService.deleteMe().subscribe({
      next: () => {
        this._snackBar.open('Cuenta eliminada correctamente','',{duration: 3000});
        localStorage.removeItem('user');
        this.router.navigate(['/home']);
      },
      error: () => {
        console.error();
        this._snackBar.open('❌ No se pudo eliminar la cuenta','',{duration: 3000});
      }
    });
}

  editarPerfil(){
    this.router.navigate(['/profile/edit'])
  }

  private openSnackBar(message: string, action: string = 'Cerrar'): void {
    this._snackBar.open(message, action, { duration: 3000 });
  }


}
