import { Component, inject, OnInit, signal, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DireccionFormComponent } from '../direccion-form-component/direccion-form-component';
import { DireccionCardComponent } from '../direccion-card-component/direccion-card-component';
import { DireccionService } from '../../services/direccion-service';
import DireccionResponse from '../../../../shared/models/direccion/direccion-response';

@Component({
  selector: 'app-my-address-page',
  standalone: true,
  imports: [
    CommonModule,
    DireccionFormComponent,
    DireccionCardComponent
  ],
  templateUrl: './my-address-page.html'
})
export class MyAddressPage implements OnInit {
  private direccionService = inject(DireccionService);
  private snackBar = inject(MatSnackBar);

  @ViewChild('direccionForm') direccionFormComponent?: DireccionFormComponent;

  direcciones = signal<DireccionResponse[]>([]);
  direccionParaEditar = signal<DireccionResponse | undefined>(undefined);
  isLoading = signal(true);

  ngOnInit(): void {
    this.cargarDirecciones();
  }

  private cargarDirecciones(): void {
    this.isLoading.set(true);

    this.direccionService.getMisDirecciones().subscribe({
      next: (direcciones) => {
        this.direcciones.set(direcciones);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error al cargar direcciones:', error);
        this.snackBar.open('❌ Error al cargar las direcciones', 'Cerrar', { duration: 3000 });
        this.isLoading.set(false);
      }
    });
  }

  handleDireccionSaved(): void {
    this.direccionParaEditar.set(undefined);
    this.cargarDirecciones();
    this.scrollToList();
  }

  handleCancelEdit(): void {
    this.direccionParaEditar.set(undefined);
    if (this.direccionFormComponent) {
      this.direccionFormComponent.reset();
    }
  }

  editarDireccion(direccion: DireccionResponse): void {
    this.direccionParaEditar.set(direccion);
    this.scrollToForm();
  }

  eliminarDireccion(id: number): void {
    if (!confirm('¿Estás seguro de eliminar esta dirección?')) {
      return;
    }

    this.direccionService.delete(id).subscribe({
      next: () => {
        this.snackBar.open('✅ Dirección eliminada correctamente', 'Cerrar', { duration: 3000 });
        
        if (this.direccionParaEditar()?.id === id) {
          this.handleCancelEdit();
        }
        
        this.cargarDirecciones();
      },
      error: (error) => {
        console.error('Error al eliminar dirección:', error);
        this.snackBar.open('❌ Error al eliminar la dirección', 'Cerrar', { duration: 4000 });
      }
    });
  }

  private scrollToForm(): void {
    setTimeout(() => {
      const formElement = document.querySelector('app-direccion-form-component');
      formElement?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 100);
  }

  private scrollToList(): void {
    setTimeout(() => {
      const listElement = document.querySelector('.grid');
      listElement?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 300);
  }
}