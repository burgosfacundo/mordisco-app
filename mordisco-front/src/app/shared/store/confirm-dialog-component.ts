import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import {
  MatDialogTitle,
  MatDialogContent,
  MatDialogActions,
  MatDialogClose,
} from '@angular/material/dialog';

@Component({
  selector: 'app-confirm-dialog',
  template: `
    <h2 mat-dialog-title>Confirmación</h2>

    <mat-dialog-content>
      {{ data.mensaje }}
    </mat-dialog-content>

    <mat-dialog-actions align="end">
    <button mat-button mat-dialog-close="false"  class="bg-red-100! text-red-700! hover:bg-red-800! hover:text-white!">Cancelar</button>
    <button mat-flat-button [mat-dialog-close]="true" class="bg-red-700! text-white! hover:bg-red-800!"> Aceptar</button>
    </mat-dialog-actions>
  `,
  standalone: true,
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatDialogClose,
    MatButtonModule,
  ]
})
export class ConfirmDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { mensaje: string }
  ) {}
}
