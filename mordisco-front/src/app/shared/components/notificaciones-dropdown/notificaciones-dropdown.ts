import { Component, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatMenuModule } from '@angular/material/menu';
import { MatBadgeModule } from '@angular/material/badge';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { Router } from '@angular/router';
import { NotificacionService } from '../../services/notificacion/notificacion-service';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-notificaciones-dropdown',
  standalone: true,
  imports: [
    CommonModule,
    MatMenuModule,
    MatBadgeModule,
    MatIconModule,
    MatButtonModule,
    MatDividerModule
  ],
  templateUrl: './notificaciones-dropdown.html',
  styleUrl: './notificaciones-dropdown.css'
})
export class NotificacionesDropdownComponent {
  private notifService = inject(NotificacionService);
  private authService = inject(AuthService);
  private router = inject(Router);

  // Computed del servicio
  notificaciones = this.notifService.notificaciones;
  noLeidas = this.notifService.noLeidas;
  hayNotificaciones = this.notifService.hayNotificaciones;
  conectado = this.notifService.conectado;

  irAPedido(pedidoId: number, index: number): void {
    this.notifService.marcarComoLeida(index);
    
    const user = this.authService.currentUser();
    
    if (user?.role === 'ROLE_RESTAURANTE') {
      this.router.navigate(['/restaurante/pedidos/detalle', pedidoId]);
    } else if (user?.role === 'ROLE_CLIENTE') {
      this.router.navigate(['/cliente/pedidos/detalle', pedidoId]);
    }
  }

  marcarTodasLeidas(): void {
    this.notifService.marcarTodasComoLeidas();
  }

  limpiarTodas(): void {
    if (confirm('¿Estás seguro de eliminar todas las notificaciones?')) {
      this.notifService.limpiarTodas();
    }
  }

  getIconoPorTipo(tipo: string): string {
    return this.notifService.obtenerIconoPorTipo(tipo as any);
  }

  getColorPorTipo(tipo: string): string {
    return this.notifService.obtenerColorPorTipo(tipo as any);
  }
}