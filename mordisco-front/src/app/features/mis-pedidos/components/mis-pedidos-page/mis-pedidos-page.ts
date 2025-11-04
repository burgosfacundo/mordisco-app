import { Component, inject, OnInit } from '@angular/core';
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

@Component({
  selector: 'app-mis-pedidos-page',
  imports: [PedidoCardComponent, ReactiveFormsModule, MatPaginator],
  templateUrl: './mis-pedidos-page.html',
  styleUrl: './mis-pedidos-page.css'
})
export class MisPedidosPage implements OnInit {

  private eventListeners: (() => void)[] = [];
  private pService : PedidoService = inject(PedidoService)
  private rService : RestauranteService = inject(RestauranteService)
  private auS : AuthService = inject(AuthService)
  private _snackBar = inject(MatSnackBar);
  private router : Router = inject(Router)

  arrPedidosOriginales? : PedidoResponse[]
  arrPedidosOriginalesCopia? : PedidoResponse[]
  filtroInput = new FormControl('')
  idCurrUser? : number
  restaurante?: RestauranteResponse
  estadoSeleccionado? : string = 'PENDIENTE'

  sizePedidos : number = 5;
  pagePedidos : number = 0;
  lengthPedidos : number = 5;
  isLoadingPedidos = true;


  ngOnInit(): void {
    const resp = this.auS.getCurrentUser()
    this.idCurrUser= resp?.userId

    if(this.idCurrUser){
      this.rService.getByUsuario(this.idCurrUser).subscribe({
        next:(data)=> this.restaurante=data,
        error:(e)=> console.log(e)
      })
      this.setupEventListeners()
      this.listarPedidosEstado('PENDIENTE')
    }
    
  }
  
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
  }

  listarPedidosEstado(estado : string){
    /*
    this.pService.getAllByRestaurante_IdAndEstado(this.restaurante?.id,estado,this.pagePedidos,this.sizePedidos).subscribe({
      next: (p)=> {this.arrPedidosOriginales = p.content,
        this.arrPedidosOriginalesCopia=[...p.content]
        this.lengthPedidos = p.totalElements
        this.isLoadingPedidos=false
      },
      error: () => {
        this._snackBar.open('❌ Error al cargar los restaurantes','Cerrar' , { duration: 3000 });
      }
    })*/
  }

  gestionarPedido(pedido : PedidoResponse){
    /*
    this.pService.setPedidoToEdit(pedido)
    */
    this.router.navigate(['/pedidos/gestionar-pedido'])
  }

  verDetalle(pedido : PedidoResponse){
    this.router.navigate(['/pedidos/detalle-pedido'])
  }

  onPageChangePedidos(event: PageEvent): void {
    this.pagePedidos = event.pageIndex
    this.sizePedidos = event.pageSize;
    if (this.estadoSeleccionado){
      this.listarPedidosEstado(this.estadoSeleccionado);
    }
  }
/*Titulo “Mis pedidos“

Subtitulo “Pendientes“

Lista de componentes /pedido-card-component.ts

Por cada componente boton “Gestionar“ con reedireccion a gestion-pedido-page.ts

 

Subtitulo “Filtro por estado“

Select con filtro por estado

Lista de componentes /pedido-card-component.ts

Por cada componente boton “Ver detalle“ con reedireccion a detalle-pedido-page.ts */
}
