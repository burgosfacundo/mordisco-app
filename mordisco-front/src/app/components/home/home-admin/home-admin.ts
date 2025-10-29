import { Component, inject, OnInit } from '@angular/core';
import { UsuarioCard } from "../../usuario-card/usuario-card";
import { RestauranteCard } from "../../restaurante-card/restaurante-card";
import { PedidoCard } from "../../pedido-card/pedido-card";
import { MatSnackBar } from '@angular/material/snack-bar';
import { RestauranteService } from '../../../services/restaurante/restaurante-service';
import { PedidoService } from '../../../services/pedido/pedido-service';
import { UserService } from '../../../features/registro/services/user-service';
import RestauranteForCard from '../../../models/restaurante/restaurante-for-card';
import Pedido from '../../../models/pedido/pedido';
import UserCard from '../../../models/user/user-card';
import { MatPaginator, PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'app-home-admin',
  imports: [UsuarioCard, RestauranteCard, PedidoCard,MatPaginator],
  templateUrl: './home-admin.html',
  styleUrl: './home-admin.css'
})
export class HomeAdmin implements OnInit {
  private _snackBar = inject(MatSnackBar);
  private restauranteService = inject(RestauranteService);
  private pedidoService = inject(PedidoService);
  private usuarioService = inject(UserService);

  protected restaurantes? : RestauranteForCard[];
  protected pedidos? : Pedido[];
  protected usuarios? : UserCard[];

  sizeUsuarios : number = 10;
  pageUsuarios : number = 0;
  lengthUsuarios : number = 10;

  sizePedidos : number = 10;
  pagePedidos : number = 0;
  lengthPedidos : number = 10;

  sizeRestaurantes : number = 10;
  pageRestaurantes : number = 0;
  lengthRestaurantes : number = 10;

  sizeOptions : number[] = [5,10,20];

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
        this.restaurantes = data.content;
        this.lengthRestaurantes = data.totalElements
        this.isLoadingRestaurantes = false;
      },
      error: () => {
        this._snackBar.open('Error al cargar los restaurantes', 'Cerrar', { duration: 3000 });
        this.isLoadingRestaurantes = false;
      }
    });
  }


  loadPedidos(): void {
    this.pedidoService.getAll(this.pagePedidos,this.sizePedidos).subscribe({
      next: (data) => {
        this.pedidos = data.content;
        this.lengthPedidos = data.totalElements
        this.isLoadingPedidos = false;
      },
      error: () => {
        this._snackBar.open('Error al cargar los pedidos', 'Cerrar', { duration: 3000 });
        this.isLoadingPedidos = false;
      }
    });
  }


  loadUsuarios(): void {
    this.usuarioService.getAll(this.pageUsuarios,this.sizeUsuarios).subscribe({
      next: (data) => {
        this.usuarios = data.content;
        this.lengthUsuarios = data.totalElements
        this.isLoadingUsuarios = false;
      },
      error: () => {
        this._snackBar.open('Error al cargar los usuarios', 'Cerrar', { duration: 3000 });
        this.isLoadingUsuarios = false;
      }
    });
  }

  onPageChangePedidos(event: PageEvent): void {
    this.pagePedidos = event.pageIndex
    this.sizePedidos = event.pageSize;
    this.loadPedidos();
  }

    onPageChangeRestaurantes(event: PageEvent): void {
    this.pageRestaurantes = event.pageIndex
    this.sizeRestaurantes = event.pageSize;
    this.loadRestaurantes();
  }


    onPageChangeUsuarios(event: PageEvent): void {
    this.pageUsuarios = event.pageIndex
    this.sizeUsuarios = event.pageSize;
    this.loadUsuarios();
  }
}
