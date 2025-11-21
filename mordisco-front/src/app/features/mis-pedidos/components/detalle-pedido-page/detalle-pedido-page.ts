import { Component, EventEmitter, inject, OnInit, output, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ProductoPedidoCardComponent } from '../producto-pedido-card-component/producto-pedido-card-component';
import { DireccionCardComponent } from '../../../direccion/components/direccion-card-component/direccion-card-component';
import { PedidoService } from '../../../../shared/services/pedido/pedido-service';
import PedidoResponse from '../../../../shared/models/pedido/pedido-response';
import { TipoEntrega } from '../../../../shared/models/enums/tipo-entrega';
import { EstadoPedido } from '../../../../shared/models/enums/estado-pedido';
import { CommonModule } from '@angular/common';
import { UsuarioCardComponent } from "../../../../shared/components/usuario-card-component/usuario-card-component";
import { AuthService } from '../../../../shared/services/auth-service';
import { CalificacionService } from '../../../../shared/services/calificacion/calificacion-service';
import { CalificacionFormPedidoComponent } from '../../../calificacion/calificacion-form-pedido-component/calificacion-form-pedido-component';
import { CalificacionFormRepartidorComponent } from '../../../calificacion/calificacion-form-repartidor-component/calificacion-form-repartidor-component';
@Component({
  selector: 'app-detalle-pedido-page',
  standalone: true,
  imports: [
    CommonModule,
    ProductoPedidoCardComponent,
    DireccionCardComponent,
    UsuarioCardComponent,
],
  templateUrl: './detalle-pedido-page.html'
})
export class DetallePedidoPage implements OnInit {
  private authService = inject(AuthService);
  private cService = inject(CalificacionService)
  private _snackBar = inject(MatSnackBar);
  private pedidoService = inject(PedidoService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  protected pedido?: PedidoResponse;
  protected isLoading = true;
  protected isUsuario = this.authService.currentUser()?.role;
  yaCalificoPedido = false
  yaCalificoRepartidor = false
  readonly tipoEntregaEnum = TipoEntrega;
  readonly estadoPedidoEnum = EstadoPedido;

  ngOnInit(): void {
    this.loadPedido();
  }

  private loadPedido(): void {
    const id = this.route.snapshot.paramMap.get('id');
    
    if (!id) {
      this._snackBar.open('❌ ID de pedido no válido', 'Cerrar', { duration: 3000 });
      this.router.navigate(['/restaurante/pedidos']);
      return;
    }

    this.pedidoService.getById(Number(id)).subscribe({
      next: (data) => {
        this.pedido = data;
        this.setCalificados(this.pedido)
        this.isLoading = false
      },
      error: (error) => {
        console.error('Error al cargar pedido:', error);
        this._snackBar.open('❌ Error al cargar el pedido', 'Cerrar', { duration: 3000 });
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
    const estados: Record<EstadoPedido, string> = {
      [EstadoPedido.PENDIENTE]: 'Pendiente',
      [EstadoPedido.EN_PROCESO]: 'En Proceso',
      [EstadoPedido.EN_CAMINO]: 'En Camino',
      [EstadoPedido.RECIBIDO]: 'Entregado',
      [EstadoPedido.CANCELADO]: 'Cancelado'
    };
    return estados[estado] || String(estado);
  }

  formatearEntrega(pedido: PedidoResponse): string {
    return pedido.tipoEntrega === TipoEntrega.DELIVERY ? 'Delivery' : 'Retiro en el local';
  }

  aceptarPedido(): void {
    if (!this.pedido?.id || !confirm('¿Aceptar este pedido?')) return;

    this.pedidoService.changeState(this.pedido.id, EstadoPedido.EN_PROCESO).subscribe({
      next: () => {
        this._snackBar.open('✅ Pedido aceptado', 'Cerrar', { duration: 3000 });
        this.loadPedido();
      },
      error: (error) => {
        console.error('Error:', error);
        this._snackBar.open('No se pudo aceptar el pedido', 'Cerrar', { duration: 4000 });
      }
    });
  }

  rechazarPedido(): void {
    if (!this.pedido?.id || !confirm('¿Rechazar/Cancelar este pedido?')) return;

    this.pedidoService.cancel(this.pedido.id).subscribe({
      next: () => {
        this._snackBar.open('✅ Pedido cancelado', 'Cerrar', { duration: 3000 });
        this.loadPedido();
      },
      error: (error) => {
        console.error('Error:', error);
        this._snackBar.open('No se pudo cancelar el pedido', 'Cerrar', { duration: 4000 });
      }
    });
  }

  marcarEnCamino(): void {
    if (!this.pedido?.id || !confirm('¿Marcar como "En Camino"?')) return;

    this.pedidoService.changeState(this.pedido.id, EstadoPedido.EN_CAMINO).subscribe({
      next: () => {
        this._snackBar.open('✅ Pedido marcado como "En Camino"', 'Cerrar', { duration: 3000 });
        this.loadPedido();
      },
      error: (error) => {
        console.error('Error:', error);
        this._snackBar.open('No se pudo actualizar el pedido', 'Cerrar', { duration: 4000 });
      }
    });
  }
  volver(): void {
    this.router.navigate(['/restaurante/pedidos']);
  }

    /**
   * Determina si debe mostrar el botón de calificar
   */
  mostrarBotonCalificar(): boolean {
    const estado = this.pedido?.estado;
    return estado === EstadoPedido.RECIBIDO && (!this.yaCalificoPedido|| !this.yaCalificoRepartidor); 
  }

  calificarPedido(): void {
    this.router.navigate(['cliente/calificar','pedido', this.pedido?.id]);
  }

  calificarRepartidor(): void {
    this.router.navigate(['cliente/calificar','repartidor', this.pedido?.id]);
  }

  obtenerCalificacionPedido(p : PedidoResponse) : void{
    this.cService.getCalificacionPedido(p.id).subscribe({
      next:()=>{
        this.yaCalificoPedido = true
      },
      error:(e)=> {console.log(e)}
    })
  }

  obtenerCalificacionRepartidor(p : PedidoResponse) : void{
    this.cService.getCalificacionRepartidor(p.id).subscribe({
      next:()=> {
        this.yaCalificoRepartidor = true
      },
      error:(e)=> {console.log(e)}
    })
  }
}