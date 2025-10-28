import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PedidoCard } from "../../pedido-card/pedido-card";
import { PedidoService } from '../../../services/pedido/pedido-service';
import { AuthService } from '../../../auth/services/auth-service';
import { RestauranteService } from '../../../services/restaurante/restaurante-service';
import { MatSnackBar } from '@angular/material/snack-bar';
import Pedido from '../../../models/pedido/pedido';
import Restaurante from '../../../models/restaurante/restaurante';
import { MatPaginator, PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'app-home-restaurante',
  standalone: true,
  imports: [CommonModule, PedidoCard,MatPaginator],
  templateUrl: './home-restaurante.html',
  styleUrl: './home-restaurante.css'
})
export class HomeRestaurante implements OnInit {
  private _snackBar = inject(MatSnackBar);
  private pedidoService = inject(PedidoService);
  private authService = inject(AuthService);
  private restauranteService = inject(RestauranteService);

  pedidosPendientes?: Pedido[];

  sizePedidos : number = 10;
  pagePedidos : number = 0;
  length : number = 10;
  sizeOptions : number[] = [5,10,20];
  
  isLoading = true;

  restaurante?: Restaurante;

  ngOnInit(): void {
    this.loadRestauranteData();
  }

  private loadRestauranteData(): void {
    const user = this.authService.currentUser();
    
    if (!user?.userId) {
      this._snackBar.open('❌ No se encontró información del usuario', 'Cerrar' , { duration: 3000 });
      this.authService.logout();
      return;
    }

    this.restauranteService.getByUsuario(user.userId).subscribe({
      next: (r) => {
        this.restaurante = r;
        this.loadPedidosPendientes(r.id);
      },
      error: () => {
        this._snackBar.open('❌ Error al cargar el restaurante', 'Cerrar' , { duration: 3000 });
        this.isLoading = false;
      }
    });
  }

  private loadPedidosPendientes(restauranteId: number): void {
    this.pedidoService.getAllByRestaurante_IdAndEstado(restauranteId, 'PENDIENTE',this.pagePedidos,this.sizePedidos)
      .subscribe({
        next: (data) => {
          this.pedidosPendientes = data.content;
          this.length = data.totalElements;
          this.isLoading = false;
        },
        error: () => {
          this._snackBar.open('❌ Error al cargar los pedidos pendientes', 'Cerrar' , { duration: 3000 });
          this.isLoading = false;
        }
      });
  }


    onPageChangePedidos(event: PageEvent): void {
    this.pagePedidos = event.pageIndex
    this.sizePedidos = event.pageSize;
    this.loadRestauranteData();
  }
}