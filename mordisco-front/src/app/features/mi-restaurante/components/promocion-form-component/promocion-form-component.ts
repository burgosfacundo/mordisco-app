import { Component, inject, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PromocionService } from '../../../../shared/services/promocion/promocion-service';
import { RestauranteService } from '../../../../shared/services/restaurante/restaurante-service';
import { AuthService } from '../../../../shared/services/auth-service';
import PromocionRequest from '../../../../shared/models/promocion/promocion-request';
import { FormValidationService } from '../../../../shared/services/form-validation-service';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

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
  private restauranteService = inject(RestauranteService);
  private formValidationService = inject(FormValidationService)
  private authService = inject(AuthService);
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private snackBar = inject(MatSnackBar);

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
      },
      error: () => {
        this.snackBar.open('❌ Error al cargar la promoción', 'Cerrar', { duration: 4000 });
      }
    });
  }

  onSubmit(): void {
    if (!this.promocionForm.valid) {
      this.markFormGroupTouched(this.promocionForm);
      this.snackBar.open('⚠️ Por favor completa todos los campos correctamente', 'Cerrar', { duration: 3000 });
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
            this.snackBar.open('✅ Promoción actualizada correctamente', 'Cerrar', { duration: 3000 });
            this.router.navigate(['/mi-restaurante']);
          },
          error: (error) => {
            console.error('Error al actualizar promoción:', error);
            this.snackBar.open('❌ Error al actualizar la promoción', 'Cerrar', { duration: 4000 });
            this.isSubmitting = false;
          }
        });
      } else {
        // Crear nueva promoción
        this.promocionService.save(promocionData).subscribe({
          next: () => {
            this.snackBar.open('✅ Promoción creada correctamente', 'Cerrar', { duration: 3000 });
            this.router.navigate(['/mi-restaurante']);
          },
          error: (error) => {
            console.error('Error al crear promoción:', error);
            this.snackBar.open('❌ Error al crear la promoción', 'Cerrar', { duration: 4000 });
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
      if (confirm('¿Descartar los cambios realizados?')) {
        this.router.navigate(['/mi-restaurante']);
      }
    } else {
      this.router.navigate(['/mi-restaurante']);
    }
  }
}