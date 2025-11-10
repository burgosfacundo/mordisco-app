import { Component, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatIconModule } from '@angular/material/icon';
import { CarritoService } from '../../../../shared/services/carrito/carrito-service';

@Component({
  selector: 'app-carrito-page',
  standalone: true,
  imports: [CommonModule, RouterLink, MatIconModule],
  templateUrl: './carrito-page.html'
})
export class CarritoPage {
  private router = inject(Router)
  private snackBar = inject(MatSnackBar)
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
    
    if (confirm(`¿Eliminar "${item?.nombre}" del carrito?`)) {
      this.carritoService.eliminarProducto(productoId)
      this.snackBar.open('Producto eliminado del carrito', 'Cerrar', { duration: 2000 })
    }
  }

  vaciarCarrito(): void {
    if (confirm('¿Estás seguro de vaciar el carrito? Esta acción no se puede deshacer.')) {
      this.carritoService.vaciarCarrito()
      this.snackBar.open('Carrito vaciado', 'Cerrar', { duration: 2000 })
      this.router.navigate(['/home'])
    }
  }

  continuarComprando(): void {
    this.router.navigate(['/home'])
  }

  irACheckout(): void {
    // Validar disponibilidad antes de ir al checkout
    const validacion = this.carritoService.validarDisponibilidad()
    
    if (!validacion.valido) {
      this.snackBar.open(
        `Los siguientes productos no están disponibles: ${validacion.productosNoDisponibles.join(', ')}`,
        'Cerrar',
        { duration: 5000 }
      )
      return
    }
    
    this.router.navigate(['/cliente/checkout'])
  }
}