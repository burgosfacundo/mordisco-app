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
import { ToastService } from '../../../../core/services/toast-service';
import { PedidosDisponiblesComponent } from '../../../../shared/components/pedidos-disponibles/pedidos-disponibles';
import { GananciasRepartidorComponent } from '../../../../shared/components/ganancias-repartidor/ganancias-repartidor';
import { CommonModule } from '@angular/common';
import { ConfirmationService } from '../../../../core/services/confirmation-service';

@Component({
  selector: 'app-home-repartidor-component',
  imports: [PedidoCardComponent, MatPaginator, PedidosDisponiblesComponent, GananciasRepartidorComponent, CommonModule],
  templateUrl: './home-repartidor-component.html',
})
export class HomeRepartidorComponent {
  private toastService = inject(ToastService);
  private pedidoService = inject(PedidoService);
  private authService = inject(AuthService);
  private repartidorService = inject(RepartidorService);
  private confirmationService = inject(ConfirmationService);
  private router = inject(Router)

  // Tab activa
  tabActiva: 'disponibles' | 'asignados' | 'ganancias' = 'disponibles';

  pedidosPendientes?: PedidoResponse[];

  sizePedidos : number = 5;
  pagePedidos : number = 0;
  length : number = 5;
  
  isLoading = true;

  restaurante?: RestauranteResponse;

  ngOnInit(): void {
        /*
    if(true){
      this.cuentaDesactivada();
      return;
    }
    */
    this.loadPedidosEnCamino();
  }

  /**
   * Cambia entre tabs
   */
  cambiarTab(tab: 'disponibles' | 'asignados' | 'ganancias'): void {
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

  marcarRecibido(pedidoId: number): void {
    this.confirmationService.confirm({
      title: 'Marcar como entregado',
      message: '¿Estás seguro que este pedido fue recibido por el cliente? Esta acción no se puede deshacer.',
      confirmText: 'Entregado',
      type: 'warning'
    }).subscribe(confirmed => {
      if (!confirmed) return;

      this.pedidoService.marcarComoEntregado(pedidoId).subscribe({
        next: () => {
          this.toastService.success('✅ Pedido marcado como "Completado"');
          this.loadPedidosEnCamino();
        }
      });
    });
  }


  onVerDetalle(pedidoId: number): void {
    this.router.navigate(['/repartidor/pedidos/detalle', pedidoId])
  }

  onPageChangePedidosEnCamino(event: PageEvent): void {
    this.pagePedidos = event.pageIndex
    this.sizePedidos = event.pageSize;
    this.loadPedidosEnCamino();
  }

  cuentaDesactivada(){
    this.confirmationService.confirm({
      title: 'Su cuenta esta bloqueada',
      message: 'Motivo: tal. Para ser desbloqueado envia un mail a mordisco@gmail.com',
      confirmText: 'Ok',
      type: 'danger',
      showCancelButton : false
    }).subscribe((confirmed) => {
      if (confirmed) this.authService.logout(); 
    });
  } 
}
