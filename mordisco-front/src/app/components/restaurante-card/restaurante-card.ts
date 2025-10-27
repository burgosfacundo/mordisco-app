import { Component, Input } from '@angular/core';
import RestauranteForCard from '../../models/restaurante/restaurante-for-card';

@Component({
  selector: 'app-restaurante-card',
  imports: [],
  templateUrl: './restaurante-card.html',
  styleUrl: './restaurante-card.css'
})
export class RestauranteCard {
  @Input() restaurante! : RestauranteForCard;
}
