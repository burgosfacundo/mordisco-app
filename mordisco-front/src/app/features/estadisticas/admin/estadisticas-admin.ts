import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';
import { EstadisticasService } from '../../../shared/services/estadisticas/estadisticas-service';
import { AdminEstadisticas } from '../../../shared/models/estadisticas/admin-estadisticas';
import { ToastService } from '../../../core/services/toast-service';

@Component({
  selector: 'app-estadisticas-admin',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatTableModule,
    BaseChartDirective
  ],
  templateUrl: './estadisticas-admin.html',
  styleUrls: ['./estadisticas-admin.css']
})
export class EstadisticasAdminComponent implements OnInit {
  private estadisticasService = inject(EstadisticasService);
  private toastService = inject(ToastService);

  estadisticas = signal<AdminEstadisticas | null>(null);
  isLoading = signal(true);

  // Configuración del gráfico de pastel para métodos de pago
  pieChartData: ChartConfiguration<'pie'>['data'] = {
    labels: [],
    datasets: [{
      data: [],
      backgroundColor: [
        '#FF6384',
        '#36A2EB',
        '#FFCE56',
        '#4BC0C0',
        '#9966FF'
      ]
    }]
  };

  pieChartOptions: ChartConfiguration<'pie'>['options'] = {
    responsive: true,
    plugins: {
      legend: {
        position: 'bottom'
      },
      tooltip: {
        callbacks: {
          label: (context) => {
            const label = context.label || '';
            const value = context.parsed;
            const dataset = context.dataset.data as number[];
            const total = dataset.reduce((acc, val) => acc + val, 0);
            const percentage = ((value / total) * 100).toFixed(1);
            return `${label}: ${value} (${percentage}%)`;
          }
        }
      }
    }
  };

  // Columnas para tablas
  restaurantesColumns = ['nombre', 'pedidos', 'ingresos'];
  repartidoresColumns = ['nombre', 'entregas', 'ganancias'];

  ngOnInit(): void {
    this.cargarEstadisticas();
  }

  private cargarEstadisticas(): void {
    this.estadisticasService.getEstadisticasAdmin().subscribe({
      next: (data) => {
        this.estadisticas.set(data);
        this.configurarGraficoPastel(data);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error cargando estadísticas:', error);
        this.toastService.error('Error al cargar las estadísticas');
        this.isLoading.set(false);
      }
    });
  }

  private configurarGraficoPastel(data: AdminEstadisticas): void {
    this.pieChartData = {
      labels: data.metodosPagoMasUsados.map(m => m.metodoPago),
      datasets: [{
        data: data.metodosPagoMasUsados.map(m => m.cantidad),
        backgroundColor: [
          '#FF6384',
          '#36A2EB',
          '#FFCE56',
          '#4BC0C0',
          '#9966FF'
        ]
      }]
    };
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('es-AR', {
      style: 'currency',
      currency: 'ARS'
    }).format(value);
  }
}
