import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService, ToastNotification } from '../../../core/services/notification-service';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast-container-component.html',
  styleUrl: './toast-container-component.css'
})
export class ToastContainerComponent {
  notificationService = inject(NotificationService);

  dismiss(id: string): void {
    this.notificationService.dismiss(id);
  }

  executeAction(notification: ToastNotification): void {
    if (notification.action) {
      notification.action.callback();
      this.dismiss(notification.id);
    }
  }
}
