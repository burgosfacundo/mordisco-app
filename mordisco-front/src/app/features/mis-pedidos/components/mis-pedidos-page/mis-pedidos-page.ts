import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { PedidoCardComponent } from '../../../../shared/components/pedido-card-component/pedido-card-component';
import { PedidoService } from '../../../../shared/services/pedido/pedido-service';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import { AuthService } from '../../../../shared/services/auth-service';
import PedidoResponse from '../../../../shared/models/pedido/pedido-response';
import RestauranteResponse from '../../../../shared/models/restaurante/restaurante-response';
import { EstadoPedido } from '../../../../shared/models/enums/estado-pedido';
import { ConfirmationService } from '../../../../core/services/confirmation-service';
import { ToastService } from '../../../../core/services/toast-service';
import { BarraBuscadoraComponent } from '../../../../shared/components/barra-buscadora-component/barra-buscadora-component';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { PagoService } from '../../../../shared/services/pagos/pago-service';

@Component({
  selector: 'app-mis-pedidos-page',
  standalone: true,
  imports: [
    CommonModule,
    PedidoCardComponent,
    MatPaginatorModule,
    MatTabsModule,
    MatIconModule,
    BarraBuscadoraComponent,
    FormsModule
  ],
  templateUrl: './mis-pedidos-page.html',
  styleUrl: './mis-pedidos-page.css'
})
export class MisPedidosPage implements OnInit {
  private pService = inject(PedidoService);
  private rService = inject(RestauranteService);
  private auS = inject(AuthService);
  private router = inject(Router);
  private confirmationService = inject(ConfirmationService);
  private toastService = inject(ToastService);

  private searchSubject = new Subject<string>();
  @ViewChild(BarraBuscadoraComponent) barraBuscadora!: BarraBuscadoraComponent;

  filtroTipoEntrega: string = '';
  filtroFechaInicio: string = '';
  filtroFechaFin: string = '';
  searchValue: string = '';
  idUsuario? : number
  arrPedidos?: PedidoResponse[];
  restaurante?: RestauranteResponse;
  

  sizePedidos = 10;
  pagePedidos = 0;
  lengthPedidos = 0;
  isLoadingPedidos = true;

  selectedTabIndex = 0;
  estadoActual: EstadoPedido | 'TODOS' = 'TODOS';

  ngOnInit(): void {
    this.cargarRestaurante();
    this.searchSubject.pipe(
      debounceTime(500), // Espera 500ms después de que el usuario deje de escribir
      distinctUntilChanged() // Solo emite si el valor cambió
    ).subscribe(() => {
        this.pagePedidos = 0; // Resetear a la primera página
        this.buscar();
    });  
  }

  private cargarRestaurante(): void {
    this.idUsuario = this.auS.getCurrentUser()?.userId;

    if (!this.idUsuario) {
      this.router.navigate(['/login']);
      return;
    }

    this.rService.getByUsuario(this.idUsuario).subscribe({
      next: (data) => {
        this.restaurante = data;
        this.cargarPedidos();
      },
      error: () => {
        this.isLoadingPedidos = false;
      }
    });
  }

