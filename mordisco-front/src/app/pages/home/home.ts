import { Component, inject, OnInit } from '@angular/core';
import Pedido from '../../models/pedido';
import { ActivatedRoute } from '@angular/router';
import { PedidoService } from '../../services/pedidoService/pedido-service';
import { PedidoCard } from '../../components/pedido-card/pedido-card';
import { AuthService } from '../../auth/services/auth-service';
import { JwtUser } from '../../auth/models/jwt-user';

@Component({
  selector: 'app-home',
  imports: [PedidoCard],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit{

  pedidosPendientes? : Pedido[]
  estadoDefault : string = "PENDIENTE"
  jwt?: JwtUser | null
  pService : PedidoService = inject(PedidoService)
  auService : AuthService = inject(AuthService)

  ngOnInit(): void {
      this.jwt= this.auService.getCurrentUser()
      const idRestaurante = this.jwt?.id
      if(idRestaurante){
        this.pService.getAllByRestaurante_IdAndEstado(idRestaurante, this.estadoDefault).subscribe({
        next: (pedidosLeidos) => this.pedidosPendientes = pedidosLeidos
      })
      }

  }

}
