import { Injectable, signal } from '@angular/core';
import { ErrorContext, ErrorSeverity } from './error-handler-service';

export interface ToastNotification {
  id: string;
  message: string;
  severity: ErrorSeverity;
  duration?: number;
  action?: {
    label: string;
    callback: () => void;
  };
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private notifications = signal<ToastNotification[]>([]);
  private idCounter = 0;

  /**
   * Obtiene las notificaciones actuales
   */
  getNotifications = this.notifications.asReadonly();

  /**
   * Muestra una notificación desde un ErrorContext
   */
  showFromError(errorContext: ErrorContext): void {
    if (errorContext.showToUser) {
      this.show(
        errorContext.userMessage,
        errorContext.severity,
        5000,
        errorContext.action
      );
    }
  }

  /**
   * Muestra una notificación de éxito
   */
  success(message: string, duration = 3000): void {
    this.show(message, 'success', duration);
  }

  /**
   * Muestra una notificación de error
   */
  error(message: string, duration = 5000): void {
    this.show(message, 'error', duration);
  }

  /**
   * Muestra una notificación de advertencia
   */
  warning(message: string, duration = 4000): void {
    this.show(message, 'warning', duration);
  }

  /**
   * Muestra una notificación informativa
   */
  info(message: string, duration = 3000): void {
    this.show(message, 'info', duration);
  }

  /**
   * Muestra una notificación genérica
   */
  private show(
    message: string,
    severity: ErrorSeverity,
    duration = 3000,
    action?: { label: string; callback: () => void }
  ): void {
    const notification: ToastNotification = {
      id: `toast-${this.idCounter++}`,
      message,
      severity,
      duration,
      action
    };

    this.notifications.update(current => [...current, notification]);

    // Auto-dismiss después del tiempo especificado
    if (duration > 0) {
      setTimeout(() => {
        this.dismiss(notification.id);
      }, duration);
    }
  }

  /**
   * Cierra una notificación específica
   */
  dismiss(id: string): void {
    this.notifications.update(current => 
      current.filter(n => n.id !== id)
    );
  }

  /**
   * Cierra todas las notificaciones
   */
  dismissAll(): void {
    this.notifications.set([]);
  }
}
