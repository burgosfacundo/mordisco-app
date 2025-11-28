import { Component, inject } from '@angular/core';
import { RestauranteCardComponent } from "../../../shared/components/restaurante-card-component/restaurante-card-component";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { RestauranteService } from '../../../shared/services/restaurante/restaurante-service';
import RestauranteForCard from '../../../shared/models/restaurante/restaurante-for-card';

@Component({
  selector: 'app-restaurante-admin-page',
  imports: [RestauranteCardComponent, MatPaginator],
  templateUrl: './restaurante-admin-page.html',
})
export class RestauranteAdminPage {
  private restauranteService = inject(RestauranteService);
  protected restaurantes : RestauranteForCard[] = [];

  sizeRestaurantes : number = 9;
  pageRestaurantes : number = 0;
  lengthRestaurantes : number = 9;

  isLoadingRestaurantes = true;

  ngOnInit(): void {
    this.loadRestaurantes()
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
    this.loadRestaurantes();
  }
}
