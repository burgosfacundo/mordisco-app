import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastNotification, ToastService } from '../../../core/services/toast-service';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast-container-component.html',
  styleUrl: './toast-container-component.css'
})
export class ToastContainerComponent {
  toastService = inject(ToastService);

  dismiss(id: string): void {
    this.toastService.dismiss(id);
  }

  executeAction(notification: ToastNotification): void {
    if (notification.action) {
      notification.action.callback();
      this.dismiss(notification.id);
    }
  }
}
