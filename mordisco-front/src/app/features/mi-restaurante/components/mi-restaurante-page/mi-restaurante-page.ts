import { Component, inject, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import RestauranteResponse from '../../../../shared/models/restaurante/restaurante-response';
import CalificacionRestauranteReponse from '../../../../shared/models/calificacion/calificacion-restaurante-response';
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
    MatPaginator, 
    RestauranteFormComponent
  ],
  templateUrl: './mi-restaurante-page.html'
})
export class MiRestaurantePageComponent implements OnInit {
  private authService = inject(AuthService);
  private restauranteService = inject(RestauranteService);
  private horarioService = inject(HorarioService);
  private promocionService = inject(PromocionService);
  private calificacionService = inject(CalificacionService);
  private snackBar = inject(MatSnackBar);
  private router = inject(Router);

  restaurante?: RestauranteResponse;
  calificaciones?: CalificacionRestauranteReponse[];
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

  // Paginación Horarios
  sizeHorario: number = 5;
  pageHorario: number = 0;
  lengthHorario: number = 0;

  isLoadingRestaurante = true;

  ngOnInit(): void {
    this.cargarRestaurante();
  }

  private cargarRestaurante(): void {
    const userId = this.authService.currentUser()?.userId;
    
    if (!userId) {
      this.snackBar.open('❌ No se encontró información del usuario', 'Cerrar', { duration: 3000 });
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

    this.calificacionService.getAllByRestauranteId(
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
      idRestaurante, 
      this.pageHorario, 
      this.sizeHorario
    ).subscribe({
      next: (response) => {
        this.horariosDeAtencion = response.content;
        this.lengthHorario = response.totalElements;
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
      this.snackBar.open('❌ Datos insuficientes para eliminar la promoción', 'Cerrar', { duration: 3000 });
      return;
    }

    if (!confirm('¿Estás seguro de eliminar esta promoción?')) {
      return;
    }

    this.promocionService.delete(this.restaurante.id, promocionId).subscribe({
      next: () => {
        this.snackBar.open('✅ Promoción eliminada correctamente', 'Cerrar', { duration: 3000 });
        this.cargarPromociones();
      },
      error: () => {
        this.snackBar.open('❌ Error al eliminar la promoción', 'Cerrar', { duration: 4000 });
      }
    });
  }

  onPageChangeHorario(event: PageEvent): void {
    this.pageHorario = event.pageIndex;
    this.sizeHorario = event.pageSize;
    this.cargarHorarios();
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
}