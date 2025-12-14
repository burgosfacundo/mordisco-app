import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';
import { EstadisticasService } from '../../../shared/services/estadisticas/estadisticas-service';
import { RestauranteEstadisticas } from '../../../shared/models/estadisticas/restaurante-estadisticas';
import { ToastService } from '../../../core/services/toast-service';

@Component({
  selector: 'app-estadisticas-restaurante',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatTableModule,
    BaseChartDirective
  ],
  templateUrl: './estadisticas-restaurante.html',
  styleUrls: ['./estadisticas-restaurante.css']
})
export class EstadisticasRestauranteComponent implements OnInit {
  private estadisticasService = inject(EstadisticasService);
  private toastService = inject(ToastService);

  estadisticas = signal<RestauranteEstadisticas | null>(null);
  isLoading = signal(true);

  // Gráfico de líneas para ingresos por período
  lineChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [{
      data: [],
      label: 'Ingresos Mensuales',
      fill: true,
      tension: 0.4,
      borderColor: '#4CAF50',
      backgroundColor: 'rgba(76, 175, 80, 0.1)'
    }]
  };

  lineChartOptions: ChartConfiguration<'line'>['options'] = {
    responsive: true,
    plugins: {
      legend: {
        display: true,
        position: 'top'
      }
    },
    scales: {
      y: {
        beginAtZero: true
      }
    }
  };

  // Gráfico de barras para productos más vendidos
  barChartData: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [{
      data: [],
      label: 'Cantidad Vendida',
      backgroundColor: 'rgba(54, 162, 235, 0.6)',
      borderColor: 'rgba(54, 162, 235, 1)',
      borderWidth: 1
    }]
  };

  barChartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    plugins: {
      legend: {
        display: true,
        position: 'top'
      }
    },
    scales: {
      y: {
        beginAtZero: true
      }
    }
  };

  // Columnas para tabla de productos
  productosColumns = ['nombre', 'cantidad', 'ingresos'];

  ngOnInit(): void {
    this.cargarEstadisticas();
  }

  private cargarEstadisticas(): void {
    this.estadisticasService.getEstadisticasRestaurante().subscribe({
      next: (data) => {
        this.estadisticas.set(data);
        this.configurarGraficoLineas(data);
        this.configurarGraficoBarras(data);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error cargando estadísticas:', error);
        this.toastService.error('Error al cargar las estadísticas');
        this.isLoading.set(false);
      }
    });
  }

  private configurarGraficoLineas(data: RestauranteEstadisticas): void {
    this.lineChartData = {
      labels: data.ingresosPorPeriodo.map(i => i.periodo),
      datasets: [{
        data: data.ingresosPorPeriodo.map(i => i.ingresos),
        label: 'Ingresos Mensuales',
        fill: true,
        tension: 0.4,
        borderColor: '#4CAF50',
        backgroundColor: 'rgba(76, 175, 80, 0.1)'
      }]
    };
  }

  private configurarGraficoBarras(data: RestauranteEstadisticas): void {
    this.barChartData = {
      labels: data.productosMasVendidos.map(p => p.nombre),
      datasets: [{
        data: data.productosMasVendidos.map(p => p.cantidadVendida),
        label: 'Cantidad Vendida',
        backgroundColor: 'rgba(54, 162, 235, 0.6)',
        borderColor: 'rgba(54, 162, 235, 1)',
        borderWidth: 1
      }]
    };
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('es-AR', {
      style: 'currency',
      currency: 'ARS'
    }).format(value);
  }

  // Helper methods para detectar datos vacíos
  hasIncomeData(): boolean {
    const stats = this.estadisticas();
    return stats !== null && stats.ingresosPorPeriodo.length > 0;
  }

  hasProductsData(): boolean {
    const stats = this.estadisticas();
    return stats !== null && stats.productosMasVendidos.length > 0;
  }

  hasAnyData(): boolean {
    const stats = this.estadisticas();
    return stats !== null && (
      stats.ingresosTotales > 0 ||
      stats.ingresosPorPeriodo.length > 0 ||
      stats.productosMasVendidos.length > 0
    );
  }
}
