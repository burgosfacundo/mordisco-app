import { Component, inject, OnInit } from '@angular/core';
import { HorarioCardComponent } from "../../../../shared/components/horario-card-component/horario-card-component";
import HorarioAtencion from '../../../../shared/models/restaurante/horario-atencion';
import { AuthService } from '../../../../shared/services/auth-service';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import RestauranteResponse from '../../../../shared/models/restaurante/restaurante-response';

@Component({
  selector: 'app-horario-page',
  imports: [HorarioCardComponent],
  templateUrl: './horario-page.html',
})
export class HorarioPage implements OnInit{

  private aus : AuthService = inject(AuthService)
  private rService : RestauranteService = inject(RestauranteService)
  private router : Router = inject(Router)
  private _snackBar : MatSnackBar = inject(MatSnackBar)
  arrHorarios? : HorarioAtencion[]
  idCurrUser? : number | undefined
  restauranteLeido? : RestauranteResponse 

  ngOnInit(): void {
    const resp = this.aus.currentUser()
    this.idCurrUser=resp?.userId

    if(this.idCurrUser){
      this.listarHorarios(this.idCurrUser)
    }else{
      
      this.openSnackBar('❌ Ocurrió un error al cargar las direcciones')
      this.router.navigate(['/'])
      return
    }
  }
  
  listarHorarios(id : number){
    this.rService.getByUsuario(id).subscribe({
    next: (r)=> {console.log(r.id),
      this.arrHorarios=r.horariosDeAtencion,
      this.restauranteLeido=r
    },
    error:(e)=> {console.log("No encontre el usuario")}
    })
  };


  crearHorario(){
    this.router.navigate(['/horarios/form-horarios'])
  }

  modificarHorario(horarioEditado : HorarioAtencion){
   // this.hService.setHorarioToEdit(horarioEditado)
    this.router.navigate(['/horarios/form-horarios'])
  }

/*
  eliminarHorario(id : number | undefined){
   if(!this.idCurrUser || !id){
        this.openSnackBar('❌ Ocurrió un error al cargar el perfil')
        this.router.navigate(['/'])
        return
    }

    this.hService.deleteHorario(this.idCurrUser,id).subscribe({
      next :(data) => {console.log(data),
        this.openSnackBar('Horario eliminado correctamente')
        this.listarHorarios(this.idCurrUser!)
      },
      error: (e)=> {console.log(e),
        this.openSnackBar('❌ Ocurrió un error al eliminar direccion')}
    })
  }*/

  private openSnackBar(message: string, action: string = 'Cerrar'): void {
    this._snackBar.open(message, action, { duration: 3000 });
  } 
}
