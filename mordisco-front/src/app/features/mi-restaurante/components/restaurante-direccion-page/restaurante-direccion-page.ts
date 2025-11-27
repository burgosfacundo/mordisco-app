import { Component, inject, OnInit, signal, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { DireccionFormComponent } from '../../../direccion/components/direccion-form-component/direccion-form-component';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import { AuthService } from '../../../../shared/services/auth-service';
import DireccionResponse from '../../../../shared/models/direccion/direccion-response';
import { NotificationService } from '../../../../core/services/notification-service';
import { ConfirmDialogComponent } from '../../../../shared/store/confirm-dialog-component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-restaurante-direccion-page',
  standalone: true,
  imports: [
    CommonModule,
    DireccionFormComponent
  ],
  templateUrl: './restaurante-direccion-page.html'
})
export class RestauranteDireccionPage implements OnInit {
  private restauranteService = inject(RestauranteService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private dialog = inject(MatDialog);
  private notificationService = inject(NotificationService);

  @ViewChild('direccionForm') direccionFormComponent?: DireccionFormComponent;

  direccion = signal<DireccionResponse | undefined>(undefined);
  isLoading = signal(true);

  ngOnInit(): void {
    this.cargarDireccionRestaurante();
  }

  private cargarDireccionRestaurante(): void {
    const userId = this.authService.currentUser()?.userId;

    if (!userId) {
      this.router.navigate(['/login']);
      return;
    }

    this.restauranteService.getByUsuario(userId).subscribe({
      next: (restaurante) => {
        this.direccion.set(restaurante.direccion);
        this.isLoading.set(false);
      },
      error: () => {
        this.router.navigate(['/restaurante']);
      }
    });
  }

  handleSaved(): void {
    this.notificationService.success('✅ Dirección del restaurante guardada correctamente');
    this.router.navigate(['/restaurante']);
  }

  handleCancel(): void {
    if (this.direccionFormComponent?.formDirecciones.dirty) {
      const dialogRef = this.dialog.open(ConfirmDialogComponent, {
        width: '400px',
        data: { mensaje: '¿Deseas salir sin guardar los cambios?' }
      });
    
      dialogRef.afterClosed().subscribe(result => {
        if (result === true) {
          this.router.navigate(['/restaurante']);
        }
      });
    } else {
      this.router.navigate(['/restaurante']);
    }
  }
}