import { Component, inject, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import RestauranteResponse from '../../../../shared/models/restaurante/restaurante-response';
import { AuthService } from '../../../../shared/services/auth-service';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import { HorarioService } from '../../../../shared/services/horario/horario-service';
import { PromocionService } from '../../../../shared/services/promocion/promocion-service';
import { DireccionCardComponent } from "../../../direccion/components/direccion-card-component/direccion-card-component";
import { PromocionCardComponent } from "../../../../shared/components/promocion-card-component/promocion-card-component";
import { CalificacionComponent } from "../../../../shared/components/calificacion-component/calificacion-component";
import PromocionResponse from '../../../../shared/models/promocion/promocion-response';
import HorarioAtencionResponse from '../../../../shared/models/horario/horario-atencion-response';
import { HorarioRestauranteComponent } from "../../../../shared/components/horario-atencion-component/horario-atencion-component";
import { CalificacionService } from '../../../../shared/services/calificacion/calificacion-service';
import { RestauranteFormComponent } from "../form-restaurante-component/form-restaurante-component";
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import CalificacionPedidoResponseDTO from '../../../../shared/models/calificacion/calificacion-pedido-response-dto';
import { NotificationService } from '../../../../core/services/notification-service';
import { ConfirmDialogComponent } from '../../../../shared/store/confirm-dialog-component';

@Component({
  selector: 'app-mi-restaurante-page',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    DireccionCardComponent,
    PromocionCardComponent,
    CalificacionComponent,
    HorarioRestauranteComponent,
    RestauranteFormComponent,
    MatPaginator
],
  templateUrl: './mi-restaurante-page.html'
})
export class MiRestaurantePageComponent implements OnInit {
  private authService = inject(AuthService);
  private restauranteService = inject(RestauranteService);
  private horarioService = inject(HorarioService);
  private promocionService = inject(PromocionService);
  private calificacionService = inject(CalificacionService);
  private notificationService = inject(NotificationService);
  private dialog = inject(MatDialog);
  private router = inject(Router);

  restaurante?: RestauranteResponse;
  calificaciones?: CalificacionPedidoResponseDTO[];
  promociones?: PromocionResponse[];
  horariosDeAtencion?: HorarioAtencionResponse[];

  // Paginación Promociones
  sizePromocion: number = 5;
  pagePromocion: number = 0;
  lengthPromocion: number = 0;

  // Paginación Calificaciones
  sizeCalificacion: number = 5;
  pageCalificacion: number = 0;
  lengthCalificacion: number = 0;

  isLoadingRestaurante = true;

  ngOnInit(): void {
    this.cargarRestaurante();
  }

  private cargarRestaurante(): void {
    const userId = this.authService.currentUser()?.userId;

    if (!userId) {
      this.authService.logout();
      return;
    }

    this.restauranteService.getByUsuario(userId).subscribe({
      next: (restaurante) => {
        this.restaurante = restaurante;
        this.isLoadingRestaurante = false;

        if (restaurante?.id) {
          this.cargarPromociones();
          this.cargarHorarios();
          this.cargarCalificaciones();
        }
      },
      error: (error) => {
        if (error.status === 404) {
          this.restaurante = undefined;
        }
        this.isLoadingRestaurante = false;
      }
    });
  }

  private cargarCalificaciones(): void {
    const idRestaurante = this.restaurante?.id;
    if (!idRestaurante) return;

    this.calificacionService.getCalificacionesRestaurante(
      idRestaurante,
      this.pageCalificacion,
      this.sizeCalificacion
    ).subscribe({
      next: (response) => {
        this.calificaciones = response.content;
        this.lengthCalificacion = response.totalElements;
      }
    });
  }

  private cargarPromociones(): void {
    const idRestaurante = this.restaurante?.id;
    if (!idRestaurante) return;

    this.promocionService.getByIdRestaurante(idRestaurante, this.pagePromocion, this.sizePromocion).subscribe({
      next: (response) => {
        this.promociones = response.content;
        this.lengthPromocion = response.totalElements;
      }
    });
  }

  private cargarHorarios(): void {
    const idRestaurante = this.restaurante?.id;
    if (!idRestaurante) return;

    this.horarioService.getAllByRestauranteId(
      idRestaurante
    ).subscribe({
      next: (response) => {
        this.horariosDeAtencion = response
      }
    });
  }

  onEditarRestaurante(): void {
    if (this.restaurante?.id) {
      this.router.navigate(['/restaurante/editar', this.restaurante.id]);
    }
  }


  onEliminarPromocion(promocionId: number): void {
    if (!promocionId || !this.restaurante?.id) {
      return;
    }

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: { mensaje: '¿Estás seguro de eliminar esta promoción?' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result !== true) return;

      this.promocionService.delete(this.restaurante!.id, promocionId).subscribe({
        next: () => {
          this.notificationService.success('✅ Promoción eliminada correctamente');
          this.cargarPromociones();
        }
      });
    });
  }

  onPageChangePromocion(event: PageEvent): void {
    this.pagePromocion = event.pageIndex;
    this.sizePromocion = event.pageSize;
    this.cargarPromociones();
  }

  onPageChangeCalificacion(event: PageEvent): void {
    this.pageCalificacion = event.pageIndex;
    this.sizeCalificacion = event.pageSize;
    this.cargarCalificaciones();
  }

  calificacionPromedio1Dec(): number {
    const prom = this.restaurante?.estrellas
    return prom ? Number(prom.toFixed(1)) : 0;
  }
}
