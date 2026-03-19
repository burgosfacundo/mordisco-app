import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import ProductoResponse from '../../models/producto/producto-response';
import { ImageFallbackDirective } from '../../directives/image-fallback.directive';

@Component({
  selector: 'app-producto-card-with-add',
  standalone: true,
  imports: [CommonModule, MatIconModule, ImageFallbackDirective],
  templateUrl: './producto-card-with-add.html'
})
export class ProductoCardWithAdd{
  producto = input<ProductoResponse>();
  enCarrito = input<boolean>(false);
  cantidad = input<number>(0);
  adminMode = input<Boolean>(false)
  
  agregar = output<void>();

  onAgregar(): void {
    this.agregar.emit();
  }
}
