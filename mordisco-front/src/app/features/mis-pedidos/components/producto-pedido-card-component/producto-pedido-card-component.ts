import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import ProductoResponse from '../../../../shared/models/producto/producto-response';
import ProductoPedidoResponse from '../../../../shared/models/producto/productoPedidoResponse';

@Component({
  selector: 'app-producto-pedido-card-component',
  imports: [CommonModule],
  templateUrl: './producto-pedido-card-component.html',
  styleUrl: './producto-pedido-card-component.css'
})
export class ProductoPedidoCardComponent {
    @Input() producto?: ProductoPedidoResponse;

  /**
   * Calcula el subtotal del producto (precio unitario * cantidad)
   */
  calcularSubtotal(): number {
    if (!this.producto) return 0;
    return this.producto.precioUnitario * this.producto.cantidad;
  }
}
