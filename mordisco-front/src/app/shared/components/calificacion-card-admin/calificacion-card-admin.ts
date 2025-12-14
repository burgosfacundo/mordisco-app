import { Component, inject, input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import CalificacionPedidoResponseDTO from '../../models/calificacion/calificacion-pedido-response-dto';
import CalificacionRepartidorResponseDTO from '../../models/calificacion/calificacion-repartidor-response-dto';
import { PedidoService } from '../../services/pedido/pedido-service';
import PedidoResponse from '../../models/pedido/pedido-response';

@Component({
  selector: 'app-calificacion-card-admin',
  imports: [CommonModule],
  templateUrl: './calificacion-card-admin.html'
})
export class CalificacionCardAdmin implements OnInit{
  private pService = inject(PedidoService)
  clienteFlag = input<boolean>(false)
  calificacionP = input<CalificacionPedidoResponseDTO>();
  calificacionR = input<CalificacionRepartidorResponseDTO>();
  ped? :PedidoResponse

  ngOnInit(): void {
    if(this.calificacionP())
      this.findPedido(this.calificacionP()?.pedidoId!)
  
    if(this.calificacionR())
      this.findPedido(this.calificacionR()?.pedidoId!)
    
  }

  calificacionPromedio1Dec(puntajePromedio : number): number {
    return puntajePromedio ? Number(puntajePromedio.toFixed(1)) : 0;
  }

  findPedido(id : number){
    this.pService.getById(id).subscribe({
        next:(d)=> this.ped = d,
      })
  }

}
