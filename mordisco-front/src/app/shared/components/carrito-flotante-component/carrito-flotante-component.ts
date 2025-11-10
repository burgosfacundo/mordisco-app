import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatBadgeModule } from '@angular/material/badge';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { CarritoService } from '../../services/carrito/carrito-service';

@Component({
  selector: 'app-carrito-flotante',
  standalone: true,
  imports: [
    CommonModule,
    MatBadgeModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule
  ],
  templateUrl: './carrito-flotante-component.html',
  styleUrl: './carrito-flotante-component.css'
})
export class CarritoFlotanteComponent {
  private router = inject(Router);
  protected carritoService = inject(CarritoService);

  tieneItems = this.carritoService.tieneItems;
  cantidadTotal = this.carritoService.cantidadTotal;
  total = this.carritoService.total;
  restauranteActual = this.carritoService.restauranteActual;

  expandido = false;

  toggleExpandir(): void {
    this.expandido = !this.expandido;
  }

  irAlCarrito(): void {
    this.router.navigate(['/cliente/carrito']);
  }

  irACheckout(): void {
    this.router.navigate(['/cliente/checkout']);
  }
}