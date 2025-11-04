import { Component, inject, OnInit } from '@angular/core';
import { HorarioCardComponent } from "../../../../shared/components/horario-card-component/horario-card-component";
import { AuthService } from '../../../../shared/services/auth-service';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import RestauranteResponse from '../../../../shared/models/restaurante/restaurante-response';
import { HorarioService } from '../../../../shared/services/horario/horario-service';
import { PageEvent, MatPaginator } from '@angular/material/paginator';
import HorarioAtencionResponse from '../../../../shared/models/horario/horario-atencion-response';

@Component({
  selector: 'app-horario-page',
  imports: [HorarioCardComponent, MatPaginator],
  templateUrl: './horario-page.html',
})
export class HorarioPage implements OnInit{

  private aus : AuthService = inject(AuthService)
  private rService : RestauranteService = inject(RestauranteService)
  private router : Router = inject(Router)
  private _snackBar : MatSnackBar = inject(MatSnackBar)
  private hService : HorarioService= inject(HorarioService)

  arrHorarios? : HorarioAtencionResponse[]
  idCurrUser? : number | undefined
  restauranteLeido? : RestauranteResponse 
  
  sizeHorarios : number = 5;
  pageHorarios: number = 0;
  lengthHorarios : number = 5;

  isLoading = true


  ngOnInit(): void {
    const resp = this.aus.currentUser()
    this.idCurrUser=resp?.userId
    if(this.idCurrUser){
      this.encontrarRestaurante(this.idCurrUser)
    }

  }
  
  listarHorarios(id : number){
    this.hService.getAllByRestauranteId(id,this.pageHorarios,this.sizeHorarios).subscribe({
      next:(data) =>{ this.arrHorarios=data.content,
        this.lengthHorarios = data.totalElements;
        this.isLoading = false;
      },error:(e)=> {
        this._snackBar.open('❌ Error al cargar los horarios','Cerrar' , { duration: 3000 });
      }
    })
  };

  encontrarRestaurante(id : number){
    if(this.idCurrUser){
    this.rService.getByUsuario(id).subscribe({
      next: (r)=> {console.log(r.id),
        this.restauranteLeido=r
        this.listarHorarios(this.restauranteLeido.id)
      },
      error:()=> {console.log("No encontre el usuario"),
        this.aus.logout()
      }
      })
    }
  }

  onPageChangeHorarios(event: PageEvent): void {
    this.pageHorarios = event.pageIndex
    this.sizeHorarios = event.pageSize;
    if (this.idCurrUser){
      this.encontrarRestaurante(this.idCurrUser);
    }
  }

  crearHorario(){
    this.router.navigate(['/horarios/form-horarios'])
  }

  modificarHorario(horarioEditado : HorarioAtencionResponse){
    this.hService.setHorarioToEdit(horarioEditado)
    this.router.navigate(['/horarios/form-horarios'])
  }

  confirmarEliminacion(id: number | undefined): void {
    if (!id) return;
       const confirmar = confirm('¿Estás seguro de eliminar este horario?');
    if (confirmar) {
      this.eliminarHorario(id);
    }
  }

  eliminarHorario(id : number | undefined){
   if(!this.idCurrUser || !id){
        this.openSnackBar('❌ Ocurrió un error al cargar el perfil')
        this.router.navigate(['/'])
        return
    }

    this.hService.delete(id).subscribe({
      next :(data) => {console.log(data),
        this.openSnackBar('Horario eliminado correctamente')
        if(this.restauranteLeido?.id){
          this.listarHorarios(this.restauranteLeido?.id)
        }
      },
      error: (e)=> {console.log(e),
        this.openSnackBar('❌ Ocurrió un error al eliminar direccion')}
    })
  }

  private openSnackBar(message: string, action: string = 'Cerrar'): void {
    this._snackBar.open(message, action, { duration: 3000 });
  } 
}
