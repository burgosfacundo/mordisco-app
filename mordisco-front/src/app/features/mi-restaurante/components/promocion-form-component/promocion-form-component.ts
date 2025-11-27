import { Component, inject, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatNativeDateModule } from '@angular/material/core';
import { PromocionService } from '../../../../shared/services/promocion/promocion-service';
import PromocionRequest from '../../../../shared/models/promocion/promocion-request';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NotificationService } from '../../../../core/services/notification-service';
import { ConfirmDialogComponent } from '../../../../shared/store/confirm-dialog-component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-promocion-form-component',
  standalone: true,
  imports: [
    CommonModule, 
    ReactiveFormsModule,
    MatDatepickerModule,
    MatInputModule,
    MatFormFieldModule,
    MatNativeDateModule
  ],
  templateUrl: './promocion-form-component.html'
})
export class PromocionFormComponent implements OnInit {
  private promocionService = inject(PromocionService);
  private formValidationService = inject(FormValidationService)
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private notificationService = inject(NotificationService);
  private dialog = inject(MatDialog)

  promocionForm!: FormGroup;
  isEditMode = false;
  promocionId?: number;
  restauranteId?: number;
  isSubmitting = false;

  ngOnInit(): void {
    this.initForm();
    
    this.promocionId = this.route.snapshot.params['id'];
    this.restauranteId = this.route.snapshot.params['idRestaurante']

    if (this.promocionId) {
      this.isEditMode = true;
      this.cargarPromocion();
    }
  }

  initForm(): void {
    this.promocionForm = this.fb.group({
      descripcion: ['', [Validators.required, Validators.maxLength(255)]],
      descuento: ['', [Validators.required, Validators.min(1), Validators.max(100)]],
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required]
    });
  }

  cargarPromocion(): void {
    if (!this.promocionId) {
      this.router.navigate(['/restaurante']);
      return;
    }

    this.promocionService.getById(this.promocionId).subscribe({
      next: (p) => {
        this.promocionForm.patchValue({
          descripcion: p.descripcion,
          descuento: p.descuento,
          fechaInicio: new Date(p.fechaInicio),
          fechaFin: new Date(p.fechaFin)
        });

        this.restauranteId = p.restaurante_Id;
      }
    });
  }

  onSubmit(): void {
    if (!this.promocionForm.valid) {
      this.markFormGroupTouched(this.promocionForm);
      this.notificationService.warning('⚠️ Por favor completa todos los campos correctamente');
      return;
    }

    console.log(this.restauranteId);
    
    if (this.restauranteId) {
      this.isSubmitting = true;
      
      const promocionData: PromocionRequest = {
        descripcion: this.promocionForm.value.descripcion,
        descuento: parseFloat(this.promocionForm.value.descuento),
        fechaInicio: this.formatDate(this.promocionForm.value.fechaInicio),
        fechaFin: this.formatDate(this.promocionForm.value.fechaFin),
        restauranteId: this.restauranteId
      };


      if (this.isEditMode && this.promocionId) {
        this.promocionService.put(promocionData, this.promocionId).subscribe({
          next: () => {
            this.notificationService.success('✅ Promoción actualizada correctamente');
            this.router.navigate(['/mi-restaurante']);
          },
          error: () => {
            this.isSubmitting = false;
          }
        });
      } else {
        // Crear nueva promoción
        this.promocionService.save(promocionData).subscribe({
          next: () => {
            this.notificationService.success('✅ Promoción creada correctamente');
            this.router.navigate(['/mi-restaurante']);
          },
          error: () => {
            this.isSubmitting = false;
          }
        });
      }
    }
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
      control?.markAsDirty();
    });
  }

  getError(fieldName: string): string | null {
    return this.formValidationService.getErrorMessage(
      this.promocionForm.get(fieldName),
      fieldName
    );
  }
  onCancel(): void {
    if (this.promocionForm.dirty) {
      const dialogRef = this.dialog.open(ConfirmDialogComponent, {
        width: '400px',
        data: { mensaje: '¿Deseas salir sin guardar los cambios?' }
      });

      dialogRef.afterClosed().subscribe(result => {
        if (result === true) {
          this.router.navigate(['/restaurante']);
        }
      });
    } else {
      this.router.navigate(['/restaurante']);
    }
  }
}