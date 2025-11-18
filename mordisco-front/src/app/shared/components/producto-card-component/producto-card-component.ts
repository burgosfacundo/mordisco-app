import { Component, input, Input } from '@angular/core';
import ProductoCard from '../../models/producto/producto-card';
import ProductoResponse from '../../models/producto/producto-response';

@Component({
  selector: 'app-producto-card-component',
  imports: [],
  templateUrl: './producto-card-component.html'
})
export class ProductoCardComponent {
  producto = input<ProductoCard>()
}
