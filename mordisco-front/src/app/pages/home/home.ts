import { Component, inject, OnInit } from '@angular/core';
import Pedido from '../../models/pedido/pedido';
import { PedidoService } from '../../services/pedido/pedido-service';
import { PedidoCard } from '../../components/pedido-card/pedido-card';
import { AuthService } from '../../auth/services/auth-service';
import { RestauranteService } from '../../services/restaurante/restaurante-service';
import Restaurante from '../../models/restaurante/restaurante';


@Component({
  selector: 'app-home',
  imports: [PedidoCard],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit{

  pedidosPendientes? : Pedido[]
  estadoDefault : string = "PENDIENTE"
  restaurante? : Restaurante
  pedidoService : PedidoService = inject(PedidoService)
  authService : AuthService = inject(AuthService)
  restauranteService : RestauranteService = inject(RestauranteService)

  ngOnInit(): void {
      const id = this.authService.currentUserValue?.id

      if(id){
        this.restauranteService.getByUsuario(id).subscribe({
          next: r => this.restaurante = r,
          error: e => console.error(e)
        })

        if(this.restaurante){
          this.pedidoService.getAllByRestaurante_IdAndEstado(this.restaurante?.id, this.estadoDefault).subscribe({
            next: (pedidosLeidos) => this.pedidosPendientes = pedidosLeidos,
            error: e => console.error(e)
            })
        }
      }    
  }
}
