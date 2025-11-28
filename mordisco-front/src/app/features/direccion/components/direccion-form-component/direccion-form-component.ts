import { Component, inject, input, OnChanges, OnInit, output, signal, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { DireccionService } from '../../services/direccion-service';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import DireccionRequest from '../../../../shared/models/direccion/direccion-request';
import DireccionResponse from '../../../../shared/models/direccion/direccion-response';
import { ToastService } from '../../../../core/services/toast-service';

@Component({
  selector: 'app-direccion-form-component',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './direccion-form-component.html'
})
export class DireccionFormComponent implements OnInit , OnChanges{
  private fb = inject(FormBuilder);
  private direccionService = inject(DireccionService);
  private toastService = inject(ToastService);
  protected validationService = inject(FormValidationService);

  direccion = input<DireccionResponse>();
  onSaved = output<void>();
  onCancelled = output<void>();

  formDirecciones!: FormGroup;
  protected isSubmitting = signal(false);
  protected isEditMode = signal(false);

  ngOnInit(): void {
    if (this.direccion()) {
      this.isEditMode.set(true);
      this.initializeForm();
      this.loadDireccionData();
    }else {
      this.isEditMode.set(false);
      this.initializeForm();
    }
  }

  ngOnChanges(_changes: SimpleChanges): void {
       if (this.direccion()) {
      this.isEditMode.set(true);
      this.initializeForm();
      this.loadDireccionData();
    }else {
      this.isEditMode.set(false);
      this.initializeForm();
    }
  }

  private initializeForm(): void {
    this.formDirecciones = this.fb.group({
      calle: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      numero: ['', [Validators.required, Validators.maxLength(5),  Validators.pattern(/^[0-9]+$/)]],
      piso: ['', [Validators.maxLength(20)]],
      depto: ['', [Validators.maxLength(20)]],
      codigoPostal: ['', [Validators.required, Validators.maxLength(10)]],
      referencias: ['', [Validators.maxLength(250)]],
      ciudad: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50), Validators.pattern(/^[A-Za-z\s]+$/)]]
    });
  }

  private loadDireccionData(): void {
    if (!this.direccion()) return;

    this.formDirecciones.patchValue({
      calle: this.direccion()?.calle,
      numero: this.direccion()?.numero,
      piso: this.direccion()?.piso || '',
      depto: this.direccion()?.depto || '',
      codigoPostal: this.direccion()?.codigoPostal,
      referencias: this.direccion()?.referencias || '',
      ciudad: this.direccion()?.ciudad
    });
  }

  guardarDireccion(): void {
    if (this.formDirecciones.invalid) {
      this.formDirecciones.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);

    const dir = this.direccion()
    const direccionData: DireccionRequest = this.formDirecciones.value;

    if (this.isEditMode() && dir?.id) {
      // Modo edición
      this.direccionService.update(dir.id, direccionData).subscribe({
        next: () => {
          this.toastService.success('✅ Dirección actualizada correctamente');
          this.resetForm();
          this.onSaved.emit();
        },
        error: () => {
          this.isSubmitting.set(false);
        }
      });
    } else {
      // Modo creación
      this.direccionService.save(direccionData).subscribe({
        next: () => {
          this.toastService.success('✅ Dirección guardada correctamente');
          this.resetForm();
          this.onSaved.emit();
        },
        error: () => {
          this.isSubmitting.set(false);
        }
      });
    }
  }

  cancelar(): void {
    this.resetForm();
    this.onCancelled.emit();
  }

  private resetForm(): void {
    this.formDirecciones.reset();
    this.isEditMode.set(false);
    this.isSubmitting.set(false);
  }

  public reset(): void {
    this.resetForm();
  }

  getError(fieldName: string): string | null {
    return this.validationService.getErrorMessage(
      this.formDirecciones.get(fieldName),
      fieldName
    );
  }
}