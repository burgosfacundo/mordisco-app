import { Component, inject, ViewChild } from '@angular/core';
import PedidoResponse from '../../../shared/models/pedido/pedido-response';
import { PedidoService } from '../../../shared/services/pedido/pedido-service';
import { PageEvent, MatPaginator } from '@angular/material/paginator';
import { PedidoCardComponent } from "../../../shared/components/pedido-card-component/pedido-card-component";
import { PromptService } from '../../../core/services/confirmation-prompt-service';
import BajaLogisticaDTO from '../../../shared/models/BajaLogisticaRequestDTO';
import { ToastService } from '../../../core/services/toast-service';
import { ConfirmationService } from '../../../core/services/confirmation-service';
import { BarraBuscadoraComponent } from '../../../shared/components/barra-buscadora-component/barra-buscadora-component';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-pedidos-admin-page',
  imports: [MatPaginator, PedidoCardComponent, BarraBuscadoraComponent, FormsModule],
  templateUrl: './pedidos-admin-page.html',
})
export class PedidosAdminPage {
  private pedidoService = inject(PedidoService);
  private promptService = inject(PromptService)
  private confirmationService = inject(ConfirmationService)
  private toastService = inject(ToastService)
  private router = inject(Router)
  protected pedidos : PedidoResponse[] = [];

  private searchSubject = new Subject<string>();
  @ViewChild(BarraBuscadoraComponent) barraBuscadora!: BarraBuscadoraComponent;

  filtroEstado: string = '';
  filtroTipoEntrega: string = '';
  filtroFechaInicio: string = '';
  filtroFechaFin: string = '';
  searchValue: string = '';

  sizePedidos : number = 9;
  pagePedidos : number = 0;
  lengthPedidos : number = 9;

  isLoadingPedidos = true;

  ngOnInit(): void {
    this.loadPedidos();
    // Configurar búsqueda con debounce
    this.searchSubject.pipe(
      debounceTime(500), // Espera 500ms después de que el usuario deje de escribir
      distinctUntilChanged() // Solo emite si el valor cambió
    ).subscribe(() => {
      this.pagePedidos = 0; // Resetear a la primera página
      this.buscar();
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
        this.isLoadingPedidos = false;
      }
    });
  }

  onPageChangePedidos(event: PageEvent): void {
    this.pagePedidos = event.pageIndex;
    this.sizePedidos = event.pageSize;

    if (this.searchValue.trim() !== '' || this.tieneFiltrosAplicados()) {
      this.buscar();
    } else {
      this.loadPedidos();
    }
  }

  cancelarPedido(id : number){
    this.promptService.show({
      title: 'Cancelar pedido',
      message: 'Indica el motivo de la cancelacion',
      placeholder: 'Ej: El restaurante se quedo sin el producto que requerias',
      required: true,
      confirmText: 'Cancelar',
      type: 'danger'
    }).subscribe(result => {
      if (!result.confirmed) return;
      const blDTO : BajaLogisticaDTO = {
        motivo : result.value ?? ""
      }
      
      this.pedidoService.darDeBaja(blDTO,id).subscribe({
        next:()=> {
          this.toastService.success('✅ Pedido cancelado');
          this.loadPedidos()},
        error:()=> {
          console.log("No se ha podido cancelar el pedido"),
          this.toastService.success('No se ha podido cancelar el pedido');
        }
      })
    });
  }   
  
  deshacerCancelacion(id :number){
    this.confirmationService.confirm({
      title: 'Deshacer cancelacion',
          message: '¿Estás seguro de deshacer la cancelacion de estte pedido?',
          confirmText: 'Aceptar',
          type: 'danger'
        }).subscribe(confirmed => {
          if (!confirmed) return;
    
          this.pedidoService.reactivar(id).subscribe({
            next: () => {
              this.toastService.success('✅ Pedido nuevamente activo');
              this.loadPedidos();
            },error:()=>{
              console.log("No se ha podido dehacer la cancelacion"),
              this.toastService.success("No se ha podido dehacer la cancelacion");
            }
          });
        });
  }  

  verDetalles(idPedido : number){
    this.router.navigate(['admin/pedidos/detalle', idPedido])
  }

  aplicarFiltros(): void {
    this.pagePedidos = 0;
    this.buscar();
  }

  private tieneFiltrosAplicados(): boolean {
    return this.filtroEstado !== '' ||
           this.filtroTipoEntrega !== '' ||
           this.filtroFechaInicio !== '' ||
           this.filtroFechaFin !== '';
  }

  buscar() {
    // Convertir fechas de tipo date a LocalDateTime con hora 00:00:00
    const fechaInicioFormatted = this.filtroFechaInicio 
      ? `${this.filtroFechaInicio}T00:00:00` 
      : '';
    
    const fechaFinFormatted = this.filtroFechaFin 
      ? `${this.filtroFechaFin}T23:59:59` 
      : '';

    this.pedidoService.filtrarPedidos(
      this.searchValue,
      this.filtroEstado,
      this.filtroTipoEntrega,
      fechaInicioFormatted,
      fechaFinFormatted,
      this.pagePedidos, 
      this.sizePedidos
    ).subscribe(resp => {
      this.pedidos = resp.content;
      this.lengthPedidos = resp.totalElements;
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
      this.loadPedidos();
    }
  }


  onClearFilters(): void {
    this.searchValue = '';
    this.filtroEstado = '';
    this.filtroTipoEntrega = '';
    this.filtroFechaInicio = '';
    this.filtroFechaFin = '';
    this.pagePedidos = 0;
    
    // Limpiar la barra buscadora visualmente
    if (this.barraBuscadora) {
      this.barraBuscadora.onSearchClear();
    }
    
    this.loadPedidos();
  }
}

