import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CalificacionFormRepartidorComponent } from '../calificacion-form-repartidor-component/calificacion-form-repartidor-component';
import { CalificacionFormPedidoComponent } from '../calificacion-form-pedido-component/calificacion-form-pedido-component';
import { NotificationService } from '../../../core/services/notification-service';

@Component({
  selector: 'app-calificacion-form-page',
  imports: [CalificacionFormPedidoComponent, CalificacionFormRepartidorComponent],
  templateUrl: './calificacion-form-page.html',
})
export class CalificacionFormPage implements OnInit{
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private notificationService = inject(NotificationService);
  public pedidoId?: number;
  tipoCalificacion : string| null = null
  
  ngOnInit(): void {
    this.obtenerPedidoID()
  }

  obtenerPedidoID(){
    const idPedido = this.route.snapshot.paramMap.get('id')
    this.tipoCalificacion = this.route.snapshot.paramMap.get('var')

    if (!idPedido || !this.tipoCalificacion) {
      this.router.navigate(['/home']);
      return;
    }
    this.pedidoId=Number(idPedido)
  }


}
