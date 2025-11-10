import { Component, inject, OnInit, signal, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DireccionFormComponent } from '../../../direccion/components/direccion-form-component/direccion-form-component';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import { AuthService } from '../../../../shared/services/auth-service';
import DireccionResponse from '../../../../shared/models/direccion/direccion-response';

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
  private snackBar = inject(MatSnackBar);

  @ViewChild('direccionForm') direccionFormComponent?: DireccionFormComponent;

  direccion = signal<DireccionResponse | undefined>(undefined);
  isLoading = signal(true);

  ngOnInit(): void {
    this.cargarDireccionRestaurante();
  }

  private cargarDireccionRestaurante(): void {
    const userId = this.authService.currentUser()?.userId;

    if (!userId) {
      this.snackBar.open('❌ Usuario no autenticado', 'Cerrar', { duration: 3000 });
      this.router.navigate(['/login']);
      return;
    }

    this.restauranteService.getByUsuario(userId).subscribe({
      next: (restaurante) => {
        this.direccion.set(restaurante.direccion);
        this.isLoading.set(false);
      },
      error: () => {
        this.snackBar.open('❌ Error al cargar el restaurante', 'Cerrar', { duration: 3000 });
        this.isLoading.set(false);
        this.router.navigate(['/restaurante']);
      }
    });
  }

  handleSaved(): void {
    this.snackBar.open('✅ Dirección del restaurante guardada correctamente', 'Cerrar', { duration: 3000 });
    this.router.navigate(['/restaurante']);
  }

  handleCancel(): void {
    if (this.direccionFormComponent?.formDirecciones.dirty) {
      if (confirm('¿Descartar los cambios?')) {
        this.router.navigate(['/restaurante']);
      }
    } else {
      this.router.navigate(['/restaurante']);
    }
  }
}