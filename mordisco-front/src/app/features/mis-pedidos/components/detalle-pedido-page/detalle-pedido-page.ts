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
import { ConfiguracionSistemaService } from '../../../../shared/services/configuracionSistema/configuracion-sistema-service';
import ConfiguracionSistemaGeneralResponseDTO from '../../../../shared/models/configuracion/configuracion-sistema-general-response-DTO';
import { MetodoPago } from '../../../../shared/models/enums/metodo-pago';
import PagoResponseDTO from '../../../../shared/models/pago/pago-response-dto';
import { PagoService } from '../../../../shared/services/pagos/pago-service';
import { PromptService } from '../../../../core/services/confirmation-prompt-service';

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
  private configService = inject(ConfiguracionSistemaService)
  private pagoService = inject(PagoService)
  private promptService = inject(PromptService);
 
  configSist? : ConfiguracionSistemaGeneralResponseDTO

  protected pedido?: PedidoResponse;
  protected isLoading = true;
  protected isUsuario = this.authService.currentUser()?.role;
  MetodoPago = MetodoPago
  pagoPedido? : PagoResponseDTO

  calificacionPedido? : CalificacionPedidoResponseDTO
  calificacionRepartidor? : CalificacionRepartidorResponseDTO
  readonly tipoEntregaEnum = TipoEntrega;
  readonly estadoPedidoEnum = EstadoPedido;

  ngOnInit(): void {
    this.loadPedido();
    this.getConfigSistema();
  }

  obtenerPagoPedido(id : number){
    this.pagoService.getPagoByPedidoId(id).subscribe({
      next:(d)=> this.pagoPedido = d
    })
  }

  getConfigSistema(){
    this.configService.getConfiguracionGeneral().subscribe({
      next:(d)=> this.configSist = d,
      error:(e)=> console.log("No se pudo obtener la configuracion actual", e)
    })
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
        this.obtenerPagoPedido(this.pedido.id)
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
      if(p.estado === EstadoPedido.COMPLETADO && p.tipoEntrega === TipoEntrega.DELIVERY && (!this.calificacionPedido || !this.calificacionRepartidor)){
        this.obtenerCalificacionPedido(p)
        this.obtenerCalificacionRepartidor(p)
      }else if(p.estado === EstadoPedido.COMPLETADO && p.tipoEntrega === TipoEntrega.RETIRO_POR_LOCAL && !this.calificacionPedido){
        this.obtenerCalificacionPedido(p)
      }
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

  marcarListoParaEntregar(): void {
    this.confirmationService.confirm({
      title: 'Cambiar Estado',
      message: '¿Marcar este pedido como "Listo para entregar"?',
      confirmText: 'Sí, marcar',
      cancelText: 'Cancelar',
      type: 'warning'
    }).subscribe(confirmed => {
      if (!confirmed || !this.pedido?.id) return;

      this.pedidoService.changeState(this.pedido.id, EstadoPedido.LISTO_PARA_ENTREGAR).subscribe({
        next: () => {
          this.toastService.success('✅ Pedido marcado como "Listo para entregar"');
          this.loadPedido();
        }
      });
    });
  }

  marcarListoParaRetirar(): void {
    this.confirmationService.confirm({
      title: 'Cambiar Estado',
      message: '¿Marcar este pedido como "Listo para retirar"?',
      confirmText: 'Sí, marcar',
      cancelText: 'Cancelar',
      type: 'warning'
    }).subscribe(confirmed => {
      if (!confirmed || !this.pedido?.id) return;

      this.pedidoService.changeState(this.pedido.id, EstadoPedido.LISTO_PARA_RETIRAR).subscribe({
        next: () => {
          this.toastService.success('✅ Pedido marcado como "Listo para retirar"');
          this.loadPedido();
        }
      });
    });
  }  

  marcarEnCamino(): void {
    this.confirmationService.confirm({
      title: 'Cambiar Estado a "En camino"',
      message: '¿Ya le entregaste al repartidor el pedido?',
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

  marcarRecibido(): void {
    if (!this.pedido) return;

    this.promptService.show({
      title: 'Marcar como entregado',
      message: 'Indica el PIN del pedido proporcionado por el cliente',
      placeholder: 'Ej: XXXXX',
      required: true,
      confirmText: 'Entregado',
      type: 'danger'
    }).subscribe(result => {
      if (!result.confirmed || !this.pedido) return;

      const PIN: string = result.value?.toUpperCase() ?? "";

      if (PIN === this.pedido.pin?.toUpperCase()) {
        this.pedidoService.marcarComoEntregado(this.pedido.id).subscribe({
          next: () => {
            this.toastService.success('✅ Pedido marcado como "Completado"');
            this.loadPedido();
          }
        });
      } else {
        this.promptService.updateValue(""); // limpia el valor
        this.promptService.shakeInput();    // vibra el input
        this.toastService.error("PIN incorrecto");
      }
    });
  }

  volver(): void {
    if(this.isUsuario === 'ROLE_CLIENTE')
      this.router.navigate(['/cliente/pedidos']);
    else if(this.isUsuario === 'ROLE_RESTAURANTE')
      this.router.navigate(['/restaurante/pedidos']);
    else if(this.isUsuario === 'ROLE_REPARTIDOR')
      this.router.navigate(['/']);
    else 
      this.router.navigate(['/admin/pedidos']);
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
          this.cService.eliminarCalificacionRepartidor(id).subscribe({
                  next: () => {
                    this.toastService.success('✅ Calificación eliminada');
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
          this.cService.eliminarCalificacionPedido(id).subscribe({
                  next: () => {
                    this.toastService.success('✅ Calificación eliminada');
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