// prompt.service.ts
import { Injectable, signal } from '@angular/core';
import { Observable, Subject } from 'rxjs';

export interface PromptConfig {
  title: string;
  message: string;
  placeholder?: string;
  defaultValue?: string;
  required?: boolean;
  confirmText?: string;
  cancelText?: string;
  type?: 'danger' | 'warning' | 'info';
}

export interface PromptResult {
  confirmed: boolean;
  value?: string;
}

@Injectable({ providedIn: 'root' })
export class PromptService {
  private showDialog = signal(false);
  private currentConfig = signal<PromptConfig | null>(null);
  private currentResult: Subject<PromptResult> | null = null;
  private inputValue = signal<string>('');

  getDialogState = this.showDialog.asReadonly();
  getCurrentConfig = this.currentConfig.asReadonly();
  getInputValue = this.inputValue.asReadonly();

  /**
   * Muestra el diálogo con input
   */
  show(config: PromptConfig): Observable<PromptResult> {
    const result = new Subject<PromptResult>();
    
    this.currentConfig.set({
      confirmText: 'Confirmar',
      cancelText: 'Cancelar',
      type: 'info',
      required: false,
      ...config
    });
    
    // Establecer valor inicial si existe
    this.inputValue.set(config.defaultValue || '');
    
    this.currentResult = result;
    this.showDialog.set(true);

    return result.asObservable();
  }

  /**
   * Actualiza el valor del input
   */
  updateValue(value: string): void {
    this.inputValue.set(value);
  }

  /**
   * Confirma la acción
   */
  confirm(): void {
    if (!this.currentResult) return;

    const config = this.currentConfig();
    
    // Validar si es requerido
    if (config?.required && !this.inputValue().trim()) {
      return;
    }

    this.currentResult.next({
      confirmed: true,
      value: this.inputValue()
    });
    this.currentResult.complete();
    this.close();
  }

  /**
   * Cancela la acción
   */
  cancel(): void {
    if (this.currentResult) {
      this.currentResult.next({
        confirmed: false
      });
      this.currentResult.complete();
    }
    this.close();
  }

  /**
   * Cierra el diálogo
   */
  private close(): void {
    this.showDialog.set(false);
    this.currentConfig.set(null);
    this.currentResult = null;
    this.inputValue.set('');
  }
}