import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';
import { ValidationError } from '../../models/error/validation-error';
import { MatIconModule } from '@angular/material/icon';

interface DialogData extends ValidationError {
  entityId: number;
}

@Component({
  selector: 'app-pedidos-activos-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './pedidos-activos-dialog-component.html',
  styleUrls: ['./pedidos-activos-dialog-component.css']
})
export class PedidosActivosDialogComponent {
  
  constructor(
    public dialogRef: MatDialogRef<PedidosActivosDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    private router: Router
  ) {}

  getIconPath(): string {
    switch (this.data.entityType) {
      case 'RESTAURANTE':
        return 'M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6';
      case 'PRODUCTO':
        return 'M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253';
      case 'USUARIO':
        return 'M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z';
      default:
        return '';
    }
  }

  getTitle(): string {
    switch (this.data.entityType) {
      case 'RESTAURANTE':
        return 'No se puede eliminar el restaurante';
      case 'PRODUCTO':
        return 'No se puede eliminar el producto';
      case 'USUARIO':
        return 'No se puede eliminar la cuenta';
      default:
        return 'No se puede eliminar';
    }
  }

  getDescription(): string {
    const entity = this.data.entityType?.toLowerCase() || 'elemento';
    return `Este ${entity} tiene <strong>${this.data.cantidad} pedido${this.data.cantidad === 1 ? '' : 's'} activo${this.data.cantidad === 1 ? '' : 's'}</strong> que deben completarse primero.`;
  }

  getOptions(): string[] {
    switch (this.data.entityType) {
      case 'RESTAURANTE':
        return [
          'Espera a que los pedidos se completen',
          'Cancela los pedidos manualmente desde el panel',
          'Desactiva el restaurante temporalmente en lugar de eliminarlo'
        ];
      case 'PRODUCTO':
        return [
          'Espera a que los pedidos con este producto se completen',
          'Marca el producto como "No disponible" para que no se puedan hacer nuevos pedidos',
          'Cancela los pedidos que contienen este producto'
        ];
      case 'USUARIO':
        return [
          'Espera a que tus pedidos activos se completen o cancélalos',
          'Puedes cancelar tus pedidos desde "Mis Pedidos"',
          'Contacta con soporte si necesitas ayuda urgente'
        ];
      default:
        return ['Espera a que los pedidos se completen'];
    }
  }

  getActionRoute(): string {
    switch (this.data.entityType) {
      case 'RESTAURANTE':
        return `/restaurante/${this.data.entityId}/pedidos`;
      case 'PRODUCTO':
        return `/producto/${this.data.entityId}/pedidos-activos`;
      case 'USUARIO':
        return `/cliente/pedidos`;
      default:
        return '/';
    }
  }

  getActionLabel(): string {
    switch (this.data.entityType) {
      case 'RESTAURANTE':
        return 'Ver Pedidos del Restaurante';
      case 'PRODUCTO':
        return 'Ver Pedidos con este Producto';
      case 'USUARIO':
        return 'Ver Mis Pedidos';
      default:
        return 'Ver Pedidos';
    }
  }

  verPedidos(): void {
    this.dialogRef.close();
    this.router.navigate([this.getActionRoute()]);
  }

  cerrar(): void {
    this.dialogRef.close();
  }
}