import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CalificacionFormRepartidorComponent } from '../calificacion-form-repartidor-component/calificacion-form-repartidor-component';
import { CalificacionFormPedidoComponent } from '../calificacion-form-pedido-component/calificacion-form-pedido-component';
import PedidoResponse from '../../../shared/models/pedido/pedido-response';
import { PedidoService } from '../../../shared/services/pedido/pedido-service';

@Component({
  selector: 'app-calificacion-form-page',
  imports: [CalificacionFormPedidoComponent, CalificacionFormRepartidorComponent],
  templateUrl: './calificacion-form-page.html',
})
export class CalificacionFormPage implements OnInit{
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private pedidoService = inject(PedidoService);
  pedidoResponse? : PedidoResponse;
  tipoCalificacion : string| null = null
  
  ngOnInit(): void {
    this.obtenerPedido()
  }

  obtenerPedido(){
    const idPedido = this.route.snapshot.paramMap.get('id')
    this.tipoCalificacion = this.route.snapshot.paramMap.get('var')

    if (!idPedido || !this.tipoCalificacion) {
      this.router.navigate(['/home']);
      return;
    }
    this.pedidoService.getById(Number(idPedido)).subscribe({
      next:(d)=> this.pedidoResponse=d,
      error:(e)=>{
        console.log("No se ha podido encontrar el pedido ", e),
        this.router.navigate(['/home']);
      }
    })


  }


}
