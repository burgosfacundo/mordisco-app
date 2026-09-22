import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { PedidoService } from '../../../../shared/services/pedido/pedido-service';
import { PagoService } from '../../../../shared/services/pagos/pago-service';
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
  private pagoService = inject(PagoService)

  pedido = signal<PedidoResponse | null>(null)
  isLoading = signal(true)

  ngOnInit(): void {
    const pedidoId = this.route.snapshot.queryParamMap.get('pedido')
    
    if (!pedidoId) {
      this.router.navigate(['/cliente/pedidos'])
      return
    }

    const id = Number(pedidoId);
    this.pagoService.getPagoByPedidoId(id).subscribe({
      next: pago => {
        if (pago.estado === 'PENDIENTE') {
          this.router.navigate(['/cliente/pedidos/pago-pendiente'], { queryParams: { pedido: id } });
          return;
        }
        if (pago.estado !== 'APROBADO') {
          this.router.navigate(['/cliente/pedidos/pago-fallido'], { queryParams: { pedido: id } });
          return;
        }
        this.cargarPedido(id);
      },
      // A redirect is not proof of payment. Keep the user on the pending route
      // when the authoritative status cannot be read yet.
      error: () => this.router.navigate(['/cliente/pedidos/pago-pendiente'], { queryParams: { pedido: id } })
    });
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