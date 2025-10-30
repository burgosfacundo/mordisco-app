import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserService } from '../../../registro/services/user-service';
import UserProfile from '../../../../models/user/user-profile';
import Direccion from '../../../direccion/models/direccion';
import { DireccionCardComponent } from "../../../direccion/components/direccion-card-component/direccion-card-component";

@Component({
  selector: 'app-view-profile',
  imports: [DireccionCardComponent],
  templateUrl: './view-profile-component.html',
  styleUrl: './view-profile-component.css'
})
export class ViewProfileComponent {
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
