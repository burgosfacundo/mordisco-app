import { Component, OnInit } from '@angular/core';
import Pedido from '../../models/pedido';
import { ActivatedRoute } from '@angular/router';
import { PedidoService } from '../../services/pedidoService/pedido-service';
import { PedidoCard } from '../../components/pedido-card/pedido-card';
import { UsuarioCard } from "../../components/usuario-card/usuario-card";
import User from '../../models/user';

@Component({
  selector: 'app-home',
  imports: [PedidoCard, UsuarioCard],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit{

  pedidosPendientes? : Pedido[]
  estadoDefault : string = "PENDIENTE"
  arrayUsers? : User[]

  constructor(public pSerice : PedidoService,private aRoute: ActivatedRoute){}

  ngOnInit(): void {
      const idRestaurante = 1 /*this.aRoute.snapshot.params['id']*/
      this.pSerice.getAllByRestaurante_IdAndEstado(idRestaurante, this.estadoDefault).subscribe({
        next: (pedidosLeidos) => this.pedidosPendientes = pedidosLeidos
      })
  }

}
