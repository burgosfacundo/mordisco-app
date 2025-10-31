import { Component, Input } from '@angular/core';
import RestauranteForCard from '../../models/restaurante/restaurante-for-card';


@Component({
  selector: 'app-restaurante-card-component',
  imports: [],
  templateUrl: './restaurante-card-component.html',
  styleUrl: './restaurante-card-component.css'
})
export class RestauranteCardComponent {
  @Input() restaurante! : RestauranteForCard;
}
