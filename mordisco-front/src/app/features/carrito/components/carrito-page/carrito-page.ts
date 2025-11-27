import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { CarritoService } from '../../../../shared/services/carrito/carrito-service';
import { NotificationService } from '../../../../core/services/notification-service';
import { ConfirmDialogComponent } from '../../../../shared/store/confirm-dialog-component';

@Component({
  selector: 'app-carrito-page',
  standalone: true,
  imports: [CommonModule, RouterLink, MatIconModule],
  templateUrl: './carrito-page.html'
})
export class CarritoPage {
  private router = inject(Router)
  private notificationService = inject(NotificationService)
  private dialog = inject(MatDialog)
  protected carritoService = inject(CarritoService)

  // Computed values
  items = this.carritoService.items
  resumen = this.carritoService.resumen
  tieneItems = this.carritoService.tieneItems

  incrementar(productoId: number): void {
    this.carritoService.incrementarCantidad(productoId)
  }

  decrementar(productoId: number): void {
    this.carritoService.decrementarCantidad(productoId)
  }

  actualizarCantidad(productoId: number, event: Event): void {
    const input = event.target as HTMLInputElement
    const cantidad = parseInt(input.value, 10)
    
    if (isNaN(cantidad) || cantidad < 1) {
      input.value = '1'
      this.carritoService.actualizarCantidad(productoId, 1)
      return
    }
    
    this.carritoService.actualizarCantidad(productoId, cantidad)
  }

  eliminarProducto(productoId: number): void {
    const item = this.carritoService.obtenerProducto(productoId)
    
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: { mensaje: `¿Eliminar "${item?.nombre}" del carrito?` }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.carritoService.eliminarProducto(productoId)
        this.notificationService.success('Producto eliminado del carrito')
      }
    });
  }

  vaciarCarrito(): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: { mensaje: '¿Estás seguro de vaciar el carrito? Esta acción no se puede deshacer.' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.carritoService.vaciarCarrito()
        this.notificationService.success('Carrito vaciado')
        this.router.navigate(['/home'])
      }
    });
  }

  continuarComprando(): void {
    this.router.navigate(['/home'])
  }

  irACheckout(): void {
    // Validar disponibilidad antes de ir al checkout
    const validacion = this.carritoService.validarDisponibilidad()
    
    if (!validacion.valido) {
      this.notificationService.error(
        `Los siguientes productos no están disponibles: ${validacion.productosNoDisponibles.join(', ')}`
      )
      return
    }
    
    this.router.navigate(['/cliente/checkout'])
  }
}