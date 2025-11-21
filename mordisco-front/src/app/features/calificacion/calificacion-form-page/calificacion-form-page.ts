import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CalificacionService } from '../../../shared/services/calificacion/calificacion-service';
import { CalificacionFormRepartidorComponent } from '../calificacion-form-repartidor-component/calificacion-form-repartidor-component';
import { CalificacionFormPedidoComponent } from '../calificacion-form-pedido-component/calificacion-form-pedido-component';

@Component({
  selector: 'app-calificacion-form-page',
  imports: [CalificacionFormPedidoComponent, CalificacionFormRepartidorComponent],
  templateUrl: './calificacion-form-page.html',
})
export class CalificacionFormPage implements OnInit{
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);
  private cService = inject(CalificacionService)
  public pedidoId?: number;
  yaCalificoPedido = false
  yaCalificoRepartidor = false
  tipoCalificacion : string| null = null
  ngOnInit(): void {
    this.obtenerPedidoID()
  }

  obtenerPedidoID(){
    const idPedido = this.route.snapshot.paramMap.get('id')
    this.tipoCalificacion = this.route.snapshot.paramMap.get('var')

    if (!idPedido || !this.tipoCalificacion) {
      this.snackBar.open('ID pedido inválido', 'Cerrar', { duration: 3000 });
      this.router.navigate(['/home']);
      return;
    }
    this.pedidoId=Number(idPedido)
  }


}
