import { Component, Input } from '@angular/core';
import ProductoPedidoResponse from '../../../../shared/models/producto/producto-pedido-response';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-producto-pedido-card-component',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './producto-pedido-card-component.html'
})
export class ProductoPedidoCardComponent {
  @Input() producto?: ProductoPedidoResponse;

  calcularSubtotal(): number {
    if (!this.producto) return 0;
    return this.producto.precioUnitario * this.producto.cantidad;
  }
}
