import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PedidoService } from '../../../../shared/services/pedido/pedido-service';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { AuthService } from '../../../../shared/services/auth-service';
import { PedidoCardComponent } from '../../../../shared/components/pedido-card-component/pedido-card-component';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import RestauranteResponse from '../../../../shared/models/restaurante/restaurante-response';
import PedidoResponse from '../../../../shared/models/pedido/pedido-response';
import { Router } from '@angular/router';
import { EstadoPedido } from '../../../../shared/models/enums/estado-pedido';
import { ToastService } from '../../../../core/services/toast-service';
import { ConfirmationService } from '../../../../core/services/confirmation-service';

@Component({
  selector: 'app-home-restaurante-component',
  standalone: true,
  imports: [PedidoCardComponent, MatPaginator, CommonModule],
  templateUrl: './home-restaurante-component.html'
})
export class HomeRestauranteComponent implements OnInit {
  private toastService = inject(ToastService);
  private confirmationService = inject(ConfirmationService);
  private pedidoService = inject(PedidoService);
  private authService = inject(AuthService);
  private restauranteService = inject(RestauranteService);
  private router = inject(Router);

  pedidosPendientes?: PedidoResponse[];

  sizePedidos : number = 5;
  pagePedidos : number = 0;
  length : number = 5;
  
  isLoading = true;

  restaurante?: RestauranteResponse;

  ngOnInit(): void {
    this.loadRestauranteData();
  }

  private loadRestauranteData(): void {
    const user = this.authService.currentUser();
    
    if (!user?.userId) {
      this.authService.logout();
      return;
    }
    
    this.restauranteService.getByUsuario(user.userId).subscribe({
      next: (r) => {
        this.restaurante = r
        this.loadPedidosPendientes(r.id)
      },
      error: () => {
        this.router.navigate(['/restaurante'])
        this.isLoading = false
      }
    });
  }

  private loadPedidosPendientes(restauranteId: number): void {
    this.pedidoService.getAllByRestaurante_IdAndEstado(restauranteId, 'PENDIENTE',this.pagePedidos,this.sizePedidos)
      .subscribe({
        next: (data) => {
          this.pedidosPendientes = data.content;
          this.length = data.totalElements;
          this.isLoading = false;
        },
        error: () => {
          this.isLoading = false;
        }
      });
  }

  aceptarPedido(pedidoId: number): void {
    this.confirmationService.confirm({
      title: 'Aceptar Pedido',
      message: '¿Estás seguro de aceptar este pedido?',
      confirmText: 'Sí, aceptar',
      cancelText: 'Cancelar',
      type: 'info'
    }).subscribe(confirmed => {
      if (!confirmed) return;

      this.pedidoService.changeState(pedidoId, EstadoPedido.EN_PREPARACION).subscribe({
        next: () => {
          this.toastService.success('✅ Pedido aceptado');
          this.loadRestauranteData();
        }
      });
    });
  }

  rechazarPedido(pedidoId: number): void {
    this.confirmationService.confirm({
      title: 'Rechazar Pedido',
      message: '¿Estás seguro de rechazar/cancelar este pedido?',
      confirmText: 'Sí, rechazar',
      cancelText: 'No, mantener',
      type: 'danger'
    }).subscribe(confirmed => {
      if (!confirmed) return;

      this.pedidoService.cancel(pedidoId).subscribe({
        next: () => {
          this.toastService.success('✅ Pedido cancelado');
          this.loadRestauranteData();
        }
      });
    });
  }

  cambiarEstado(event: { pedidoId: number, nuevoEstado: EstadoPedido }): void {
    const estadoLabel = event.nuevoEstado.replace(/_/g, ' ').toLowerCase();
    
    this.confirmationService.confirm({
      title: 'Cambiar Estado',
      message: `¿Cambiar estado del pedido a "${estadoLabel}"?`,
      confirmText: 'Sí, cambiar',
      cancelText: 'Cancelar',
      type: 'warning'
    }).subscribe(confirmed => {
      if (!confirmed) return;

      this.pedidoService.changeState(event.pedidoId, event.nuevoEstado).subscribe({
        next: () => {
          this.toastService.success(`✅ Estado actualizado a "${estadoLabel}"`);
          this.loadRestauranteData();
        }
      });
    });
  }


  onPageChangePedidos(event: PageEvent): void {
    this.pagePedidos = event.pageIndex
    this.sizePedidos = event.pageSize;
    this.loadRestauranteData();
  }


}