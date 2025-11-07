import { Component, inject, Input } from '@angular/core';
import RestauranteResponse from '../../../shared/models/restaurante/restaurante-response';
import { CalificacionService } from '../../../shared/services/calificacion/calificacion-service';

@Component({
  selector: 'app-detalle-restaurante',
  imports: [],
  templateUrl: './detalle-restaurante.html',
  styleUrl: './detalle-restaurante.css'
})
export class DetalleRestaurante {

  @Input() rest! : RestauranteResponse
  private cService : CalificacionService = inject(CalificacionService)
  cantCalificaciones: number = 0;

  ngOnInit() {
    this.cargarNumeroCalificaciones();
  }

  private cargarNumeroCalificaciones(): void {
    this.cService.getAllByRestauranteId(this.rest.id, 0, 5).subscribe({
      next: (data) => this.cantCalificaciones = data.totalElements
    });
  }


}
