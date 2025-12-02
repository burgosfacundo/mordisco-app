import { Component, inject, ViewChild } from '@angular/core';
import { RestauranteCardComponent } from "../../../shared/components/restaurante-card-component/restaurante-card-component";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { RestauranteService } from '../../../shared/services/restaurante/restaurante-service';
import RestauranteForCard from '../../../shared/models/restaurante/restaurante-for-card';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { BarraBuscadoraComponent } from '../../../shared/components/barra-buscadora-component/barra-buscadora-component';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-restaurante-admin-page',
  imports: [RestauranteCardComponent, MatPaginator, BarraBuscadoraComponent, FormsModule],
  templateUrl: './restaurante-admin-page.html',
})
export class RestauranteAdminPage {
  private restauranteService = inject(RestauranteService);
  protected restaurantes : RestauranteForCard[] = [];
  private router = inject(Router)
  sizeRestaurantes : number = 9;
  pageRestaurantes : number = 0;
  lengthRestaurantes : number = 9;

  private searchSubject = new Subject<string>();
  @ViewChild(BarraBuscadoraComponent) barraBuscadora!: BarraBuscadoraComponent;

  filtroActivo: string = '';
  searchValue: string = '';
  isLoadingRestaurantes = true;

  ngOnInit(): void {
    this.loadRestaurantes()
    this.searchSubject.pipe(
      debounceTime(500),
      distinctUntilChanged()
    ).subscribe(() => {
      this.pageRestaurantes = 0;
      this.buscar();
    });    
  }

  loadRestaurantes(): void { 
    this.restauranteService.getAll(this.pageRestaurantes,this.sizeRestaurantes).subscribe({
      next: (data) => {
        this.restaurantes = data.content;
        this.lengthRestaurantes = data.totalElements
        this.isLoadingRestaurantes = false;
      },
      error: () => {
        this.isLoadingRestaurantes = false;
      }
    });
  }

  onPageChangeRestaurantes(event: PageEvent): void {
    this.pageRestaurantes = event.pageIndex
    this.sizeRestaurantes = event.pageSize;
    if (this.searchValue.trim() !== '' || this.tieneFiltrosAplicados()) {
      this.buscar();
    } else {
      this.loadRestaurantes();
    }  
  }

  aplicarFiltros(): void {
    this.pageRestaurantes = 0;
    this.buscar();
  }

  private tieneFiltrosAplicados(): boolean {
    return this.filtroActivo !== '';
  }

  buscar(): void {
    this.restauranteService.filtrarRestaurantes(
      this.searchValue,
      this.filtroActivo,
      this.pageRestaurantes,
      this.sizeRestaurantes
    ).subscribe({
      next: (resp) => {
        this.restaurantes = resp.content;
        this.lengthRestaurantes = resp.totalElements;
      }
    });
  }

  onSearchChanged(text: string): void {
    this.searchValue = text;

    if (text.trim() !== '' || this.tieneFiltrosAplicados()) {
      this.searchSubject.next(text);
    } else if (text.trim() === '' && !this.tieneFiltrosAplicados()) {
      this.pageRestaurantes = 0;
      this.loadRestaurantes();
    }
  }

  onClearFilters(): void {
    this.searchValue = '';
    this.filtroActivo = '';
    this.pageRestaurantes = 0;

    if (this.barraBuscadora) {
      this.barraBuscadora.onSearchClear();
    }

    this.loadRestaurantes();
  }  

  verMenu(id : number){
    this.router.navigate(['/admin/restaurante', id])
  }

  verCalificaciones(id : number){
    this.router.navigate(['/admin/restaurante/calificaciones', id])
  }
}
