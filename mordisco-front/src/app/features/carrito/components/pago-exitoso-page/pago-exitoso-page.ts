import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { PedidoService } from '../../../../shared/services/pedido/pedido-service';
import PedidoResponse from '../../../../shared/models/pedido/pedido-response';

@Component({
  selector: 'app-pago-exitoso-page',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './pago-exitoso-page.html'
})
export class PagoExitosoPage implements OnInit {
  private route = inject(ActivatedRoute)
  private router = inject(Router)
  private pedidoService = inject(PedidoService)

  pedido = signal<PedidoResponse | null>(null)
  isLoading = signal(true)

  ngOnInit(): void {
    const pedidoId = this.route.snapshot.queryParamMap.get('pedido')
    
    if (!pedidoId) {
      this.router.navigate(['/cliente/pedidos'])
      return
    }

    this.cargarPedido(Number(pedidoId));
  }

  private cargarPedido(pedidoId: number): void {
    this.pedidoService.getById(pedidoId).subscribe({
      next: (pedido) => {
        this.pedido.set(pedido)
        this.isLoading.set(false)
      },
      error: () => {
        this.isLoading.set(false)
        this.router.navigate(['/cliente/pedidos'])
      }
    });
  }

  irAMisPedidos(): void {
    this.router.navigate(['/cliente/pedidos'])
  }

  irAlInicio(): void {
    this.router.navigate(['/home'])
  }
}