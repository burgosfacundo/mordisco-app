import { Component, inject, input } from '@angular/core';
import { ToastService } from '../../../core/services/toast-service';
import { PedidoService } from '../../../shared/services/pedido/pedido-service';
import { AuthService } from '../../../shared/services/auth-service';
import { RepartidorService } from '../../../shared/services/repartidor/repartidor-service';
import { MatDialog } from '@angular/material/dialog';
import PedidoResponse from '../../../shared/models/pedido/pedido-response';
import { ConfirmDialogComponent } from '../../../shared/store/confirm-dialog-component';
import { PageEvent, MatPaginator } from '@angular/material/paginator';
import { PedidoCardComponent } from "../../../shared/components/pedido-card-component/pedido-card-component";
import { MatIcon } from "@angular/material/icon";
import { Router } from '@angular/router';

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
  private dialog = inject(MatDialog);
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
            this.toastService.success('✅ Pedido marcado como "Recibido"');
            this.loadPedidos();
          }
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
