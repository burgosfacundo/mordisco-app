import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PedidoService } from '../../../../shared/services/pedido/pedido-service';
import { UserService } from '../../../registro/services/user-service';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';


@Component({
  selector: 'app-home-admin-component',
  imports: [RouterLink],
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
