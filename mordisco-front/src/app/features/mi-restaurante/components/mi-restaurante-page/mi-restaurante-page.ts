import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
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
import { MatPaginator, PageEvent } from "@angular/material/paginator";

@Component({
  selector: 'app-mi-restaurante-page',
  standalone: true,
  imports: [CommonModule, RouterLink, DireccionCardComponent, PromocionCardComponent, CalificacionComponent, HorarioRestauranteComponent, MatPaginator],
  templateUrl: './mi-restaurante-page.html'
})
export class MiRestaurantePageComponent implements OnInit {
  private authService = inject(AuthService)
  private restauranteService = inject(RestauranteService)
  private horarioService = inject(HorarioService)
  private promocionService = inject(PromocionService)
  private calificacionService = inject(CalificacionService)
  private snackBar = inject(MatSnackBar)
  private router = inject(Router)

  restaurante?: RestauranteResponse
  calificaciones?: CalificacionRestauranteReponse[]
  promociones?: PromocionResponse[]
  horariosDeAtencion?: HorarioAtencionResponse[]

  sizePromocion : number = 5;
  pagePromocion : number = 0;
  lengthPromocion : number = 5;

  sizeCalificacion: number = 5;
  pageCalificacion: number = 0;
  lengthCalificacion : number = 5;

  sizeHorario: number = 5;
  pageHorario: number = 0;
  lengthHorario : number = 5;

  isLoadingRestaurante = true;

  ngOnInit(): void {
    this.cargarRestaurante()
  }

  cargarRestaurante(): void {
    const userId = this.authService.currentUser()?.userId;
    
    if (!userId) {
      this.snackBar.open('❌ No se encontró información del usuario', 'Cerrar', { duration: 3000 });
      console.error(userId)
      return;
    }

    this.restauranteService.getByUsuario(userId).subscribe({
      next: (restaurante) => {
        this.restaurante = restaurante;
        this.isLoadingRestaurante = false;
      },
      error: (error) => {
        console.error('Error al cargar restaurante:', error);
        this.snackBar.open('❌ Error al cargar el restaurante', 'Cerrar', { duration: 4000 });
        this.isLoadingRestaurante = false;
      }
    });

  }

  cargarCalificaciones(){
    const idRestaurante = this.restaurante?.id
    if (!idRestaurante) {
      this.snackBar.open('❌ No se encontró información del restaurante', 'Cerrar', { duration: 3000 });
      console.error(idRestaurante)
      return;
    }

    this.calificacionService.getAllByRestauranteId(idRestaurante,this.pageCalificacion,this.sizeCalificacion).subscribe({
        next: (c) => {
          this.calificaciones = c.content
          this.pageCalificacion = c.page
          this.sizeCalificacion = c.size
        },
        error: (e) =>{
        console.error('Error al cargar las calificaciones:', e);
        this.snackBar.open('❌ Error al cargar las calificaciones', 'Cerrar', { duration: 4000 });
        }
    })
  }

  cargarPromociones(){
    const idRestaurante = this.restaurante?.id

    if (!idRestaurante) {
      this.snackBar.open('❌ No se encontró información del restaurante', 'Cerrar', { duration: 3000 });
      console.error(idRestaurante)
      return;
    }

    this.promocionService.get(idRestaurante,this.pageCalificacion,this.sizeCalificacion).subscribe({
        next: (p) => {
          this.promociones = p.content
          this.pagePromocion = p.page
          this.sizePromocion = p.size
        },
        error: (e) =>{
        console.error('Error al cargar las promociones:', e);
        this.snackBar.open('❌ Error al cargar las promociones', 'Cerrar', { duration: 4000 });
        }
    })
  }

  cargarHorarios(){
    const idRestaurante = this.restaurante?.id

    if (!idRestaurante) {
      this.snackBar.open('❌ No se encontró información del restaurante', 'Cerrar', { duration: 3000 });
      console.error(idRestaurante)
      return;
    }

    this.horarioService.getAllByRestauranteId(idRestaurante,this.pageCalificacion,this.sizeCalificacion).subscribe({
        next: (h) => {
          this.horariosDeAtencion = h.content
          this.pageHorario = h.page
          this.sizeHorario = h.size
        },
        error: (e) =>{
        console.error('Error al cargar los horarios:', e);
        this.snackBar.open('❌ Error al cargar los horarios', 'Cerrar', { duration: 4000 });
        }
    })
  }

  onEditarRestaurante(): void {
    // TODO: Navegar al formulario de edición de restaurante
    // this.router.navigate(['/restaurante/editar', this.restaurante?.id]);
    this.snackBar.open('⚠️ Funcionalidad en desarrollo', 'Cerrar', { duration: 2000 });
  }

  onEliminarHorario(horarioId: number): void {
    if (!horarioId) {
      this.snackBar.open('❌ ID de horario inválido', 'Cerrar', { duration: 3000 });
      return;
    }

    if (!confirm('¿Estás seguro de eliminar este horario de atención?')) {
      return;
    }

    this.horarioService.delete(horarioId).subscribe({
      next: () => {
        this.snackBar.open('✅ Horario eliminado correctamente', 'Cerrar', { duration: 3000 });
        this.cargarRestaurante();
      },
      error: (error) => {
        console.error('Error al eliminar horario:', error);
        this.snackBar.open('❌ Error al eliminar el horario', 'Cerrar', { duration: 4000 });
      }
    });
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
        this.cargarRestaurante();
      },
      error: (error) => {
        console.error('Error al eliminar promoción:', error);
        this.snackBar.open('❌ Error al eliminar la promoción', 'Cerrar', { duration: 4000 });
      }
    });
  }

  onEditarHorario(horarioId: number): void {
    // TODO: Navegar al formulario de edición de horario
    // this.router.navigate(['/horario-form', horarioId]);
    this.snackBar.open('⚠️ Funcionalidad en desarrollo', 'Cerrar', { duration: 2000 });
  }

  onEditarPromocion(promocionId: number): void {
    this.router.navigate(['/promocion-form', promocionId]);
    this.snackBar.open('⚠️ Funcionalidad en desarrollo', 'Cerrar', { duration: 2000 });
  }


  onPageChangeHorario(event: PageEvent): void {
    this.pageHorario = event.pageIndex
    this.sizeHorario = event.pageSize;
    this.cargarHorarios();
  }

  onPageChangePromocion(event: PageEvent): void {
    this.pagePromocion = event.pageIndex
    this.sizePromocion = event.pageSize;
    this.cargarPromociones();
  }

  onPageChangeCalificacion(event: PageEvent): void {
    this.pageCalificacion = event.pageIndex
    this.sizeCalificacion = event.pageSize;
    this.cargarCalificaciones();
  }
  
}