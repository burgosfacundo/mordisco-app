import { Component, inject } from '@angular/core';
import { PedidoService } from '../../../../shared/services/pedido/pedido-service';
import { AuthService } from '../../../../shared/services/auth-service';
import PedidoResponse from '../../../../shared/models/pedido/pedido-response';
import RestauranteResponse from '../../../../shared/models/restaurante/restaurante-response';
import { RepartidorService } from '../../../../shared/services/repartidor/repartidor-service';
import { PageEvent, MatPaginator } from '@angular/material/paginator';
import { PedidoCardComponent } from "../../../../shared/components/pedido-card-component/pedido-card-component";
import { MatDialog} from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../../../shared/store/confirm-dialog-component';
import { Router } from '@angular/router';
import { NotificationService } from '../../../../core/services/notification-service';
import { PedidosDisponiblesComponent } from '../../../../shared/components/pedidos-disponibles/pedidos-disponibles';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home-repartidor-component',
  imports: [PedidoCardComponent, MatPaginator, PedidosDisponiblesComponent, CommonModule],
  templateUrl: './home-repartidor-component.html',
})
export class HomeRepartidorComponent {
  private notificationService = inject(NotificationService);
  private pedidoService = inject(PedidoService);
  private authService = inject(AuthService);
  private repartidorService = inject(RepartidorService);
  private dialog = inject(MatDialog);
  private router = inject(Router)

  // Tab activa
  tabActiva: 'disponibles' | 'asignados' = 'disponibles';

  pedidosPendientes?: PedidoResponse[];

  sizePedidos : number = 5;
  pagePedidos : number = 0;
  length : number = 5;
  
  isLoading = true;

  restaurante?: RestauranteResponse;

  ngOnInit(): void {
    this.loadPedidosEnCamino();
  }

  /**
   * Cambia entre tabs
   */
  cambiarTab(tab: 'disponibles' | 'asignados'): void {
    this.tabActiva = tab;
    if (tab === 'asignados') {
      this.loadPedidosEnCamino();
    }
  }

  /**
   * Maneja el evento cuando se acepta un pedido desde pedidos-disponibles
   */
  onPedidoAceptado(pedidoId: number): void {
    // Cambiar a la tab de asignados y recargar
    this.tabActiva = 'asignados';
    this.loadPedidosEnCamino();
  }

  private loadPedidosEnCamino(): void {
    const user = this.authService.currentUser();
    
    if (!user?.userId) {
      this.authService.logout();
      return;
    }

    this.repartidorService.getPedidosAEntregar(user.userId,this.pagePedidos,this.sizePedidos).subscribe({
        next: (data) => {
          this.pedidosPendientes = data.content;
          this.length = data.totalElements;
          this.isLoading = false;
        },error: () => {
          this.isLoading = false;
        }
    });
  }

  marcarRecibido(pedidoID : number) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '300px',
      data: { mensaje: '¿Estás seguro de que querés aceptar este pedido?' }
    });

    dialogRef.afterClosed().subscribe((resultado) => {
      if (resultado === true) {
        this.guardarAceptacion(pedidoID);
      }
    });
  }

  guardarAceptacion(pedidoId : number) {
    this.pedidoService.marcarComoEntregado(pedidoId).subscribe({
          next: () => {
            this.notificationService.success('✅ Pedido marcado como "Recibido"');
            this.loadPedidosEnCamino();
          }
    });
  }

  verDetalle(pedidoId: number): void {
    this.router.navigate(['/repartidor/pedidos/detalle', pedidoId])
  }

  onPageChangePedidosEnCamino(event: PageEvent): void {
    this.pagePedidos = event.pageIndex
    this.sizePedidos = event.pageSize;
    this.loadPedidosEnCamino();
  }
}
