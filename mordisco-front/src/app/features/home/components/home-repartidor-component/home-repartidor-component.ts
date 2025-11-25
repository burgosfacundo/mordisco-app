import { Component, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PedidoService } from '../../../../shared/services/pedido/pedido-service';
import { AuthService } from '../../../../shared/services/auth-service';
import PedidoResponse from '../../../../shared/models/pedido/pedido-response';
import RestauranteResponse from '../../../../shared/models/restaurante/restaurante-response';
import { RepartidorService } from '../../../../shared/services/repartidor/repartidor-service';
import { PageEvent, MatPaginator } from '@angular/material/paginator';
import { PedidoCardComponent } from "../../../../shared/components/pedido-card-component/pedido-card-component";
import {MatDialog} from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../../../shared/store/ConfirmDialogComponent';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home-repartidor-component',
  imports: [PedidoCardComponent, MatPaginator],
  templateUrl: './home-repartidor-component.html',
})
export class HomeRepartidorComponent {
  private _snackBar = inject(MatSnackBar);
  private pedidoService = inject(PedidoService);
  private authService = inject(AuthService);
  private repartidorService = inject(RepartidorService);
  private dialog = inject(MatDialog);
  private router = inject(Router)

  pedidosPendientes?: PedidoResponse[];

  sizePedidos : number = 5;
  pagePedidos : number = 0;
  length : number = 5;
  
  isLoading = true;

  restaurante?: RestauranteResponse;

  ngOnInit(): void {
    this.loadPedidosEnCamino();
  }

  private loadPedidosEnCamino(): void {
    const user = this.authService.currentUser();
    
    if (!user?.userId) {
      this._snackBar.open('❌ No se encontró información del usuario', 'Cerrar' , { duration: 3000 });
      this.authService.logout();
      return;
    }

    this.repartidorService.getPedidosAEntregar(user.userId,this.pagePedidos,this.sizePedidos).subscribe({
        next: (data) => {
          this.pedidosPendientes = data.content;
          this.length = data.totalElements;
          this.isLoading = false;
        },error: () => {
          this._snackBar.open('❌ Error al cargar los pedidos pendientes', 'Cerrar' , { duration: 3000 });
          this.isLoading = false;}
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
            this._snackBar.open('✅ Pedido marcado como "Recibido"', 'Cerrar', { duration: 3000 });
            this.loadPedidosEnCamino();
          },
          error: (error) => {
            console.error('Error al actualizar pedido:', error);
            this._snackBar.open(
              error.error?.message || 'No se pudo actualizar el pedido',
              'Cerrar',
              { duration: 4000 }
            );
          }});
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
