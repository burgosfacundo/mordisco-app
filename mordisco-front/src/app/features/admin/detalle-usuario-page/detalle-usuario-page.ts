import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../../registro/services/user-service';
import UserProfile from '../../../shared/models/user/user-profile';
import { MisPedidosClientePage } from '../../mis-pedidos/components/mis-pedidos-cliente-page/mis-pedidos-cliente-page';
import { MatIcon } from "@angular/material/icon";
import { EntregasPage } from '../../entregas/entregas-page/entregas-page';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import CalificacionPedidoResponseDTO from '../../../shared/models/calificacion/calificacion-pedido-response-dto';
import { CalificacionService } from '../../../shared/services/calificacion/calificacion-service';
import CalificacionRepartidorResponseDTO from '../../../shared/models/calificacion/calificacion-repartidor-response-dto';
import { CalificacionCardAdmin } from "../../../shared/components/calificacion-card-admin/calificacion-card-admin";
import RestauranteResponse from '../../../shared/models/restaurante/restaurante-response';
import { RestauranteService } from '../../../shared/services/restaurante/restaurante-service';
import { PromptService } from '../../../core/services/confirmation-prompt-service';
import BajaLogisticaDTO from '../../../shared/models/BajaLogisticaRequestDTO';
import { ConfirmationService } from '../../../core/services/confirmation-service';
import { ToastService } from '../../../core/services/toast-service';

@Component({
  selector: 'app-detalle-usuario-page',
  imports: [MisPedidosClientePage, EntregasPage, MatIcon, MatPaginator, CalificacionCardAdmin, EntregasPage],
  templateUrl: './detalle-usuario-page.html',
})
export class DetalleUsuarioPage {

  private uService = inject(UserService);
  private rService = inject (RestauranteService)
  private cService = inject(CalificacionService);
  private promptService = inject(PromptService)  
  private confirmationService = inject(ConfirmationService)
  private toastService = inject (ToastService)
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  protected user! : UserProfile
  protected rol? : string | null
  protected restaurante? : RestauranteResponse
  calificacionesPedidos?: CalificacionPedidoResponseDTO[];
  calificacionesRepartidores? : CalificacionRepartidorResponseDTO[];
  clienteFlag = false
  protected isLoading = true;

  sizeCalificacionPedidos: number = 5;
  pageCalificacionPedidos: number = 0;
  lengthCalificacionPedidos: number = 0;

  sizeCalificacionRepartidores: number = 5;
  pageCalificacionRepartidores: number = 0;
  lengthCalificacionRepartidores: number = 0;


  ngOnInit(): void {
    this.loadUsuario();
  }

  private loadUsuario(): void {
    const id = this.route.snapshot.paramMap.get('idUser');
    this.rol = this.route.snapshot.paramMap.get('idRol');
    
    if (!id) {
      this.router.navigate(['/restaurante/pedidos']);
      return;
    }
    
    this.uService.getUserByID(Number(id)).subscribe({
      next: (data) => {
        this.user = data;
        this.isLoading = false
        if(this.user.id){
          this.setFlag();
          this.cargarCalificacionesPedidos(this.rol!);
          this.cargarCalificacionesRepartidor(this.rol!);
          if(this.rol === 'ROLE_RESTAURANTE'){
            this.buscarRestaurante()}
        }
      },
      error: () => {
        this.isLoading = false;
        this.router.navigate(['/']);
      }
    });

  }

  private cargarCalificacionesPedidos(rol : string): void {
    if (rol === 'ROLE_RESTAURANTE'){
      if(this.restaurante?.id){
      }
      this.cService.getCalificacionesRestaurante(this.restaurante?.id!,this.pageCalificacionPedidos, this.sizeCalificacionPedidos).subscribe({
      next: (response) => {
        this.calificacionesPedidos = response.content;
        this.lengthCalificacionPedidos = response.totalElements;
      }
    });      
    }else if(rol === 'ROLE_CLIENTE'){
      this.cService.getCalificacionesPedidosCliente(this.pageCalificacionRepartidores,this.sizeCalificacionRepartidores,this.user?.id!).subscribe({
        next:(response)=>{
          this.calificacionesPedidos = response.content;
          this.lengthCalificacionPedidos = response.totalElements;
        }
      })
    }
  }

  cargarCalificacionesRepartidor(rol : string){
    if(rol === 'ROLE_REPARTIDOR'){
      this.cService.getCalificacionesRepartidor(this.user?.id!,this.pageCalificacionRepartidores, this.sizeCalificacionRepartidores).subscribe({
      next: (response) => {
        this.calificacionesRepartidores = response.content;
        this.lengthCalificacionRepartidores = response.totalElements;
    }});
    }else if(rol === 'ROLE_CLIENTE'){
      this.cService.getCalificacionesRepartidorCliente(this.pageCalificacionRepartidores,this.sizeCalificacionRepartidores, this.user?.id!).subscribe({
          next:(response)=>{
          this.calificacionesRepartidores = response.content;
          this.lengthCalificacionRepartidores = response.totalElements;
        }
      })
    }
  }

  onPageChangeCalificacionPedido(event: PageEvent, rol : string): void {
    this.pageCalificacionPedidos = event.pageIndex;
    this.sizeCalificacionPedidos = event.pageSize;
    this.cargarCalificacionesPedidos(rol);
  }

  onPageChangeCalificacionRepartidor(event: PageEvent, rol : string): void {
    this.pageCalificacionRepartidores = event.pageIndex;
    this.sizeCalificacionRepartidores = event.pageSize;
    this.cargarCalificacionesRepartidor(rol);
  }

  setFlag(){
    if(this.rol === 'ROLE_CLIENTE'){
      this.clienteFlag = true
    }
  }

  buscarRestaurante(){
    if(!this.user?.id) return
    this.rService.getByUsuario(this.user?.id).subscribe({
      next:(r)=> {this.restaurante= r
        this.cargarCalificacionesPedidos(this.rol!);
      }})
  }

  bloquearCuenta(){
    this.promptService.show({
      title: 'Bloquear Cuenta',
      message: 'Indica el motivo del bloqueo',
      placeholder: 'Ej: Muchas quejas sobre incoscistencias',
      required: true,
      confirmText: 'Bloquear Cuenta',
      type: 'danger'
    }).subscribe(result => {
      if (!result.confirmed) return;
      const blDTO : BajaLogisticaDTO = {
        motivo : result.value ?? ""
      }
      
      this.uService.darDeBaja(blDTO,this.user.id).subscribe({
        next:()=> {
          this.toastService.success('✅ Usuario bloqueado');
          this.loadUsuario()},
        error:()=> {
          console.log("No se ha podido bloquear la cuenta"),
          this.toastService.success(' No se ha podido bloquear al usuario');
        }
      })
    });
  }

  desbloquearCuenta(){
    this.confirmationService.confirm({
      title: 'Desbloquear cuenta',
          message: '¿Estás seguro de desbloquear esta cuenta?',
          confirmText: 'Aceptar',
          type: 'danger'
        }).subscribe(confirmed => {
          if (!confirmed) return;
    
          this.uService.reactivar(this.user.id).subscribe({
            next: () => {
              this.toastService.success('✅ Usuario desbloqueado');
              this.loadUsuario();
            },error:()=>{
              console.log("No se ha podido desbloquear la cuenta"),
              this.toastService.success(' No se ha podido desbloquear al usuario');
            }
          });
        });

  }

  volver(): void {
    this.router.navigate(['/restaurante/pedidos']);
  }
  
}