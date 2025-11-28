import { Component, inject } from '@angular/core';
import PedidoResponse from '../../../shared/models/pedido/pedido-response';
import { PedidoService } from '../../../shared/services/pedido/pedido-service';
import { PageEvent, MatPaginator } from '@angular/material/paginator';
import { PedidoCardComponent } from "../../../shared/components/pedido-card-component/pedido-card-component";

@Component({
  selector: 'app-pedidos-admin-page',
  imports: [MatPaginator, PedidoCardComponent],
  templateUrl: './pedidos-admin-page.html',
})
export class PedidosAdminPage {
  private pedidoService = inject(PedidoService);
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
}
