import { Component, EventEmitter, inject, Input, OnChanges, OnInit, Output, signal, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { DireccionService } from '../../services/direccion-service';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import DireccionRequest from '../../../../shared/models/direccion/direccion-request';
import DireccionResponse from '../../../../shared/models/direccion/direccion-response';

@Component({
  selector: 'app-direccion-form-component',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './direccion-form-component.html'
})
export class DireccionFormComponent implements OnInit , OnChanges{
  private fb = inject(FormBuilder);
  private direccionService = inject(DireccionService);
  private snackBar = inject(MatSnackBar);
  protected validationService = inject(FormValidationService);

  @Input() direccion?: DireccionResponse;
  @Output() onSaved = new EventEmitter<void>();
  @Output() onCancelled = new EventEmitter<void>();

  formDirecciones!: FormGroup;
  protected isSubmitting = signal(false);
  protected isEditMode = signal(false);

  ngOnInit(): void {
    if (this.direccion) {
      this.isEditMode.set(true);
      this.initializeForm();
      this.loadDireccionData();
    }else {
      this.isEditMode.set(false);
      this.initializeForm();
    }
  }

  ngOnChanges(_changes: SimpleChanges): void {
       if (this.direccion) {
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
      numero: ['', [Validators.required, Validators.maxLength(5),  Validators.pattern(/^[0-9]$/)]],
      piso: ['', [Validators.maxLength(20)]],
      depto: ['', [Validators.maxLength(20)]],
      codigoPostal: ['', [Validators.required, Validators.maxLength(10)]],
      referencias: ['', [Validators.maxLength(250)]],
      ciudad: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50), Validators.pattern(/^[A-Za-z\s]+$/)]]
    });
  }

  private loadDireccionData(): void {
    if (!this.direccion) return;

    this.formDirecciones.patchValue({
      calle: this.direccion.calle,
      numero: this.direccion.numero,
      piso: this.direccion.piso || '',
      depto: this.direccion.depto || '',
      codigoPostal: this.direccion.codigoPostal,
      referencias: this.direccion.referencias || '',
      ciudad: this.direccion.ciudad
    });
  }

  guardarDireccion(): void {
    if (this.formDirecciones.invalid) {
      this.formDirecciones.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);

    const direccionData: DireccionRequest = this.formDirecciones.value;

    if (this.isEditMode() && this.direccion?.id) {
      // Modo edición
      this.direccionService.update(this.direccion.id, direccionData).subscribe({
        next: () => {
          this.snackBar.open('✅ Dirección actualizada correctamente', 'Cerrar', { duration: 3000 });
          this.resetForm();
          this.onSaved.emit();
        },
        error: (error) => {
          console.error('Error:', error);
          this.snackBar.open('❌ Error al actualizar la dirección', 'Cerrar', { duration: 4000 });
          this.isSubmitting.set(false);
        }
      });
    } else {
      // Modo creación
      this.direccionService.save(direccionData).subscribe({
        next: () => {
          this.snackBar.open('✅ Dirección guardada correctamente', 'Cerrar', { duration: 3000 });
          this.resetForm();
          this.onSaved.emit();
        },
        error: (error) => {
          console.error('Error:', error);
          this.snackBar.open('❌ Error al guardar la dirección', 'Cerrar', { duration: 4000 });
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
    this.direccion = undefined;
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