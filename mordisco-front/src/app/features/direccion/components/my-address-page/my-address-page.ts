import { Component, inject, OnInit, signal, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { DireccionFormComponent } from '../direccion-form-component/direccion-form-component';
import { DireccionCardComponent } from '../direccion-card-component/direccion-card-component';
import { DireccionService } from '../../services/direccion-service';
import DireccionResponse from '../../../../shared/models/direccion/direccion-response';
import { ToastService } from '../../../../core/services/toast-service';
import { ConfirmDialogComponent } from '../../../../shared/store/confirm-dialog-component';

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
  private toastService = inject(ToastService);
  private dialog = inject(MatDialog);

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
      error: () => {
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
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: { mensaje: '¿Estás seguro de eliminar esta dirección?' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result !== true) return;

      this.direccionService.delete(id).subscribe({
        next: () => {
          this.toastService.success('✅ Dirección eliminada correctamente');
          
          if (this.direccionParaEditar()?.id === id) {
            this.handleCancelEdit();
          }
          
          this.cargarDirecciones();
        }
      });
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