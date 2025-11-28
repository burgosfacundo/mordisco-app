import { Component, inject, input, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { PedidoService } from '../../../../shared/services/pedido/pedido-service';
import { AuthService } from '../../../../shared/services/auth-service';
import { DetallePedidoComponent } from '../../../mis-pedidos/components/detalle-pedido-component/detalle-pedido-component';
import PedidoResponse from '../../../../shared/models/pedido/pedido-response';
import { EstadoPedido } from '../../../../shared/models/enums/estado-pedido';
import { ToastService } from '../../../../core/services/toast-service';
import { ConfirmationService } from '../../../../core/services/confirmation-service';
import { PromptService } from '../../../../core/services/confirmation-prompt-service';
import { PromptDialogComponent } from '../../../../shared/components/confirmation-dialog/confirmation-prompt-component/prompt-dialog-component';

@Component({
  selector: 'app-mis-pedidos-cliente-page',
  standalone: true,
  imports: [
    CommonModule,
    MatPaginatorModule,
    MatTabsModule,
    MatIconModule,
    DetallePedidoComponent,
],
  templateUrl: './mis-pedidos-cliente-page.html',
  styleUrl: './mis-pedidos-cliente-page.css'
})
export class MisPedidosClientePage implements OnInit {
  private pedidoService = inject(PedidoService)
  private authService = inject(AuthService)
  private toastService = inject(ToastService)
  private promptService = inject(PromptService)
  private router = inject(Router)

  pedidos = signal<PedidoResponse[]>([])
  isLoading = signal(true)
  adminMode = input<boolean>(false)
  admin_idUser = input<number>()

  // Paginación
  page = 0
  size = 10
  totalElements = 0

  // Filtro por tab
  estadoActual: EstadoPedido | 'TODOS' = 'TODOS'

  ngOnInit(): void {
    this.cargarPedidos()
  }

  private cargarPedidos(): void {
    let userId : number | undefined
    if(!this.adminMode()){
      userId= this.authService.currentUser()?.userId
    }else{
      userId=this.admin_idUser()
    }

    if (!userId) {
      this.router.navigate(['/login'])
      return
    }

    this.isLoading.set(true)

    if (this.estadoActual === 'TODOS') {
      this.pedidoService.getAllByCliente(userId, this.page, this.size).subscribe({
        next: (response) => {
          this.pedidos.set(response.content)
          this.totalElements = response.totalElements
          this.isLoading.set(false)
        },
        error: () => {
          this.isLoading.set(false)
        }
      });
    } else {
      this.pedidoService.getAllByClienteYEstado(userId, this.estadoActual, this.page, this.size).subscribe({
        next: (response) => {
          this.pedidos.set(response.content)
          this.totalElements = response.totalElements
          this.isLoading.set(false)
        },
        error: () => {
          this.isLoading.set(false)
        }
      });
    }
  }

  onTabChange(index: number): void {
    const estados: (EstadoPedido | 'TODOS')[] = [
      'TODOS',
      EstadoPedido.PENDIENTE,
      EstadoPedido.EN_PREPARACION,
      EstadoPedido.LISTO_PARA_RETIRAR,
      EstadoPedido.EN_CAMINO,
      EstadoPedido.COMPLETADO,
      EstadoPedido.CANCELADO
    ]

    this.estadoActual = estados[index]
    this.page = 0
    this.cargarPedidos()
  }

  onPageChange(event: PageEvent): void {
    this.page = event.pageIndex
    this.size = event.pageSize
    this.cargarPedidos()
  }

  verDetalle(pedidoId: number): void {
    if(this.adminMode()){
      this.router.navigate(['/admin/pedidos/detalle', pedidoId])
    }else{
      this.router.navigate(['/cliente/pedidos/detalle', pedidoId])
    }
  }

  cancelarPedido(pedidoId: number): void {
    this.promptService.show({
      title: 'Cancelar Pedido',
      message: 'Indica el motivo de la cancelación',
      placeholder: 'Ej: Cliente solicitó cancelación',
      required: true,
      confirmText: 'Cancelar pedido',
      type: 'danger'
    }).subscribe(result => {
      if (!result.confirmed) return;

      this.pedidoService.cancel(pedidoId, result.value).subscribe({
        next: () => {
          this.toastService.success('✅ Pedido cancelado');
          this.cargarPedidos();
        }
      });
    });
  }
  
  navegarAHome() {
    this.router.navigate(['/home'])
  }
}