import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { GananciaRepartidorService } from '../../services/ganancia-repartidor/ganancia-repartidor-service';
import { GananciaRepartidor, TotalesGanancia } from '../../models/ganancia/ganancia-repartidor.model';

@Component({
  selector: 'app-ganancias-repartidor',
  standalone: true,
  imports: [CommonModule, MatPaginator],
  templateUrl: './ganancias-repartidor.html',
  styleUrl: './ganancias-repartidor.css'
})
export class GananciasRepartidorComponent implements OnInit {
  private gananciaService = inject(GananciaRepartidorService);

  totales = signal<TotalesGanancia | null>(null);
  ganancias = signal<GananciaRepartidor[]>([]);
  isLoading = signal<boolean>(true);
  isLoadingTotales = signal<boolean>(true);

  // Paginación
  page = 0;
  size = 10;
  totalElements = 0;

  ngOnInit(): void {
    this.cargarTotales();
    this.cargarGanancias();
  }

  private cargarTotales(): void {
    this.isLoadingTotales.set(true);
    this.gananciaService.getMisTotales().subscribe({
      next: (totales) => {
        this.totales.set(totales);
        this.isLoadingTotales.set(false);
      },
      error: () => {
        this.isLoadingTotales.set(false);
      }
    });
  }

  private cargarGanancias(): void {
    this.isLoading.set(true);
    this.gananciaService.getMisGanancias(this.page, this.size).subscribe({
      next: (response) => {
        this.ganancias.set(response.content);
        this.totalElements = response.totalElements;
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.page = event.pageIndex;
    this.size = event.pageSize;
    this.cargarGanancias();
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('es-AR', {
      style: 'currency',
      currency: 'ARS'
    }).format(amount);
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('es-AR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    }).format(date);
  }
}
