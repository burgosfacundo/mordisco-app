import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { Subscription, timer } from 'rxjs';
import { exhaustMap, takeUntil } from 'rxjs/operators';
import { PedidoService } from '../../../../shared/services/pedido/pedido-service';
import { PagoService } from '../../../../shared/services/pagos/pago-service';
import PedidoResponse from '../../../../shared/models/pedido/pedido-response';
import PagoResponseDTO from '../../../../shared/models/pago/pago-response-dto';

@Component({
  selector: 'app-pago-pendiente-page',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './pago-pendiente-page.html'
})
export class PagoPendientePage implements OnInit, OnDestroy {
  private readonly PAYMENT_POLL_INTERVAL_MS = 5000;
  private readonly PAYMENT_POLL_TIMEOUT_MS = 120000;
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private pedidoService = inject(PedidoService);
  private pagoService = inject(PagoService);
  private pagoPollingSubscription?: Subscription;

  pedido = signal<PedidoResponse | null>(null);
  isLoading = signal(true);

  ngOnInit(): void {
    this.detenerPollingPago();

    const pedidoId = this.route.snapshot.queryParamMap.get('pedido');
    if (!pedidoId) {
      this.router.navigate(['/cliente/pedidos']);
      return;
    }

    const id = Number(pedidoId);
    this.pagoService.getPagoByPedidoId(id).subscribe({
      next: pago => this.manejarEstadoPago(pago, id, true),
      error: () => this.manejarErrorCarga()
    });
  }

  ngOnDestroy(): void {
    this.detenerPollingPago();
  }

  private manejarEstadoPago(pago: PagoResponseDTO, pedidoId: number, inicial: boolean): void {
    if (pago.estado === 'APROBADO') {
      this.detenerPollingPago();
      this.router.navigate(['/cliente/pedidos/pago-exitoso'], { queryParams: { pedido: pedidoId } });
      return;
    }

    if (pago.estado !== 'PENDIENTE') {
      this.detenerPollingPago();
      this.router.navigate(['/cliente/pedidos/pago-fallido'], { queryParams: { pedido: pedidoId } });
      return;
    }

    if (inicial) {
      this.cargarPedido(pedidoId);
    }
  }

  private cargarPedido(pedidoId: number): void {
    this.pedidoService.getById(pedidoId).subscribe({
      next: pedido => {
        this.pedido.set(pedido);
        this.isLoading.set(false);
        this.iniciarPollingPago(pedidoId);
      },
      error: () => this.manejarErrorCarga()
    });
  }

  private iniciarPollingPago(pedidoId: number): void {
    this.detenerPollingPago();

    this.pagoPollingSubscription = timer(
      this.PAYMENT_POLL_INTERVAL_MS,
      this.PAYMENT_POLL_INTERVAL_MS
    )
      .pipe(
        exhaustMap(() => this.pagoService.getPagoByPedidoId(pedidoId)),
        takeUntil(timer(this.PAYMENT_POLL_TIMEOUT_MS))
      )
      .subscribe({
        next: pago => this.manejarEstadoPago(pago, pedidoId, false),
        error: () => this.manejarErrorCarga()
      });
  }

  private detenerPollingPago(): void {
    this.pagoPollingSubscription?.unsubscribe();
    this.pagoPollingSubscription = undefined;
  }

  private manejarErrorCarga(): void {
    this.detenerPollingPago();
    this.isLoading.set(false);
    this.router.navigate(['/cliente/pedidos']);
  }

  irAMisPedidos(): void {
    this.router.navigate(['/cliente/pedidos']);
  }

  irAlInicio(): void {
    this.router.navigate(['/home']);
  }
}
