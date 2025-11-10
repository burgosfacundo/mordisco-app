import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import ProductoResponse from '../../models/producto/producto-response';
@Component({
  selector: 'app-producto-card-with-add',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './producto-card-with-add.html'
})
export class ProductoCardWithAdd{
  @Input() producto!: ProductoResponse;
  @Input() enCarrito = false;
  @Input() cantidad = 0;
  
  @Output() agregar = new EventEmitter<void>();

  onAgregar(): void {
    this.agregar.emit();
  }
}
