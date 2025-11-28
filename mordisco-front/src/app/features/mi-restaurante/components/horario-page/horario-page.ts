import { Component, inject, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { HorarioCardComponent } from "../../../../shared/components/horario-card-component/horario-card-component";
import { AuthService } from '../../../../shared/services/auth-service';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import { Router } from '@angular/router';
import RestauranteResponse from '../../../../shared/models/restaurante/restaurante-response';
import { HorarioService } from '../../../../shared/services/horario/horario-service';
import HorarioAtencionResponse from '../../../../shared/models/horario/horario-atencion-response';
import { ToastService } from '../../../../core/services/toast-service';
import { ConfirmDialogComponent } from '../../../../shared/store/confirm-dialog-component';

@Component({
  selector: 'app-horario-page',
  imports: [HorarioCardComponent],
  templateUrl: './horario-page.html',
})
export class HorarioPage implements OnInit{

  private aus : AuthService = inject(AuthService)
  private rService : RestauranteService = inject(RestauranteService)
  private router : Router = inject(Router)
  private toastService = inject(ToastService)
  private dialog = inject(MatDialog)
  private hService : HorarioService= inject(HorarioService)

  arrHorarios? : HorarioAtencionResponse[]
  idCurrUser? : number | undefined
  restauranteLeido? : RestauranteResponse 

  isLoading = true


  ngOnInit(): void {
    const resp = this.aus.currentUser()
    this.idCurrUser=resp?.userId
    if(this.idCurrUser){
      this.encontrarRestaurante(this.idCurrUser)
    }

  }
  
  listarHorarios(id : number){

    this.hService.getAllByRestauranteId(id).subscribe({
      next:(data) =>{ 
        this.arrHorarios=data,
        this.isLoading = false;
      },error:()=> {
        this.router.navigate(['/'])
      }
    })
  };

  encontrarRestaurante(id : number){
    if(this.idCurrUser){
    this.rService.getByUsuario(id).subscribe({
      next: (r)=> {
        this.restauranteLeido=r
        this.listarHorarios(this.restauranteLeido.id)
      },
      error:()=> {
        this.aus.logout()
      }
      })
    }
  }

  crearHorario(){
    this.router.navigate(['/restaurante/horarios/nuevo'])
  }

  modificarHorario(horarioEditado : HorarioAtencionResponse){
    this.hService.setHorarioToEdit(horarioEditado)
    this.router.navigate(['/restaurante/horarios/editar', horarioEditado.id])
  }

  confirmarEliminacion(id: number | undefined): void {
    if (!id) return;
    
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: { mensaje: '¿Estás seguro de eliminar este horario?' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.eliminarHorario(id);
      }
    });
  }

  eliminarHorario(id : number | undefined){
   if(!this.idCurrUser || !id){
        this.router.navigate(['/'])
        return
    }

    this.hService.delete(id).subscribe({
      next :() => {
        this.toastService.success('Horario eliminado correctamente')
        if(this.restauranteLeido?.id){
          this.listarHorarios(this.restauranteLeido?.id)
        }
      },
      error:()=> {
        this.router.navigate(['/'])
      }
    })
  }
}
