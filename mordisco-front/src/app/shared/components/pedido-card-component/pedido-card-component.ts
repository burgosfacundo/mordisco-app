import { Component, Input } from '@angular/core';
import Pedido from '../../../models/pedido/pedido';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-pedido-card-component',
  imports: [RouterLink],
  templateUrl: './pedido-card-component.html',
  styleUrl: './pedido-card-component.css'
})
export class PedidoCardComponent {
  @Input() pedido? : Pedido
}
