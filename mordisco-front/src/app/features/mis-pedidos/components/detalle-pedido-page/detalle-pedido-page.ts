import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UsuarioCardComponent } from '../../../../shared/components/usuario-card-component/usuario-card-component';
import { ProductoPedidoCardComponent } from '../producto-pedido-card-component/producto-pedido-card-component';
import { DireccionCardComponent } from '../../../direccion/components/direccion-card-component/direccion-card-component';
import { PedidoService } from '../../../../shared/services/pedido/pedido-service';
import PedidoResponse from '../../../../shared/models/pedido/pedido-response';
import { TipoEntrega } from '../../../../shared/models/enums/tipo-entrega';
import { EstadoPedido } from '../../../../shared/models/enums/estado-pedido';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-detalle-pedido-page',
  standalone: true,
  imports: [
    CommonModule,
    UsuarioCardComponent,
    ProductoPedidoCardComponent,
    DireccionCardComponent
  ],
  templateUrl: './detalle-pedido-page.html'
})
export class DetallePedidoPage implements OnInit {
  private _snackBar = inject(MatSnackBar);
  private pedidoService = inject(PedidoService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  protected pedido?: PedidoResponse;
  protected isLoading = true;
  
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
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error al cargar pedido:', error);
        this._snackBar.open('❌ Error al cargar el pedido', 'Cerrar', { duration: 3000 });
        this.isLoading = false;
        this.router.navigate(['/restaurante/pedidos']);
      }
    });
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

  volver(): void {
    this.router.navigate(['/restaurante/pedidos']);
  }
}