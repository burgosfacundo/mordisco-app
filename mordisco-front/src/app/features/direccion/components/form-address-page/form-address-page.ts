import { Component, inject, OnInit } from '@angular/core';
import { DireccionFormComponent } from "../direccion-form-component/direccion-form-component";
import { DireccionService } from '../../services/direccion-service';
import { AuthService } from '../../../../shared/services/auth-service';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-form-address-page',
  imports: [DireccionFormComponent],
  templateUrl: './form-address-page.html',
})
export class FormAddressPage implements OnInit {
  private dService = inject(DireccionService);
  private authService = inject(AuthService);
  private restauranteService = inject(RestauranteService);
  private snackBar = inject(MatSnackBar);

  modoEdicion = false;
  isLoadingDireccion = true;

  ngOnInit(): void {
    this.cargarDireccion();
  }

  private cargarDireccion(): void {
    const userId = this.authService.currentUser()?.userId;
    const userRole = this.authService.currentUser()?.role;

    if (!userId) {
      this.snackBar.open('❌ Usuario no autenticado', 'Cerrar', { duration: 3000 });
      this.isLoadingDireccion = false;
      return;
    }

    // Si es restaurante, cargar dirección del restaurante
    if (userRole === 'ROLE_RESTAURANTE') {
      this.restauranteService.getByUsuario(userId).subscribe({
        next: (restaurante) => {
          if (restaurante?.direccion) {
            this.modoEdicion = true;
            this.dService.setDireccionToEdit(restaurante.direccion);
          } else {
            this.modoEdicion = false;
            this.dService.clearDireccionToEdit();
          }
          this.isLoadingDireccion = false;
        },
        error: () => {
          this.modoEdicion = false;
          this.isLoadingDireccion = false;
        }
      });
    } else {
      // Para clientes, usar el servicio de direcciones normal
      this.dService.currentDir.subscribe(dir => {
        this.modoEdicion = !!dir;
        this.isLoadingDireccion = false;
      });
    }
  }
}