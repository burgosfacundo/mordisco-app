import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { PedidoCardComponent } from '../../../../shared/components/pedido-card-component/pedido-card-component';
import PedidoResponse from '../../../../shared/models/pedido/pedido-response';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { PedidoService } from '../../../../shared/services/pedido/pedido-service';
import { AuthService } from '../../../../shared/services/auth-service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PageEvent, MatPaginator } from '@angular/material/paginator';
import RestauranteResponse from '../../../../shared/models/restaurante/restaurante-response';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import { Router } from '@angular/router';
import { EstadoPedido } from '../../../../shared/models/enums/estado-pedido';

@Component({
  selector: 'app-mis-pedidos-page',
  imports: [PedidoCardComponent, ReactiveFormsModule, MatPaginator],
  templateUrl: './mis-pedidos-page.html',
  styleUrl: './mis-pedidos-page.css'
})
export class MisPedidosPage implements OnInit, OnDestroy {

  //private eventListeners: (() => void)[] = [];
  private pService : PedidoService = inject(PedidoService)
  private rService : RestauranteService = inject(RestauranteService)
  private auS : AuthService = inject(AuthService)
  private _snackBar = inject(MatSnackBar);
  private router : Router = inject(Router)

  EstadoPedido = EstadoPedido
  arrPedidos? : PedidoResponse[]
  estado = new FormControl<string>('TODOS')
  idCurrUser? : number
  restaurante?: RestauranteResponse
  estadoSeleccionado : string | null = this.estado.value

  sizePedidos : number = 5;
  pagePedidos : number = 0;
  lengthPedidos : number = 5;
  isLoadingPedidos = true;
  isUpdating = false;


  ngOnInit(): void {
    const resp = this.auS.getCurrentUser()
    this.idCurrUser= resp?.userId

    if(this.idCurrUser){
      this.rService.getByUsuario(this.idCurrUser).subscribe({
        next:(data)=> {this.restaurante=data,
          this.listarTodos()
        },
        error:(e)=> {
          console.log(e);
          this.isLoadingPedidos = false; // En caso de error, también paramos el spinner
        }
      })
     // this.setupEventListeners()
    }
    this.estado.valueChanges.subscribe((nuevoEstado) => {
      if (nuevoEstado) {
        if(nuevoEstado === "TODOS"){
          this.estadoSeleccionado = nuevoEstado;
          console.log(this.estadoSeleccionado)
          this.listarTodos()}
        else{
          this.estadoSeleccionado = nuevoEstado;
          console.log(this.estadoSeleccionado)
          this.listarPedidosEstado(nuevoEstado);
        }
      }
    });
  }

  ngOnDestroy(): void {
   // this.eventListeners.forEach(cleanup => cleanup());
  }
  /*
  private setupEventListeners(): void {
    const estadoListener = (event: Event) => {
      const { detail } = event as CustomEvent<any>;
      this.estadoSeleccionado = typeof detail === 'string' ? detail : detail?.nombre;
      if (this.estadoSeleccionado) {
        this.listarPedidosEstado(this.estadoSeleccionado);
      }
    };

    window.addEventListener('estado-changed', estadoListener);

    this.eventListeners.push(
      () => window.removeEventListener('estado-changed', estadoListener),
    );
  }*/

  listarPedidosEstado(estado : string){
    if(!this.restaurante) return;
      this.pService.getAllByRestaurante_IdAndEstado(this.restaurante?.id,estado,this.pagePedidos,this.sizePedidos).subscribe({
        next: (p)=> {this.arrPedidos = p.content,
          this.lengthPedidos = p.totalElements
          this.isLoadingPedidos=false
        },
        error: () => {
          this._snackBar.open('❌ Error al cargar los pedidos','Cerrar' , { duration: 3000 });
          this.isLoadingPedidos = false;
        }
      })
    
  }

  listarTodos(){
    if(!this.restaurante) return;
    this.pService.fildAllByRestaurante_Id(this.restaurante.id, this.pagePedidos,this.sizePedidos).subscribe({
        next: (p)=> {this.arrPedidos = p.content,
          this.lengthPedidos = p.totalElements
          this.isLoadingPedidos=false
        },
        error: () => {
          this._snackBar.open('❌ Error al cargar los pedidos','Cerrar' , { duration: 3000 });
          this.isLoadingPedidos = false;
        }
    })
  }
  
  onPageChangePedidos(event: PageEvent): void {
    this.pagePedidos = event.pageIndex
    this.sizePedidos = event.pageSize;
    if (this.estadoSeleccionado === 'TODOS'){
      this.listarTodos()
    }else if(this.estadoSeleccionado){
      this.listarPedidosEstado(this.estadoSeleccionado);
    }
  }

  confirmacionCambioEstado(pedido : PedidoResponse, estado : EstadoPedido){
    if (!pedido.id) return;
    const confirmar = confirm('¿Estás seguro de cambiar este estado?');
    if (confirmar && estado !==  EstadoPedido.CANCELADO) {
      this.cambiarEstado(pedido,estado)
    }else{
      this.cancelarPedido(pedido)
    }
  }

  cambiarEstado(pedido : PedidoResponse, estado : EstadoPedido){
    this.pService.changeState(pedido.id,estado).subscribe({
      next:(data)=> {console.log(data)
        if (this.estadoSeleccionado === 'TODOS') {
          this.listarTodos();
        } else if (this.estadoSeleccionado) {
          this.listarPedidosEstado(this.estadoSeleccionado);
        }
      },
      error:(e)=>{console.log(e), 
        alert("No puedes cambiar al estado anterior")
      }
    })

  }

  cancelarPedido(pedido : PedidoResponse){
    this.pService.cancel(pedido.id).subscribe({
      next:(data)=> {console.log(data)
        if (this.estadoSeleccionado === 'TODOS') {
          this.listarTodos();
        } else if (this.estadoSeleccionado) {
          this.listarPedidosEstado(this.estadoSeleccionado);
        }
      },
      error:(e)=>{console.log(e), 
        alert("No puedes cambiar al estado anterior")
      }
    })
  }

  verDetalle(pedido : PedidoResponse){
    this.router.navigate(['/pedidos/detalle-pedido'])
  }
}
