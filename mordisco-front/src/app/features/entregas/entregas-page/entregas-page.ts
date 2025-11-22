import { Component, inject, signal } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PedidoService } from '../../../shared/services/pedido/pedido-service';
import { AuthService } from '../../../shared/services/auth-service';
import { RepartidorService } from '../../../shared/services/repartidor/repartidor-service';
import { MatDialog } from '@angular/material/dialog';
import PedidoResponse from '../../../shared/models/pedido/pedido-response';
import { ConfirmDialogComponent } from '../../../shared/store/ConfirmDialogComponent';
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
  private _snackBar = inject(MatSnackBar);
  private router = inject(Router)
  private pedidoService = inject(PedidoService);
  private authService = inject(AuthService);
  private repartidorService = inject(RepartidorService);
  private dialog = inject(MatDialog);
  pedidos?: PedidoResponse[];
  
  sizePedidos : number = 5;
  pagePedidos : number = 0;
  length : number = 5;
  
  isLoading = true;

  ngOnInit(): void {
    this.loadPedidos();
  }

  private loadPedidos(): void {
    const user = this.authService.currentUser();
    
    if (!user?.userId) {
      this._snackBar.open('❌ No se encontró información del usuario', 'Cerrar' , { duration: 3000 });
      this.authService.logout();
      return;
    }

    this.repartidorService.getAllPedidosRepartidor(user.userId,this.pagePedidos,this.sizePedidos).subscribe({
        next: (data) => {
          this.pedidos = data.content;
          this.length = data.totalElements;
          this.isLoading = false;
        },error: () => {
          this._snackBar.open('❌ Error al cargar los pedidos', 'Cerrar' , { duration: 3000 });
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
            this.loadPedidos();
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

  onPageChangePedidos(event: PageEvent): void {
    this.pagePedidos = event.pageIndex
    this.sizePedidos = event.pageSize;
    this.loadPedidos();
  }
  navegarAHome() {
    this.router.navigate(['/home'])
  }
}
