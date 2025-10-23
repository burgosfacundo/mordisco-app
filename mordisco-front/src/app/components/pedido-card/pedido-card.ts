import { Component, Input } from '@angular/core';
import Pedido from '../../models/pedido';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-pedido-card',
  imports: [RouterLink],
  templateUrl: './pedido-card.html',
  styleUrl: './pedido-card.css'
})
export class PedidoCard {

  @Input() pedido? : Pedido
}
