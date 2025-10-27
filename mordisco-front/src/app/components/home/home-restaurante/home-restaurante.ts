import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PedidoCard } from "../../pedido-card/pedido-card";
import { PedidoService } from '../../../services/pedido/pedido-service';
import { AuthService } from '../../../auth/services/auth-service';
import { RestauranteService } from '../../../services/restaurante/restaurante-service';
import { MatSnackBar } from '@angular/material/snack-bar';
import Pedido from '../../../models/pedido/pedido';
import Restaurante from '../../../models/restaurante/restaurante';

@Component({
  selector: 'app-home-restaurante',
  standalone: true,
  imports: [CommonModule, PedidoCard],
  templateUrl: './home-restaurante.html',
  styleUrl: './home-restaurante.css'
})
export class HomeRestaurante implements OnInit {
  private _snackBar = inject(MatSnackBar);
  private pedidoService = inject(PedidoService);
  private authService = inject(AuthService);
  private restauranteService = inject(RestauranteService);

  pedidosPendientes?: Pedido[];
  restaurante?: Restaurante;
  isLoading = true;

  ngOnInit(): void {
    this.loadRestauranteData();
  }

  private loadRestauranteData(): void {
    const user = this.authService.currentUser();
    
    if (!user?.userId) {
      this.openSnackBar('❌ No se encontró información del usuario');
      this.authService.logout();
      return;
    }

    this.restauranteService.getByUsuario(user.userId).subscribe({
      next: (restaurante) => {
        this.restaurante = restaurante;
        this.loadPedidosPendientes(restaurante.id);
      },
      error: (e) => {
        console.error('Error al cargar restaurante:', e);
        this.openSnackBar('❌ Error al cargar el restaurante');
        this.isLoading = false;
      }
    });
  }

  private loadPedidosPendientes(restauranteId: number): void {
    this.pedidoService.getAllByRestaurante_IdAndEstado(restauranteId, 'PENDIENTE')
      .subscribe({
        next: (pedidos) => {
          this.pedidosPendientes = pedidos;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error al cargar pedidos:', err);
          this.openSnackBar('❌ Error al cargar los pedidos pendientes');
          this.isLoading = false;
        }
      });
  }

  private openSnackBar(message: string, action: string = 'Cerrar'): void {
    this._snackBar.open(message, action, { duration: 3000 });
  }
}