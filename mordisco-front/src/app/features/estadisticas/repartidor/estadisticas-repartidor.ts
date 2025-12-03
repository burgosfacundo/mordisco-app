import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';
import { EstadisticasService } from '../../../shared/services/estadisticas/estadisticas-service';
import { RepartidorEstadisticas } from '../../../shared/models/estadisticas/repartidor-estadisticas';
import { ToastService } from '../../../core/services/toast-service';

@Component({
  selector: 'app-estadisticas-repartidor',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatTabsModule,
    BaseChartDirective
  ],
  templateUrl: './estadisticas-repartidor.html',
  styleUrls: ['./estadisticas-repartidor.css']
})
export class EstadisticasRepartidorComponent implements OnInit {
  private estadisticasService = inject(EstadisticasService);
  private toastService = inject(ToastService);
  private route = inject(ActivatedRoute);

  estadisticas = signal<RepartidorEstadisticas | null>(null);
  isLoading = signal(true);
  repartidorId: number = 0;

  // Gráfico de líneas para ganancias por período
  lineChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [{
      data: [],
      label: 'Ganancias Mensuales',
      fill: true,
      tension: 0.4,
      borderColor: '#FF9800',
      backgroundColor: 'rgba(255, 152, 0, 0.1)'
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

  // Gráfico de barras para pedidos por día
  barChartDataDia: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [{
      data: [],
      label: 'Pedidos por Día',
      backgroundColor: 'rgba(76, 175, 80, 0.6)',
      borderColor: 'rgba(76, 175, 80, 1)',
      borderWidth: 1
    }]
  };

  // Gráfico de barras para pedidos por semana
  barChartDataSemana: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [{
      data: [],
      label: 'Pedidos por Semana',
      backgroundColor: 'rgba(33, 150, 243, 0.6)',
      borderColor: 'rgba(33, 150, 243, 1)',
      borderWidth: 1
    }]
  };

  // Gráfico de barras para pedidos por mes
  barChartDataMes: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [{
      data: [],
      label: 'Pedidos por Mes',
      backgroundColor: 'rgba(156, 39, 176, 0.6)',
      borderColor: 'rgba(156, 39, 176, 1)',
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
        beginAtZero: true,
        ticks: {
          stepSize: 1
        }
      }
    }
  };

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.repartidorId = Number(id);
      this.cargarEstadisticas();
    }
  }

  private cargarEstadisticas(): void {
    this.estadisticasService.getEstadisticasRepartidor(this.repartidorId).subscribe({
      next: (data) => {
        this.estadisticas.set(data);
        this.configurarGraficoLineas(data);
        this.configurarGraficosBarras(data);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error cargando estadísticas:', error);
        this.toastService.error('Error al cargar las estadísticas');
        this.isLoading.set(false);
      }
    });
  }

  private configurarGraficoLineas(data: RepartidorEstadisticas): void {
    this.lineChartData = {
      labels: data.gananciasPorPeriodo.map(g => g.periodo),
      datasets: [{
        data: data.gananciasPorPeriodo.map(g => g.ganancias),
        label: 'Ganancias Mensuales',
        fill: true,
        tension: 0.4,
        borderColor: '#FF9800',
        backgroundColor: 'rgba(255, 152, 0, 0.1)'
      }]
    };
  }

  private configurarGraficosBarras(data: RepartidorEstadisticas): void {
    // Pedidos por día
    this.barChartDataDia = {
      labels: data.pedidosPorDia.map(p => p.periodo),
      datasets: [{
        data: data.pedidosPorDia.map(p => p.cantidad),
        label: 'Pedidos por Día',
        backgroundColor: 'rgba(76, 175, 80, 0.6)',
        borderColor: 'rgba(76, 175, 80, 1)',
        borderWidth: 1
      }]
    };

    // Pedidos por semana
    this.barChartDataSemana = {
      labels: data.pedidosPorSemana.map(p => p.periodo),
      datasets: [{
        data: data.pedidosPorSemana.map(p => p.cantidad),
        label: 'Pedidos por Semana',
        backgroundColor: 'rgba(33, 150, 243, 0.6)',
        borderColor: 'rgba(33, 150, 243, 1)',
        borderWidth: 1
      }]
    };

    // Pedidos por mes
    this.barChartDataMes = {
      labels: data.pedidosPorMes.map(p => p.periodo),
      datasets: [{
        data: data.pedidosPorMes.map(p => p.cantidad),
        label: 'Pedidos por Mes',
        backgroundColor: 'rgba(156, 39, 176, 0.6)',
        borderColor: 'rgba(156, 39, 176, 1)',
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

  formatTime(minutes: number): string {
    if (minutes < 60) {
      return `${Math.round(minutes)} min`;
    }
    const hours = Math.floor(minutes / 60);
    const mins = Math.round(minutes % 60);
    return `${hours}h ${mins}min`;
  }

  getTotalEntregas(stats: RepartidorEstadisticas): number {
    return stats.pedidosPorMes.reduce((sum, p) => sum + p.cantidad, 0);
  }
}
