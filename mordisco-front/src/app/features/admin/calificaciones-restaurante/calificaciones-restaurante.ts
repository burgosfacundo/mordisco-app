import { Component, inject, ViewChild } from '@angular/core';
import { CalificacionService } from '../../../shared/services/calificacion/calificacion-service';
import { ActivatedRoute } from '@angular/router';
import CalificacionPedidoResponseDTO from '../../../shared/models/calificacion/calificacion-pedido-response-dto';
import { PageEvent, MatPaginator } from '@angular/material/paginator';
import { CalificacionCardAdmin } from "../../../shared/components/calificacion-card-admin/calificacion-card-admin";
import { RestauranteService } from '../../../shared/services/restaurante/restaurante-service';
import RestauranteResponse from '../../../shared/models/restaurante/restaurante-response';
import { FormsModule } from '@angular/forms';
import { BarraBuscadoraComponent } from '../../../shared/components/barra-buscadora-component/barra-buscadora-component';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-calificaciones-restaurante',
  imports: [MatPaginator, CalificacionCardAdmin, FormsModule, BarraBuscadoraComponent],
  templateUrl: './calificaciones-restaurante.html',
})
export class CalificacionesRestaurante {

  private cService = inject(CalificacionService)
  private rService = inject(RestauranteService)
  private route = inject(ActivatedRoute)

  private searchSubject = new Subject<string>();
  @ViewChild(BarraBuscadoraComponent) barraBuscadora!: BarraBuscadoraComponent;

  calificaciones? : CalificacionPedidoResponseDTO[]
  isLoading = false
  idRest? : number
  restaurante? : RestauranteResponse

  filtroEstrellas: string = '';
  filtroFechaInicio: string = '';
  filtroFechaFin: string = '';
  searchValue: string = '';

  // Paginación
  page = 0
  size = 10
  totalElements = 0

  ngOnInit(): void {
    this.idRest = Number(this.route.snapshot.params['id'])

    if (!this.idRest || isNaN(this.idRest)) {
      console.error('ID de restaurante inválido');
      return;
    }
    this.buscarRestaurante()

    this.searchSubject.pipe(
      debounceTime(500), // Espera 500ms después de que el usuario deje de escribir
      distinctUntilChanged() // Solo emite si el valor cambió
    ).subscribe(() => {
      this.page = 0; // Resetear a la primera página
      this.buscar();
    }); 
  }

  buscarRestaurante(){
    if (!this.idRest) return;
    this.rService.findById(this.idRest!).subscribe({
      next: (d)=> {this.restaurante = d,
        this.cargarCalificaciones()
      },
      error:(e)=> {console.log(e)

      }
    })
  }

  cargarCalificaciones(){
    if (!this.idRest) return;
    this.isLoading = true;

    this.cService.getCalificacionesRestaurante(this.idRest,this.page, this.size).subscribe({
      next: (response) => {
        this.calificaciones = response.content;
        this.totalElements = response.totalElements,
        this.isLoading = false},      
        error:(e)=> {console.log("Errorrrr", e),
          this.isLoading = false;
        }
      })
  }


  aplicarFiltros(): void {
    this.page = 0;
    this.buscar();
  }

  private tieneFiltrosAplicados(): boolean {
    return this.filtroEstrellas !== '' ||
           this.filtroFechaInicio !== '' ||
           this.filtroFechaFin !== '';
  }

  buscar() {
    // Convertir fechas de tipo date a LocalDateTime con hora 00:00:00
    const fechaInicioFormatted = this.filtroFechaInicio 
      ? `${this.filtroFechaInicio}T00:00:00` 
      : '';
    
    const fechaFinFormatted = this.filtroFechaFin 
      ? `${this.filtroFechaFin}T23:59:59` 
      : '';
    this.isLoading = true;
    this.cService.filtrarCalificacion(
      this.searchValue,
      this.filtroEstrellas,
      fechaInicioFormatted,
      fechaFinFormatted,
      this.page, 
      this.size
    ).subscribe(resp => {
      this.calificaciones = resp.content;
      this.totalElements = resp.totalElements;
        this.isLoading = false;    
    });
  }


 onSearchChanged(text: string) {
    this.searchValue = text;
    
    // Si hay texto o hay filtros aplicados, buscar
    if (text.trim() !== '' || this.tieneFiltrosAplicados()) {
      this.searchSubject.next(text);
    } else if (text.trim() === '' && !this.tieneFiltrosAplicados()) {
      // Si borraron todo y no hay filtros, cargar todos
      this.page = 0;
      this.cargarCalificaciones();
    }
  }


  onClearFilters(): void {
    this.searchValue = '';
    this.filtroEstrellas = '';
    this.filtroFechaInicio = '';
    this.filtroFechaFin = '';
    this.page = 0;
    if (this.barraBuscadora) {
      this.barraBuscadora.onSearchClear();
    }
    this.cargarCalificaciones();
  }  

  onPageChange(event: PageEvent): void {
    this.page = event.pageIndex
    this.size = event.pageSize
    this.cargarCalificaciones()
  }
}
