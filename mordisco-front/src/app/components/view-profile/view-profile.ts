import { Component, inject } from '@angular/core';
import { DireccionCardComponent } from "../direccion-card/direccion-card-component";
import { Router } from '@angular/router';
import UserProfile from '../../models/user/user-profile';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserService } from '../../services/user/user-service';
import Direccion from '../../models/direccion/direccion';

@Component({
  selector: 'app-view-profile',
  imports: [DireccionCardComponent],
  templateUrl: './view-profile.html',
  styleUrl: './view-profile.css'
})
export class ViewProfile {
  private userService : UserService = inject(UserService)
  private router : Router = inject(Router)
  private _snackBar : MatSnackBar = inject(MatSnackBar)
  usuario? : UserProfile
  direcciones? : Direccion[]


  ngOnInit(): void {
    this.userService.getMe().subscribe({
      next: u => this.usuario = u,
      error: () => {
        this.openSnackBar('❌ Ocurrió un error al cargar el perfil')
        this.router.navigate(['/'])
      }
    })
  }

  eliminarCuenta(){
    /*this.uService.eliminarUusuario*/
  }

  cambiarContrasenia(){
    this.router.navigate(['/edit-password'])
  }

  editarPerfil(){
    this.router.navigate(['/profile/edit'])
  }

  private openSnackBar(message: string, action: string = 'Cerrar'): void {
    this._snackBar.open(message, action, { duration: 3000 });
  }


}
