import { Component, Input } from '@angular/core';
import { RouterLink } from "@angular/router";
import PedidoResponse from '../../models/pedido/pedido-response';

@Component({
  selector: 'app-pedido-card-component',
  imports: [RouterLink],
  templateUrl: './pedido-card-component.html',
  styleUrl: './pedido-card-component.css'
})
export class PedidoCardComponent {
  @Input() pedido? : PedidoResponse
}
