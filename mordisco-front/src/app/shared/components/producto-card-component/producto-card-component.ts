import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import ProductoCard from '../../models/producto/producto-card';
import { ImageFallbackDirective } from '../../directives/image-fallback.directive';

@Component({
  selector: 'app-producto-card-component',
  imports: [CommonModule, ImageFallbackDirective],
  templateUrl: './producto-card-component.html'
})
export class ProductoCardComponent {
  producto = input<ProductoCard>()
}
