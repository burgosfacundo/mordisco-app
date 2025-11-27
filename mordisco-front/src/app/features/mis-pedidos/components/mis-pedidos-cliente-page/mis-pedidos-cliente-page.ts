import { Component, inject, input, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { PedidoService } from '../../../../shared/services/pedido/pedido-service';
import { AuthService } from '../../../../shared/services/auth-service';
import { DetallePedidoComponent } from '../../../mis-pedidos/components/detalle-pedido-component/detalle-pedido-component';
import PedidoResponse from '../../../../shared/models/pedido/pedido-response';
import { EstadoPedido } from '../../../../shared/models/enums/estado-pedido';

@Component({
  selector: 'app-mis-pedidos-cliente-page',
  standalone: true,
  imports: [
    CommonModule,
    MatPaginatorModule,
    MatTabsModule,
    MatIconModule,
    DetallePedidoComponent
],
  templateUrl: './mis-pedidos-cliente-page.html',
  styleUrl: './mis-pedidos-cliente-page.css'
})
export class MisPedidosClientePage implements OnInit {
  private pedidoService = inject(PedidoService)
  private authService = inject(AuthService)
  private snackBar = inject(MatSnackBar)
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
      this.snackBar.open('Error: Usuario no autenticado', 'Cerrar', { duration: 3000 })
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
        error: (error) => {
          console.error('Error al cargar pedidos:', error)
          this.snackBar.open('Error al cargar pedidos', 'Cerrar', { duration: 3000 })
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
        error: (error) => {
          console.error('Error al cargar pedidos:', error)
          this.snackBar.open('Error al cargar pedidos', 'Cerrar', { duration: 3000 })
          this.isLoading.set(false)
        }
      });
    }
  }

  onTabChange(index: number): void {
    const estados: (EstadoPedido | 'TODOS')[] = [
      'TODOS',
      EstadoPedido.PENDIENTE,
      EstadoPedido.EN_PROCESO,
      EstadoPedido.EN_CAMINO,
      EstadoPedido.RECIBIDO,
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
    this.router.navigate(['/cliente/pedidos/detalle', pedidoId])
  }

  cancelarPedido(pedidoId: number): void {
    if (!confirm('¿Estás seguro de cancelar este pedido?')) return

    this.pedidoService.cancel(pedidoId).subscribe({
      next: () => {
        this.snackBar.open('✅ Pedido cancelado', 'Cerrar', { duration: 3000 })
        this.cargarPedidos()
      },
      error: (error) => {
        console.error('Error al cancelar pedido:', error)
        this.snackBar.open(
          error.error?.message || 'No se pudo cancelar el pedido',
          'Cerrar',
          { duration: 4000 }
        )
      }
    })
  }
  
  navegarAHome() {
    this.router.navigate(['/home'])
  }
}