  private cargarPedidos(): void {
    if (!this.restaurante) return;

    this.isLoadingPedidos = true;

    if (this.estadoActual === 'TODOS') {
      this.pService.findAllByRestaurante_Id(
        this.restaurante.id,
        this.pagePedidos,
        this.sizePedidos
      ).subscribe({
        next: (response) => {
          this.arrPedidos = response.content;
          this.lengthPedidos = response.totalElements;
          this.isLoadingPedidos = false;
        },
        error: () => {
          this.isLoadingPedidos = false;
        }
      });
    } else {
      this.pService.getAllByRestaurante_IdAndEstado(
        this.restaurante.id,
        this.estadoActual,
        this.pagePedidos,
        this.sizePedidos
      ).subscribe({
        next: (response) => {
          this.arrPedidos = response.content;
          this.lengthPedidos = response.totalElements;
          this.isLoadingPedidos = false;
        },
        error: () => {
          this.isLoadingPedidos = false;
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
      EstadoPedido.LISTO_PARA_ENTREGAR,
      EstadoPedido.ASIGNADO_A_REPARTIDOR,
      EstadoPedido.EN_CAMINO,
      EstadoPedido.COMPLETADO,
      EstadoPedido.CANCELADO
    ];

    this.estadoActual = estados[index];
    this.pagePedidos = 0;

    if (this.searchValue.trim() !== '' || this.tieneFiltrosAplicados()) {
      this.buscar();
    } else {
      this.cargarPedidos();
    }  
  }

  onPageChangePedidos(event: PageEvent): void {
    this.pagePedidos = event.pageIndex;
    this.sizePedidos = event.pageSize;
    if (this.searchValue.trim() !== '' || this.tieneFiltrosAplicados()) {
      this.buscar();
    } else {
      this.cargarPedidos();
    }
  }

  aceptarPedido(pedidoId: number): void {
    this.confirmationService.confirm({
      title: 'Aceptar Pedido',
      message: '¿Estás seguro de aceptar este pedido?',
      confirmText: 'Aceptar',
      type: 'info'
    }).subscribe(confirmed => {
      if (!confirmed) return;

      this.pService.changeState(pedidoId, EstadoPedido.EN_PREPARACION).subscribe({
        next: () => {
          this.toastService.success('✅ Pedido aceptado');
          this.cargarPedidos();
        }
      });
    });
  }

  rechazarPedido(pedidoId: number): void {
    this.confirmationService.confirm({
      title: 'Rechazar Pedido',
      message: '¿Estás seguro de rechazar/cancelar este pedido? Esta acción no se puede deshacer.',
      confirmText: 'Rechazar',
      type: 'danger'
    }).subscribe(confirmed => {
      if (!confirmed) return;

      this.pService.cancel(pedidoId).subscribe({
        next: () => {
          this.toastService.success('✅ Pedido rechazado');
          this.cargarPedidos();
        }
      });
    });
  }

  cambiarEstado(event: { pedidoId: number, nuevoEstado: EstadoPedido }): void {
    const estadoLabel = event.nuevoEstado.replace(/_/g, ' ').toLowerCase();
    
    this.confirmationService.confirm({
      title: 'Cambiar Estado',
      message: `¿Cambiar estado del pedido a "${estadoLabel}"?`,
      confirmText: 'Cambiar',
      type: 'warning'
    }).subscribe(confirmed => {
      if (!confirmed) return;

      this.pService.changeState(event.pedidoId, event.nuevoEstado).subscribe({
        next: () => {
          this.toastService.success(`✅ Estado actualizado a "${estadoLabel}"`);
          this.cargarPedidos();
        }
      });
    });
  }

  onSearchChanged(text: string) {
    this.searchValue = text;
    
    // Si hay texto o hay filtros aplicados, buscar
    if (text.trim() !== '' || this.tieneFiltrosAplicados()) {
      this.searchSubject.next(text);
    } else if (text.trim() === '' && !this.tieneFiltrosAplicados()) {
      // Si borraron todo y no hay filtros, cargar todos
      this.pagePedidos = 0;
      this.cargarPedidos();
    }
  } 
 
  private tieneFiltrosAplicados(): boolean {
    return this.filtroTipoEntrega !== '' ||
           this.filtroFechaInicio !== '' ||
           this.filtroFechaFin !== '';
  }  

  onClearFilters(): void {
    this.searchValue = '';
    this.filtroTipoEntrega = '';
    this.filtroFechaInicio = '';
    this.filtroFechaFin = '';
    this.pagePedidos = 0;
    
    // Limpiar la barra buscadora visualmente
    if (this.barraBuscadora) {
      this.barraBuscadora.onSearchClear();
    }
    
    this.cargarPedidos();
  }  

  buscar() {
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

    this.pService.filtrarPedidosRestaurante(
      this.restaurante?.id!,
      this.searchValue,
      estadoParaBuscar,
      this.filtroTipoEntrega,
      fechaInicioFormatted,
      fechaFinFormatted,
      this.pagePedidos, 
      this.sizePedidos
    ).subscribe(resp => {
      this.arrPedidos = resp.content;
      this.lengthPedidos = resp.totalElements;
    });
  }


  aplicarFiltros(): void {
    this.pagePedidos = 0;
    this.buscar();
  }

  verDetalles(pedidoId: number): void {
    this.router.navigate(['/restaurante/pedidos/detalle', pedidoId]);
  }
}