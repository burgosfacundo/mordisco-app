import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { PedidoCardComponent } from '../../../../shared/components/pedido-card-component/pedido-card-component';
import { PedidoService } from '../../../../shared/services/pedido/pedido-service';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import { AuthService } from '../../../../shared/services/auth-service';
import PedidoResponse from '../../../../shared/models/pedido/pedido-response';
import RestauranteResponse from '../../../../shared/models/restaurante/restaurante-response';
import { EstadoPedido } from '../../../../shared/models/enums/estado-pedido';
import { ConfirmationService } from '../../../../core/services/confirmation-service';
import { ToastService } from '../../../../core/services/toast-service';

@Component({
  selector: 'app-mis-pedidos-page',
  standalone: true,
  imports: [
    CommonModule,
    PedidoCardComponent,
    MatPaginatorModule,
    MatTabsModule,
    MatIconModule
  ],
  templateUrl: './mis-pedidos-page.html',
  styleUrl: './mis-pedidos-page.css'
})
export class MisPedidosPage implements OnInit {
  private pService = inject(PedidoService);
  private rService = inject(RestauranteService);
  private auS = inject(AuthService);
  private router = inject(Router);
  private confirmationService = inject(ConfirmationService);
  private toastService = inject(ToastService);

  arrPedidos?: PedidoResponse[];
  restaurante?: RestauranteResponse;

  sizePedidos = 10;
  pagePedidos = 0;
  lengthPedidos = 0;
  isLoadingPedidos = true;

  selectedTabIndex = 0;
  estadoActual: EstadoPedido | 'TODOS' = 'TODOS';

  ngOnInit(): void {
    this.cargarRestaurante();
  }

  private cargarRestaurante(): void {
    const userId = this.auS.getCurrentUser()?.userId;

    if (!userId) {
      this.router.navigate(['/login']);
      return;
    }

    this.rService.getByUsuario(userId).subscribe({
      next: (data) => {
        this.restaurante = data;
        this.cargarPedidos();
      },
      error: () => {
        this.isLoadingPedidos = false;
      }
    });
  }

  private cargarPedidos(): void {
    if (!this.restaurante) return;

    this.isLoadingPedidos = true;

    if (this.estadoActual === 'TODOS') {
      this.pService.findAllByRestaurante_Id(
        this.restaurante.id,
        this.pagePedidos,
        this.sizePedidos
      ).subscribe({
        next: (response) => {
          this.arrPedidos = response.content;
          this.lengthPedidos = response.totalElements;
          this.isLoadingPedidos = false;
        },
        error: () => {
          this.isLoadingPedidos = false;
        }
      });
    } else {
      this.pService.getAllByRestaurante_IdAndEstado(
        this.restaurante.id,
        this.estadoActual,
        this.pagePedidos,
        this.sizePedidos
      ).subscribe({
        next: (response) => {
          this.arrPedidos = response.content;
          this.lengthPedidos = response.totalElements;
          this.isLoadingPedidos = false;
        },
        error: () => {
          this.isLoadingPedidos = false;
        }
      });
    }
  }

  onTabChange(index: number): void {
    const estados: (EstadoPedido | 'TODOS')[] = [
      'TODOS',
      EstadoPedido.PENDIENTE,
      EstadoPedido.EN_PREPARACION,
      EstadoPedido.LISTO_PARA_RETIRAR,
      EstadoPedido.LISTO_PARA_ENTREGAR,
      EstadoPedido.EN_CAMINO,
      EstadoPedido.COMPLETADO,
      EstadoPedido.CANCELADO
    ];

    this.estadoActual = estados[index];
    this.pagePedidos = 0;
    this.cargarPedidos();
  }

  onPageChangePedidos(event: PageEvent): void {
    this.pagePedidos = event.pageIndex;
    this.sizePedidos = event.pageSize;
    this.cargarPedidos();
  }

  aceptarPedido(pedidoId: number): void {
    this.confirmationService.confirm({
      title: 'Aceptar Pedido',
      message: '¿Estás seguro de aceptar este pedido?',
      confirmText: 'Aceptar',
      type: 'info'
    }).subscribe(confirmed => {
      if (!confirmed) return;

      this.pService.changeState(pedidoId, EstadoPedido.EN_PREPARACION).subscribe({
        next: () => {
          this.toastService.success('✅ Pedido aceptado');
          this.cargarPedidos();
        }
      });
    });
  }

  rechazarPedido(pedidoId: number): void {
    this.confirmationService.confirm({
      title: 'Rechazar Pedido',
      message: '¿Estás seguro de rechazar/cancelar este pedido? Esta acción no se puede deshacer.',
      confirmText: 'Rechazar',
      type: 'danger'
    }).subscribe(confirmed => {
      if (!confirmed) return;

      this.pService.cancel(pedidoId).subscribe({
        next: () => {
          this.toastService.success('✅ Pedido rechazado');
          this.cargarPedidos();
        }
      });
    });
  }

  cambiarEstado(event: { pedidoId: number, nuevoEstado: EstadoPedido }): void {
    const estadoLabel = event.nuevoEstado.replace(/_/g, ' ').toLowerCase();
    
    this.confirmationService.confirm({
      title: 'Cambiar Estado',
      message: `¿Cambiar estado del pedido a "${estadoLabel}"?`,
      confirmText: 'Cambiar',
      type: 'warning'
    }).subscribe(confirmed => {
      if (!confirmed) return;

      this.pService.changeState(event.pedidoId, event.nuevoEstado).subscribe({
        next: () => {
          this.toastService.success(`✅ Estado actualizado a "${estadoLabel}"`);
          this.cargarPedidos();
        }
      });
    });
  }
}