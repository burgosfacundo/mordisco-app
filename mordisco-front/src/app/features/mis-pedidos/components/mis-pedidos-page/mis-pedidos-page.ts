import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
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
  private _snackBar = inject(MatSnackBar);
  private router = inject(Router);

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
      this._snackBar.open('Error: Usuario no autenticado', 'Cerrar', { duration: 3000 });
      this.router.navigate(['/login']);
      return;
    }

    this.rService.getByUsuario(userId).subscribe({
      next: (data) => {
        this.restaurante = data;
        this.cargarPedidos();
      },
      error: (e) => {
        console.error('Error al cargar restaurante:', e);
        this._snackBar.open('Error al cargar el restaurante', 'Cerrar', { duration: 3000 });
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
          this._snackBar.open('Error al cargar pedidos', 'Cerrar', { duration: 3000 });
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
          this._snackBar.open('Error al cargar pedidos', 'Cerrar', { duration: 3000 });
          this.isLoadingPedidos = false;
        }
      });
    }
  }

  onTabChange(index: number): void {
    const estados: (EstadoPedido | 'TODOS')[] = [
      'TODOS',
      EstadoPedido.PENDIENTE,
      EstadoPedido.EN_PROCESO,
      EstadoPedido.EN_CAMINO,
      EstadoPedido.RECIBIDO,
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
    if (!confirm('¿Aceptar este pedido?')) return;

    this.pService.changeState(pedidoId, EstadoPedido.EN_PROCESO).subscribe({
      next: () => {
        this._snackBar.open('✅ Pedido aceptado', 'Cerrar', { duration: 3000 });
        this.cargarPedidos();
      },
      error: (error) => {
        console.error('Error al aceptar pedido:', error);
        this._snackBar.open(
          error.error?.message || 'No se pudo aceptar el pedido',
          'Cerrar',
          { duration: 4000 }
        );
      }
    });
  }

  rechazarPedido(pedidoId: number): void {
    if (!confirm('¿Rechazar/Cancelar este pedido?')) return;

    this.pService.cancel(pedidoId).subscribe({
      next: () => {
        this._snackBar.open('✅ Pedido cancelado', 'Cerrar', { duration: 3000 });
        this.cargarPedidos();
      },
      error: (error) => {
        console.error('Error al cancelar pedido:', error);
        this._snackBar.open(
          error.error?.message || 'No se pudo cancelar el pedido',
          'Cerrar',
          { duration: 4000 }
        );
      }
    });
  }

  marcarEnCamino(pedidoId: number): void {
    if (!confirm('¿Marcar pedido como "En Camino"?')) return;

    this.pService.changeState(pedidoId, EstadoPedido.EN_CAMINO).subscribe({
      next: () => {
        this._snackBar.open('✅ Pedido marcado como "En Camino"', 'Cerrar', { duration: 3000 });
        this.cargarPedidos();
      },
      error: (error) => {
        console.error('Error al actualizar pedido:', error);
        this._snackBar.open(
          error.error?.message || 'No se pudo actualizar el pedido',
          'Cerrar',
          { duration: 4000 }
        );
      }
    });
  }
}