import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductoPedidoCardComponent } from '../producto-pedido-card-component/producto-pedido-card-component';
import { DireccionCardComponent } from '../../../direccion/components/direccion-card-component/direccion-card-component';
import { PedidoService } from '../../../../shared/services/pedido/pedido-service';
import PedidoResponse from '../../../../shared/models/pedido/pedido-response';
import { TipoEntrega } from '../../../../shared/models/enums/tipo-entrega';
import { EstadoPedido, ESTADO_PEDIDO_LABELS } from '../../../../shared/models/enums/estado-pedido';
import { CommonModule } from '@angular/common';
import { UsuarioCardComponent } from "../../../../shared/components/usuario-card-component/usuario-card-component";
import { AuthService } from '../../../../shared/services/auth-service';
import { CalificacionService } from '../../../../shared/services/calificacion/calificacion-service';
import CalificacionPedidoResponseDTO from '../../../../shared/models/calificacion/calificacion-pedido-response-dto';
import CalificacionRepartidorResponseDTO from '../../../../shared/models/calificacion/calificacion-repartidor-response-dto';
import { CalificacionComponent } from '../../../../shared/components/calificacion-component/calificacion-component';
import { ToastService } from '../../../../core/services/toast-service';
import { ConfirmationService } from '../../../../core/services/confirmation-service';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../../../shared/store/confirm-dialog-component';
@Component({
  selector: 'app-detalle-pedido-page',
  standalone: true,
  imports: [
    CommonModule,
    ProductoPedidoCardComponent,
    DireccionCardComponent,
    UsuarioCardComponent,
    CalificacionComponent
],
  templateUrl: './detalle-pedido-page.html'
})
export class DetallePedidoPage implements OnInit {
  private authService = inject(AuthService);
  private cService = inject(CalificacionService)
  private toastService = inject(ToastService);
  private pedidoService = inject(PedidoService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private confirmationService = inject(ConfirmationService);
  private dialog = inject(MatDialog);

  protected pedido?: PedidoResponse;
  protected isLoading = true;
  protected isUsuario = this.authService.currentUser()?.role;
 
  calificacionPedido? : CalificacionPedidoResponseDTO
  calificacionRepartidor? : CalificacionRepartidorResponseDTO
  readonly tipoEntregaEnum = TipoEntrega;
  readonly estadoPedidoEnum = EstadoPedido;

  ngOnInit(): void {
    this.loadPedido();
  }

  private loadPedido(): void {
    const id = this.route.snapshot.paramMap.get('id');
    
    if (!id) {
      this.router.navigate(['/restaurante/pedidos']);
      return;
    }

    this.pedidoService.getById(Number(id)).subscribe({
      next: (data) => {
        this.pedido = data;
        this.setCalificados(this.pedido)
        this.isLoading = false
      },
      error: () => {
        this.isLoading = false;
        this.router.navigate(['/restaurante/pedidos']);
      }
    });

  }

  setCalificados(p : PedidoResponse){
    if(this.isUsuario === 'ROLE_CLIENTE'){
      this.obtenerCalificacionPedido(p)
      this.obtenerCalificacionRepartidor(p)
    }
  }

  formatearEstado(estado: EstadoPedido): string {
    return ESTADO_PEDIDO_LABELS[estado] || String(estado);
  }

  formatearEntrega(pedido: PedidoResponse): string {
    return pedido.tipoEntrega === TipoEntrega.DELIVERY ? 'Delivery' : 'Retiro en el local';
  }

  aceptarPedido(): void {
    this.confirmationService.confirm({
      title: 'Aceptar Pedido',
      message: '¿Estás seguro de aceptar este pedido?',
      confirmText: 'Sí, aceptar',
      cancelText: 'Cancelar',
      type: 'info'
    }).subscribe(confirmed => {
      if (!confirmed || !this.pedido?.id) return;

      this.pedidoService.changeState(this.pedido.id, EstadoPedido.EN_PREPARACION).subscribe({
        next: () => {
          this.toastService.success('✅ Pedido aceptado');
          this.loadPedido();
        }
      });
    });
  }

  rechazarPedido(): void {
    this.confirmationService.confirm({
      title: 'Rechazar Pedido',
      message: '¿Estás seguro de rechazar/cancelar este pedido?',
      confirmText: 'Sí, rechazar',
      cancelText: 'No, mantener',
      type: 'danger'
    }).subscribe(confirmed => {
      if (!confirmed || !this.pedido?.id) return;

      this.pedidoService.cancel(this.pedido.id).subscribe({
        next: () => {
          this.toastService.success('✅ Pedido cancelado');
          this.loadPedido();
        }
      });
    });
  }

  marcarEnCamino(): void {
    this.confirmationService.confirm({
      title: 'Cambiar Estado',
      message: '¿Marcar este pedido como "En Camino"?',
      confirmText: 'Sí, marcar',
      cancelText: 'Cancelar',
      type: 'warning'
    }).subscribe(confirmed => {
      if (!confirmed || !this.pedido?.id) return;

      this.pedidoService.changeState(this.pedido.id, EstadoPedido.EN_CAMINO).subscribe({
        next: () => {
          this.toastService.success('✅ Pedido marcado como "En Camino"');
          this.loadPedido();
        }
      });
    });
  }
  volver(): void {
    this.router.navigate(['/cliente/pedidos']);
  }

    /**
   * Determina si debe mostrar el botón de calificar
   */
  mostrarBotonCalificar(): boolean {
    const estado = this.pedido?.estado;
    return estado === EstadoPedido.COMPLETADO && (!this.calificacionPedido|| !this.calificacionRepartidor); 
  }

  calificarPedido(): void {
    this.router.navigate(['cliente/calificar','pedido', this.pedido?.id]);
  }

  calificarRepartidor(): void {
    this.router.navigate(['cliente/calificar','repartidor', this.pedido?.id]);
  }

  obtenerCalificacionPedido(p : PedidoResponse) : void{
    this.cService.getCalificacionPedido(p.id).subscribe({
      next:(data)=>{
        this.calificacionPedido = data
      }
    })
  }

  obtenerCalificacionRepartidor(p : PedidoResponse) : void{
    this.cService.getCalificacionRepartidor(p.id).subscribe({
      next:(data)=> {
        this.calificacionRepartidor = data
      }
    })
  }

  eliminarCalificacionRepartidor(id: number): void {
    this.confirmationService.confirm({
      title: 'Eliminar calificacion al repartidor',
          message: '¿Estás seguro que queres eliminar la calificacion al repartidor? Esta accion no tiene como deshacerse',
 
          type: 'danger'
        }).subscribe(confirmed => {
          if (!confirmed) return;
          this.pedidoService.cancel(this.pedido?.id!).subscribe({
                  next: () => {
                    this.toastService.success('✅ Pedido cancelado');
                    this.loadPedido();
                  }
          });
        });
  }
  
  eliminarCalificacionPedido(id: number): void {
    this.confirmationService.confirm({
      title: 'Eliminar calificacion al restaurante',
          message: '¿Estás seguro que queres eliminar la calificacion del pedido? Esta accion no tiene como deshacerse',
          confirmText: 'Aceptar',
          type: 'danger'
        }).subscribe(confirmed => {
          if (!confirmed) return;
          this.pedidoService.cancel(this.pedido?.id!).subscribe({
                  next: () => {
                    this.toastService.success('✅ Pedido cancelado');
                    this.loadPedido();
                  }
          });
        });
  }

  private reloadComponent(): void {
    const currentUrl = this.router.url;
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
    this.router.navigateByUrl(currentUrl);
  });
}
}