// prompt-dialog.component.ts
import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { trigger, transition, style, animate } from '@angular/animations';
import { PromptService } from '../../../../core/services/confirmation-prompt-service';

@Component({
  selector: 'app-prompt-dialog',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div 
      *ngIf="isDialogOpen" 
      class="modal-overlay" 
      @fadeIn
      (click)="cancel()">
      
      <div 
        class="modal-content" 
        @slideIn
        (click)="$event.stopPropagation()">
        
        <div class="modal-header">
          <h2>{{ config?.title }}</h2>
        </div>

        <div class="modal-body">
          <p>{{ config?.message }}</p>
          
        <input 
          type="text"
          [placeholder]="config?.placeholder || ''"
          [value]="inputValue"
          (input)="onInputChange($event)"
          (keyup.enter)="confirm()"
          class="dialog-input"
          [class.shake]="shake()"
          autofocus
        />
        </div>

        <div class="modal-actions">
          <button 
            class="btn-cancel" 
            (click)="cancel()">
            {{ config?.cancelText }}
          </button>
          
          <button 
            [class]="'btn-confirm btn-' + config?.type"
            [disabled]="isConfirmDisabled()"
            (click)="confirm()">
            {{ config?.confirmText }}
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .modal-overlay {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 9999;
    }

    .modal-content {
      background: white;
      border-radius: 12px;
      min-width: 400px;
      max-width: 500px;
      box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
      overflow: hidden;
    }

    .modal-header {
      padding: 20px 24px;
      border-bottom: 1px solid #e5e7eb;
    }

    .modal-header h2 {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
      color: #111827;
    }

    .modal-body {
      padding: 24px;
    }

    .modal-body p {
      margin: 0 0 16px 0;
      color: #6b7280;
      line-height: 1.5;
    }

    .dialog-input {
      width: 100%;
      padding: 10px 12px;
      border: 1px solid #d1d5db;
      border-radius: 6px;
      font-size: 14px;
      transition: border-color 0.2s, box-shadow 0.2s;
      box-sizing: border-box;
    }

    .dialog-input:focus {
      outline: none;
      border-color: #3b82f6;
      box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
    }

    .dialog-input::placeholder {
      color: #9ca3af;
    }

    .modal-actions {
      padding: 16px 24px;
      background: #f9fafb;
      display: flex;
      justify-content: flex-end;
      gap: 12px;
      border-top: 1px solid #e5e7eb;
    }

    button {
      padding: 10px 20px;
      border: none;
      border-radius: 6px;
      cursor: pointer;
      font-size: 14px;
      font-weight: 500;
      transition: all 0.2s;
    }

    .btn-cancel {
      background: #f3f4f6;
      color: #374151;
    }

    .btn-cancel:hover {
      background: #e5e7eb;
    }

    .btn-confirm {
      color: white;
    }

    .btn-confirm:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }

    .btn-danger {
      background: #ef4444;
    }

    .btn-danger:hover:not(:disabled) {
      background: #dc2626;
    }

    .btn-warning {
      background: #f59e0b;
    }

    .btn-warning:hover:not(:disabled) {
      background: #d97706;
    }

    .btn-info {
      background: #3b82f6;
    }

    .btn-info:hover:not(:disabled) {
      background: #2563eb;
    }

    .shake {
      animation: shake 0.3s;
    }

    @keyframes shake {
      0% { transform: translateX(0); }
      25% { transform: translateX(-4px); }
      50% { transform: translateX(4px); }
      75% { transform: translateX(-4px); }
      100% { transform: translateX(0); }
    }    
  `],
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('200ms ease-in', style({ opacity: 1 }))
      ]),
      transition(':leave', [
        animate('200ms ease-out', style({ opacity: 0 }))
      ])
    ]),
    trigger('slideIn', [
      transition(':enter', [
        style({ transform: 'translateY(-20px)', opacity: 0 }),
        animate('300ms ease-out', style({ transform: 'translateY(0)', opacity: 1 }))
      ]),
      transition(':leave', [
        animate('200ms ease-in', style({ transform: 'translateY(-20px)', opacity: 0 }))
      ])
    ])
  ]
})
export class PromptDialogComponent {
  // prompt-dialog.component.ts
  private promptService = inject(PromptService);
  shake = signal(false);

  constructor() {
    this.promptService.registerShakeTrigger(() => this.triggerShake());
  }

  get isDialogOpen() {
    return this.promptService.getDialogState();
  }
  
  get config() {
    return this.promptService.getCurrentConfig();
  }
  
  get inputValue() {
    return this.promptService.getInputValue();
  }

  onInputChange(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.promptService.updateValue(value);
  }

  isConfirmDisabled(): boolean {
    return this.config?.required === true && !this.inputValue.trim();
  }

  confirm(): void {
    if (!this.isConfirmDisabled()) {
      this.promptService.confirm();
    }
  }

  cancel(): void {
    this.promptService.cancel();
  }

  triggerShake() {
    this.shake.set(true);
    setTimeout(() => this.shake.set(false), 300);
  }
}