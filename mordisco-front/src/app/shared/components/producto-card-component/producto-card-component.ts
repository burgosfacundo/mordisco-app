import { Component, input } from '@angular/core';
import ProductoCard from '../../models/producto/producto-card';

@Component({
  selector: 'app-producto-card-component',
  imports: [],
  templateUrl: './producto-card-component.html'
})
export class ProductoCardComponent {
  producto = input<ProductoCard>()
}
