import { CommonModule } from '@angular/common';
import { Component, inject, Input } from '@angular/core';
import { UsuarioCardComponent } from '../../../../shared/components/usuario-card-component/usuario-card-component';
import { ProductoPedidoCardComponent } from '../producto-pedido-card-component/producto-pedido-card-component';
import { DireccionCardComponent } from '../../../direccion/components/direccion-card-component/direccion-card-component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PedidoService } from '../../../../shared/services/pedido/pedido-service';
import { ActivatedRoute, Router } from '@angular/router';
import PedidoResponse from '../../../../shared/models/pedido/pedido-response';
import { EstadoPedido } from '../../../../shared/models/enums/estado-pedido';
import { TipoEntrega } from '../../../../shared/models/enums/tipo-entrega';
import UserPedido from '../../../../shared/models/user/user-pedido';

@Component({
  selector: 'app-detalle-pedido-page',
  imports: [CommonModule,UsuarioCardComponent,ProductoPedidoCardComponent,DireccionCardComponent],
  templateUrl: './detalle-pedido-page.html',
  styleUrl: './detalle-pedido-page.css'
})
export class DetallePedidoPage {
   private _snackBar = inject(MatSnackBar);
  private pedidoService = inject(PedidoService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  @Input() usuarioPedido!: UserPedido;

  protected pedido?: PedidoResponse;
  protected isLoading = true;
  readonly tipoEntregaEnum = TipoEntrega;


 formatearEntrega(pedidoResponse : PedidoResponse){
    if(pedidoResponse.tipoEntrega === TipoEntrega.DELIVERY){
      return 'Delivery'
    } else{
      return 'Retiro en el local'
    }
  }
/*
  ngOnInit(): void {
    this.loadPedido();
  }

  loadPedido(): void {
    const id = this.route.snapshot.paramMap.get('id');
    
    if (!id) {
      this._snackBar.open('ID de pedido no válido', 'Cerrar', { duration: 3000 });
      this.router.navigate(['/admin']);
      return;
    }

    this.pedidoService.getById(id).subscribe({
      next: (data) => {
        this.pedido = data;
        this.isLoading = false;
      },
      error: () => {
        this._snackBar.open('Error al cargar el pedido', 'Cerrar', { duration: 3000 });
        this.isLoading = false;
        this.router.navigate(['/admin']);
      }
    });
  }

  volver(): void {
    this.router.navigate(['/admin']);
  }*/
 volver(){}

}
