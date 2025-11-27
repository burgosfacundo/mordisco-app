import { Injectable, signal } from '@angular/core';
import { Observable, Subject } from 'rxjs';

export interface ConfirmationConfig {
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  type?: 'danger' | 'warning' | 'info';
}

export interface ConfirmationResult {
  confirmed: boolean;
}

@Injectable({ providedIn: 'root' })
export class ConfirmationService {
  private confirmationSubject = new Subject<{
    config: ConfirmationConfig;
    result: Subject<boolean>;
  }>();

  // Signal para mostrar/ocultar el diálogo
  private showDialog = signal(false);
  private currentConfig = signal<ConfirmationConfig | null>(null);
  private currentResult: Subject<boolean> | null = null;

  /**
   * Obtiene el estado del diálogo
   */
  getDialogState = this.showDialog.asReadonly();
  
  /**
   * Obtiene la configuración actual
   */
  getCurrentConfig = this.currentConfig.asReadonly();

  /**
   * Muestra un diálogo de confirmación
   */
  confirm(config: ConfirmationConfig): Observable<boolean> {
    const result = new Subject<boolean>();
    
    this.currentConfig.set({
      ...config,
      confirmText: config.confirmText || 'Confirmar',
      cancelText: config.cancelText || 'Cancelar',
      type: config.type || 'warning'
    });
    
    this.currentResult = result;
    this.showDialog.set(true);

    return result.asObservable();
  }

  /**
   * Confirma la acción
   */
  confirmAction(): void {
    if (this.currentResult) {
      this.currentResult.next(true);
      this.currentResult.complete();
    }
    this.closeDialog();
  }

  /**
   * Cancela la acción
   */
  cancelAction(): void {
    if (this.currentResult) {
      this.currentResult.next(false);
      this.currentResult.complete();
    }
    this.closeDialog();
  }

  /**
   * Cierra el diálogo
   */
  private closeDialog(): void {
    this.showDialog.set(false);
    this.currentConfig.set(null);
    this.currentResult = null;
  }
}
