import { Component, inject, Inject, OnInit } from '@angular/core';
import { DireccionCard } from "../../components/direccion-card/direccion-card";
import { AuthService } from '../../auth/services/auth-service';
import { AuthResponse } from '../../auth/models/auth-response';
import { DireccionService } from '../../services/direccion/direccion-service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import Direccion from '../../models/direccion/direccion';

@Component({
  selector: 'app-my-address',
  imports: [DireccionCard],
  templateUrl: './my-address.html',
  styleUrl: './my-address.css'
})
export class MyAddress implements OnInit{

  private aus : AuthService = inject (AuthService)
  private dService : DireccionService = inject(DireccionService)
  private _snackBar : MatSnackBar = inject(MatSnackBar)
  private router : Router = inject(Router)
  protected arrDirecciones? : Direccion[]
  idCurrUser : number | undefined

  ngOnInit(): void {
    const resp : AuthResponse | null = this.aus.getCurrentUser()
    this.idCurrUser = resp?.userId

    if(this.idCurrUser){
      this.listarDirecciones(this.idCurrUser)
    }else{
        this.openSnackBar('❌ Ocurrió un error al cargar las direcciones')
        this.router.navigate(['/'])
        return
    }
  }

  listarDirecciones(idCurrUser : number){
    this.dService.getAll(idCurrUser).subscribe({
      next: (data) => this.arrDirecciones = data,
      error:(e) => {console.log(e),
        this.openSnackBar('❌ Ocurrió un error al cargar las direcciones')
        this.router.navigate(['/'])
      }
    })
  }
  
  eliminarDireccion(id : number | undefined){
   if(!this.idCurrUser || !id){
        this.openSnackBar('❌ Ocurrió un error al cargar el perfil')
        this.router.navigate(['/'])
        return
    }

    this.dService.deleteDireccion(this.idCurrUser,id).subscribe({
      next :(data) => {console.log(data),
        this.openSnackBar('Direccion eliminada correctamente')
        this.listarDirecciones(this.idCurrUser!)
      },
      error: (e)=> {console.log(e),
        this.openSnackBar('❌ Ocurrió un error al eliminar direccion')}
    })
  }

  crearDireccion(){
    this.router.navigate(['/profile/my-address/form-address'])
  }

  modificarDireccion(direccionEditada : Direccion){
    this.dService.setDireccionToEdit(direccionEditada)
    this.router.navigate(['/profile/my-address/form-address'])
  }

  private openSnackBar(message: string, action: string = 'Cerrar'): void {
    this._snackBar.open(message, action, { duration: 3000 });
  }

}
