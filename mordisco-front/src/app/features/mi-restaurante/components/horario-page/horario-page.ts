import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
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
  imports: [HorarioCardComponent, MatPaginator],
  templateUrl: './horario-page.html',
})
export class HorarioPage implements OnInit, OnDestroy {

  private aus : AuthService = inject(AuthService)
  private rService : RestauranteService = inject(RestauranteService)
  private router : Router = inject(Router)
  private toastService = inject(ToastService)
  private dialog = inject(MatDialog)
  private hService : HorarioService= inject(HorarioService)

  arrHorarios? : HorarioAtencionResponse[]
  arrHorariosPaginados? : HorarioAtencionResponse[]
  idCurrUser? : number | undefined
  restauranteLeido? : RestauranteResponse

  // Paginación
  sizeHorario: number = this.getPageSizeForScreen();
  pageHorario: number = 0;
  lengthHorario: number = 0;

  isLoading = true
  private eventListeners: (() => void)[] = [];


  ngOnInit(): void {
    this.setupEventListeners();
    const resp = this.aus.currentUser()
    this.idCurrUser=resp?.userId
    if(this.idCurrUser){
      this.encontrarRestaurante(this.idCurrUser)
    }
  }

  ngOnDestroy(): void {
    this.eventListeners.forEach(cleanup => cleanup());
  }

  private getPageSizeForScreen(): number {
    const width = window.innerWidth;
    if (width >= 1024) return 6;  // lg: 3 columnas x 2 filas
    if (width >= 640) return 4;   // sm: 2 columnas x 2 filas
    return 3;                      // mobile: 1 columna x 3 filas
  }

  private setupEventListeners(): void {
    const resizeListener = () => {
      const newSize = this.getPageSizeForScreen();
      if (newSize !== this.sizeHorario) {
        this.sizeHorario = newSize;
        this.pageHorario = 0;
        this.paginarHorarios();
      }
    };

    window.addEventListener('resize', resizeListener);
    this.eventListeners.push(() => window.removeEventListener('resize', resizeListener));
  }
  
  listarHorarios(id : number){
    this.hService.getAllByRestauranteId(id).subscribe({
      next:(data) =>{
        const ordenDias = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];
        this.arrHorarios = data.sort((a, b) => {
          return ordenDias.indexOf(a.dia) - ordenDias.indexOf(b.dia);
        });
        this.lengthHorario = this.arrHorarios.length;
        this.paginarHorarios();
        this.isLoading = false;
      },error:()=> {
        this.router.navigate(['/'])
      }
    })
  }

  private paginarHorarios(): void {
    if (!this.arrHorarios) return;

    const start = this.pageHorario * this.sizeHorario;
    const end = start + this.sizeHorario;
    this.arrHorariosPaginados = this.arrHorarios.slice(start, end);
  }

  onPageChange(event: PageEvent): void {
    this.pageHorario = event.pageIndex;
    this.sizeHorario = event.pageSize;
    this.paginarHorarios();
  }

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
