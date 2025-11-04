import { Component, Input } from '@angular/core';
import ProductoCard from '../../models/producto/producto-card';

@Component({
  selector: 'app-producto-card-component',
  imports: [],
  templateUrl: './producto-card-component.html',
  styleUrl: './producto-card-component.css'
})
export class ProductoCardComponent {
@Input() producto? : ProductoCard
}
