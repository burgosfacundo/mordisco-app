import { Component, inject, OnInit } from '@angular/core';
import { PedidoService } from '../../../../shared/services/pedido/pedido-service';
import { UserService } from '../../../registro/services/user-service';
import UserCard from '../../../../shared/models/user/user-card';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { UsuarioCardComponent } from '../../../../shared/components/usuario-card-component/usuario-card-component';
import { RestauranteCardComponent } from '../../../../shared/components/restaurante-card-component/restaurante-card-component';
import { PedidoCardComponent } from '../../../../shared/components/pedido-card-component/pedido-card-component';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import RestauranteForCard from '../../../../shared/models/restaurante/restaurante-for-card';
import PedidoResponse from '../../../../shared/models/pedido/pedido-response';

@Component({
  selector: 'app-home-admin-component',
  imports: [RestauranteCardComponent, UsuarioCardComponent, PedidoCardComponent, MatPaginator],
  templateUrl: './home-admin-component.html'
})
export class HomeAdminComponent implements OnInit {
  private restauranteService = inject(RestauranteService);
  private pedidoService = inject(PedidoService);
  private usuarioService = inject(UserService);

  sizeUsuarios : number = 5;
  pageUsuarios : number = 0;
  lengthUsuarios : number = 5;

  sizePedidos : number = 5;
  pagePedidos : number = 0;
  lengthPedidos : number = 5;

  sizeRestaurantes : number = 5;
  pageRestaurantes : number = 0;
  lengthRestaurantes : number = 5;

  isLoadingRestaurantes = true;
  isLoadingPedidos = true;
  isLoadingUsuarios = true;

  ngOnInit(): void {
    this.loadInfo()
  }


  loadInfo(): void {
    this.loadRestaurantes()
    this.loadPedidos()
    this.loadUsuarios()
  }


  loadRestaurantes(): void {
    
    this.restauranteService.getAll(this.pageRestaurantes,this.sizeRestaurantes).subscribe({
      next: (data) => {
        this.lengthRestaurantes = data.totalElements
        this.isLoadingRestaurantes = false;
      },
      error: () => {
        this.isLoadingRestaurantes = false;
      }
    });
  }


  loadPedidos(): void {
    this.pedidoService.getAll(this.pagePedidos,this.sizePedidos).subscribe({
      next: (data) => {
        this.lengthPedidos = data.totalElements
        this.isLoadingPedidos = false;
      },
      error: () => {
        this.isLoadingPedidos = false;
      }
    });
  }


  loadUsuarios(): void {
    this.usuarioService.getAll(this.pageUsuarios,this.sizeUsuarios).subscribe({
      next: (data) => {        
        this.lengthUsuarios = data.totalElements
        this.isLoadingUsuarios = false;
      },
      error: () => {
        this.isLoadingUsuarios = false;
      }
    });
  }


}
