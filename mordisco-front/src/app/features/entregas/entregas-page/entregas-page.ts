import { Component, inject, input } from '@angular/core';
import { ToastService } from '../../../core/services/toast-service';
import { PedidoService } from '../../../shared/services/pedido/pedido-service';
import { AuthService } from '../../../shared/services/auth-service';
import { RepartidorService } from '../../../shared/services/repartidor/repartidor-service';
import PedidoResponse from '../../../shared/models/pedido/pedido-response';
import { PageEvent, MatPaginator } from '@angular/material/paginator';
import { PedidoCardComponent } from "../../../shared/components/pedido-card-component/pedido-card-component";
import { MatIcon } from "@angular/material/icon";
import { Router } from '@angular/router';
import { ConfirmationService } from '../../../core/services/confirmation-service';

@Component({
  selector: 'app-entregas-page',
  imports: [MatPaginator, PedidoCardComponent, MatIcon],
  templateUrl: './entregas-page.html',
})
export class EntregasPage {
  private toastService = inject(ToastService);
  private router = inject(Router)
  private pedidoService = inject(PedidoService);
  private authService = inject(AuthService);
  private repartidorService = inject(RepartidorService);
  private confirmationService = inject(ConfirmationService);
  pedidos?: PedidoResponse[];
  adminMode = input<boolean>(false)
  admin_idUser = input<number>()
  
  sizePedidos : number = 5;
  pagePedidos : number = 0;
  length : number = 5;
  
  isLoading = true;

  ngOnInit(): void {
    this.loadPedidos();
  }

  private loadPedidos(): void {
    let userId : number | undefined
    if(!this.adminMode()){
      userId= this.authService.currentUser()?.userId
    }else{
      userId=this.admin_idUser()
    }

    if (!userId) {
      this.authService.logout();
      return;
    }

    this.repartidorService.getAllPedidosRepartidor(userId!,this.pagePedidos,this.sizePedidos).subscribe({
        next: (data) => {
          this.pedidos = data.content;
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
      confirmText: 'Rechazar',
      type: 'danger'
    }).subscribe(confirmed => {
      if (!confirmed) return;

      this.pedidoService.marcarComoEntregado(pedidoId).subscribe({
        next: () => {
          this.toastService.success('✅ Pedido marcado como "Completado"');
          this.loadPedidos();
        }
      });
    });
  }



  verDetalle(pedidoId: number): void {
    this.router.navigate(['/repartidor/pedidos/detalle', pedidoId])
  }

  onPageChangePedidos(event: PageEvent): void {
    this.pagePedidos = event.pageIndex
    this.sizePedidos = event.pageSize;
    this.loadPedidos();
  }
  navegarAHome() {
    this.router.navigate(['/home'])
  }
}
