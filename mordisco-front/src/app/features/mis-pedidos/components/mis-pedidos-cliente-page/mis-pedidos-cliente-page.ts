import { Component, inject, input, OnInit, signal, ViewChild } from '@angular/core';
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
import { BarraBuscadoraComponent } from '../../../../shared/components/barra-buscadora-component/barra-buscadora-component';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-mis-pedidos-cliente-page',
  standalone: true,
  imports: [
    CommonModule,
    MatPaginatorModule,
    MatTabsModule,
    MatIconModule,
    DetallePedidoComponent,
    BarraBuscadoraComponent,
    FormsModule
],
  templateUrl: './mis-pedidos-cliente-page.html',
  styleUrl: './mis-pedidos-cliente-page.css'
})
export class MisPedidosClientePage implements OnInit {
  private pedidoService = inject(PedidoService)
  private authService = inject(AuthService)
  private toastService = inject(ToastService)
  private confirmationService = inject(ConfirmationService)
  private router = inject(Router)

  private searchSubject = new Subject<string>();
  @ViewChild(BarraBuscadoraComponent) barraBuscadora!: BarraBuscadoraComponent;

  filtroTipoEntrega: string = '';
  filtroFechaInicio: string = '';
  filtroFechaFin: string = '';
  searchValue: string = '';

  pedidos = signal<PedidoResponse[]>([])
  isLoading = signal(true)
  adminMode = input<boolean>(false)
  admin_idUser = input<number>()
  idUsuario? : number

  // Paginación
  page = 0
  size = 10
  totalElements = 0

  // Filtro por tab
  estadoActual: EstadoPedido | 'TODOS' = 'TODOS'

  ngOnInit(): void {
    this.cargarPedidos()
     this.searchSubject.pipe(
          debounceTime(500), // Espera 500ms después de que el usuario deje de escribir
          distinctUntilChanged() // Solo emite si el valor cambió
        ).subscribe(() => {
          this.page = 0; // Resetear a la primera página
          this.buscar();
        }); 
  }

  private cargarPedidos(): void {
    if(!this.adminMode()){
      this.idUsuario= this.authService.currentUser()?.userId
    }else{
      this.idUsuario=this.admin_idUser()
    }

    if (!this.idUsuario) {
      this.router.navigate(['/login'])
      return
    }

    this.isLoading.set(true)

    if (this.estadoActual === 'TODOS') {
      this.pedidoService.getAllByCliente(this.idUsuario, this.page, this.size).subscribe({
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
      this.pedidoService.getAllByClienteYEstado(this.idUsuario, this.estadoActual, this.page, this.size).subscribe({
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

    if (this.searchValue.trim() !== '' || this.tieneFiltrosAplicados()) {
      this.buscar();
    } else {
      this.cargarPedidos();
    } 
  }

  onPageChangePedidos(event: PageEvent): void {
    this.page = event.pageIndex;
    this.size = event.pageSize;

    if (this.searchValue.trim() !== '' || this.tieneFiltrosAplicados()) {
      this.buscar();
    } else {
      this.cargarPedidos();
    }
  }

  verDetalle(pedidoId: number): void {
    if(this.adminMode()){
      this.router.navigate(['/admin/pedidos/detalle', pedidoId])
    }else{
      this.router.navigate(['/cliente/pedidos/detalle', pedidoId])
    }
  }

  cancelarPedido(pedidoId: number){
    this.confirmationService.confirm({
      title: 'Cancelar Pedido',
          message: '¿Estás seguro que queres cancelar el pedido? Esta accion no tiene como deshacerse',
          confirmText: 'Aceptar',
          type: 'danger'
        }).subscribe(confirmed => {
          if (!confirmed) return;
          this.pedidoService.cancel(pedidoId).subscribe({
                  next: () => {
                    this.toastService.success('✅ Pedido cancelado');
                    this.cargarPedidos();
                  }
          });
        });

  }
   aplicarFiltros(): void {
    this.page = 0;
    this.buscar();
  }

  private tieneFiltrosAplicados(): boolean {
    return this.filtroTipoEntrega !== '' ||
           this.filtroFechaInicio !== '' ||
           this.filtroFechaFin !== '';
  }

  buscar() {
      if (!this.idUsuario) return; // ← Agregar validación

    // Convertir fechas de tipo date a LocalDateTime con hora 00:00:00
    const fechaInicioFormatted = this.filtroFechaInicio 
      ? `${this.filtroFechaInicio}T00:00:00` 
      : '';
    
    const fechaFinFormatted = this.filtroFechaFin 
      ? `${this.filtroFechaFin}T23:59:59` 
      : '';
    const estadoParaBuscar = this.estadoActual === 'TODOS' 
      ? '' 
      : this.estadoActual;  
  

      this.pedidoService.filtrarPedidosClientes(
      this.idUsuario!,
      this.searchValue,
      estadoParaBuscar,
      this.filtroTipoEntrega,
      fechaInicioFormatted,
      fechaFinFormatted,
      this.page, 
      this.size
    ).subscribe(resp => {
      this.pedidos.set(resp.content)
      this.totalElements = resp.totalElements;
    });
  }


 onSearchChanged(text: string) {
    this.searchValue = text;
    
    // Si hay texto o hay filtros aplicados, buscar
    if (text.trim() !== '' || this.tieneFiltrosAplicados()) {
      this.searchSubject.next(text);
    } else if (text.trim() === '' && !this.tieneFiltrosAplicados()) {
      // Si borraron todo y no hay filtros, cargar todos
      this.page = 0;
      this.cargarPedidos();
    }
  }


  onClearFilters(): void {
    this.searchValue = '';
    this.filtroTipoEntrega = '';
    this.filtroFechaInicio = '';
    this.filtroFechaFin = '';
    this.page = 0;
    
    // Limpiar la barra buscadora visualmente
    if (this.barraBuscadora) {
      this.barraBuscadora.onSearchClear();
    }
    
    this.cargarPedidos();
  }

  navegarAHome() {
    this.router.navigate(['/home'])
  }
}