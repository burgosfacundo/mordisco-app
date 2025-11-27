import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConfirmationService } from '../../../core/services/confirmation-service';

@Component({
  selector: 'app-confirmation-dialog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './confirmation-dialog-component.html',
  styleUrl: './confirmation-dialog-component.css',
})
export class ConfirmationDialogComponent {
  confirmationService = inject(ConfirmationService);
  currentConfig = this.confirmationService.getCurrentConfig();
}
