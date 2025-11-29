import { Component, inject } from '@angular/core';
import PedidoResponse from '../../../shared/models/pedido/pedido-response';
import { PedidoService } from '../../../shared/services/pedido/pedido-service';
import { PageEvent, MatPaginator } from '@angular/material/paginator';
import { PedidoCardComponent } from "../../../shared/components/pedido-card-component/pedido-card-component";
import { PromptService } from '../../../core/services/confirmation-prompt-service';
import BajaLogisticaDTO from '../../../shared/models/BajaLogisticaRequestDTO';
import { ToastService } from '../../../core/services/toast-service';
import { ConfirmationService } from '../../../core/services/confirmation-service';

@Component({
  selector: 'app-pedidos-admin-page',
  imports: [MatPaginator, PedidoCardComponent],
  templateUrl: './pedidos-admin-page.html',
})
export class PedidosAdminPage {
  private pedidoService = inject(PedidoService);
  private promptService = inject(PromptService)
  private confirmationService = inject(ConfirmationService)
  private toastService = inject(ToastService)
  protected pedidos : PedidoResponse[] = [];

  sizePedidos : number = 9;
  pagePedidos : number = 0;
  lengthPedidos : number = 9;

  isLoadingPedidos = true;

  ngOnInit(): void {
    this.loadPedidos()
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
    this.pagePedidos = event.pageIndex
    this.sizePedidos = event.pageSize;
    this.loadPedidos();
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
}

