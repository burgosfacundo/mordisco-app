import { Component, inject, input, ViewChild, viewChild } from '@angular/core';
import { ToastService } from '../../../core/services/toast-service';
import { PedidoService } from '../../../shared/services/pedido/pedido-service';
import { AuthService } from '../../../shared/services/auth-service';
import { RepartidorService } from '../../../shared/services/repartidor/repartidor-service';
import PedidoResponse from '../../../shared/models/pedido/pedido-response';
import { PageEvent, MatPaginator } from '@angular/material/paginator';
import { PedidoCardComponent } from "../../../shared/components/pedido-card-component/pedido-card-component";
import { MatIcon } from "@angular/material/icon";
import { Router } from '@angular/router';
import { ConfirmationService } from '../../../core/services/confirmation-service';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { BarraBuscadoraComponent } from '../../../shared/components/barra-buscadora-component/barra-buscadora-component';
import { PromptService } from '../../../core/services/confirmation-prompt-service';

@Component({
  selector: 'app-entregas-page',
  imports: [MatPaginator, PedidoCardComponent, MatIcon, FormsModule, BarraBuscadoraComponent],
  templateUrl: './entregas-page.html',
})
export class EntregasPage {
  private toastService = inject(ToastService);
  private router = inject(Router)
  private pedidoService = inject(PedidoService);
  private authService = inject(AuthService);
  private repartidorService = inject(RepartidorService);
  private promptService = inject(PromptService)
  pedidos?: PedidoResponse[];
  adminMode = input<boolean>(false)
  admin_idUser = input<number>()
  private searchSubject = new Subject<string>();
  @ViewChild(BarraBuscadoraComponent) barraBuscadora!: BarraBuscadoraComponent;

  filtroEstado: string = '';
  filtroFechaInicio: string = '';
  filtroFechaFin: string = '';
  searchValue: string = '';
  idUser? : number
  
  sizePedidos : number = 5;
  pagePedidos : number = 0;
  length : number = 5;
  
  isLoading = true;

  ngOnInit(): void {
    this.loadPedidos();
    this.searchSubject.pipe(
      debounceTime(500), // Espera 500ms después de que el usuario deje de escribir
      distinctUntilChanged() // Solo emite si el valor cambió
    ).subscribe(() => {
      this.pagePedidos = 0; // Resetear a la primera página
      this.buscar();
    });     
  }

  private loadPedidos(): void {
    if(!this.adminMode()){
      this.idUser= this.authService.currentUser()?.userId
    }else{
      this.idUser=this.admin_idUser()
    }

    if (!this.idUser) {
      this.authService.logout();
      return;
    }

    this.repartidorService.getAllPedidosRepartidor(this.idUser!,this.pagePedidos,this.sizePedidos).subscribe({
        next: (data) => {
          this.pedidos = data.content;
          this.length = data.totalElements;
          this.isLoading = false;
        },error: () => {
          this.isLoading = false;
        }
    });
  }

  marcarRecibido(pedido: PedidoResponse): void {
    this.promptService.show({
      title: 'Marcar como entregado',
      message: 'Indica el PIN del pedido proporcionado por el cliente',
      placeholder: 'Ej: XXXXX',
      required: true,
      confirmText: 'Entrgado',
      type: 'danger'
    }).subscribe(result => {
      if (!result.confirmed) return;
      const PIN : string = result.value ?? ""
      if(PIN === pedido.pin){
        this.pedidoService.marcarComoEntregado(pedido.id).subscribe({
          next: () => {
            this.toastService.success('✅ Pedido marcado como "Completado"');
            this.loadPedidos();
          }
        });
      }else{
          this.promptService.updateValue(""); // limpia el valor
          this.promptService.shakeInput();    // vibra el input
          this.toastService.error("PIN incorrecto");
      }});  
  }



  verDetalle(pedidoId: number): void {
    this.router.navigate(['/repartidor/pedidos/detalle', pedidoId])
  }

  onPageChangePedidos(event: PageEvent): void {
    this.pagePedidos = event.pageIndex
    this.sizePedidos = event.pageSize;
    if (this.searchValue.trim() !== '' || this.tieneFiltrosAplicados()) {
      this.buscar();
    } else {
      this.loadPedidos();
    }  
  }

 aplicarFiltros(): void {
    this.pagePedidos = 0;
    this.buscar();
  }

  private tieneFiltrosAplicados(): boolean {
    return this.filtroEstado !== '' ||
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

    this.pedidoService.filtrarPedidosRepartidor(
      this.idUser!,
      this.searchValue,
      this.filtroEstado,
      fechaInicioFormatted,
      fechaFinFormatted,
      this.pagePedidos, 
      this.sizePedidos
    ).subscribe(resp => {
      this.pedidos = resp.content;
      this.length = resp.totalElements;
    });
  }  

  onClearFilters(): void {
    this.searchValue = '';
    this.filtroEstado = '';
    this.filtroFechaInicio = '';
    this.filtroFechaFin = '';
    this.pagePedidos = 0;
    
    // Limpiar la barra buscadora visualmente
    if (this.barraBuscadora) {
      this.barraBuscadora.onSearchClear();
    }
    
    this.loadPedidos();
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

  navegarAHome() {
    this.router.navigate(['/home'])
  }
}
