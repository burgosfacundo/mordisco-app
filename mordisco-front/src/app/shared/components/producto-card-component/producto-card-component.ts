import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import ProductoCard from '../../models/producto/producto-card';

@Component({
  selector: 'app-producto-card-component',
  imports: [CommonModule],
  templateUrl: './producto-card-component.html'
})
export class ProductoCardComponent {
  producto = input<ProductoCard>()
}
