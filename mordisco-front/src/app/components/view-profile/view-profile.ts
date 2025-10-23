import { Component, inject } from '@angular/core';
import User from '../../models/user';
import { UserService } from '../../services/userService/user-service';
import { AuthService } from '../../auth/services/auth-service';
import { JwtUser } from '../../auth/models/jwt-user';
import { DireccionCard } from "../direccion-card/direccion-card";
import { Router } from '@angular/router';

@Component({
  selector: 'app-view-profile',
  imports: [DireccionCard],
  templateUrl: './view-profile.html',
  styleUrl: './view-profile.css'
})
export class ViewProfile {

  usuario? : User
  jwt? : JwtUser | null
  IDUser? : number | null
  private uService : UserService = inject(UserService);
  private aus : AuthService = inject(AuthService)
  private router :Router = inject(Router)

  ngOnInit(): void {
   /* this.jwt = this.aus.getCurrentUser() PARA LA API*/
   /* this.IDUser = this.jwt?.id PARA LA API */
   this.IDUser=1 /*JSON SERVER */
    if(this.IDUser){
      this.uService.getUserByID(this.IDUser).subscribe({
      next:(data) => this.usuario=data,
      error: (e)=> console.log(e)
    })
    }
    
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


}
