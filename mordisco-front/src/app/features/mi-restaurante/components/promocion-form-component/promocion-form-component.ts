import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
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
import PromocionResponse from '../../../../shared/models/promocion/promocion-response';

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
    this.loadRestauranteId();
    
    // Detectar si es modo edición
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.promocionId = +params['id'];
        this.cargarPromocion(this.promocionId);
      }
    });
  }

  private loadRestauranteId(): void {
    const userId = this.authService.currentUser()?.userId;
    
    if (!userId) {
      this.snackBar.open('❌ Usuario no autenticado', 'Cerrar', { duration: 3000 });
      this.router.navigate(['/login']);
      return;
    }

    this.restauranteService.getByUsuario(userId).subscribe({
      next: (restaurante) => {
        this.restauranteId = restaurante.id;
      },
      error: (error) => {
        console.error('Error al cargar restaurante:', error);
        this.snackBar.open('❌ Error al cargar información del restaurante', 'Cerrar', { duration: 4000 });
        this.router.navigate(['/']);
      }
    });
  }

  initForm(): void {
    this.promocionForm = this.fb.group({
      descripcion: ['', [Validators.required, Validators.maxLength(255)]],
      descuento: ['', [Validators.required, Validators.min(1), Validators.max(100)]],
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required]
    });
  }

  cargarPromocion(id: number): void {
    if (!this.restauranteId) {
      this.snackBar.open('⚠️ Esperando datos del restaurante...', 'Cerrar', { duration: 2000 });
      setTimeout(() => this.cargarPromocion(id), 1000);
      return;
    }

    // Cargar todas las promociones del restaurante y buscar la específica
    this.promocionService.get(this.restauranteId, 0, 100).subscribe({
      next: (response) => {
        const promocion = response.content.find(p => p.id === id);
        
        if (promocion) {
          this.promocionForm.patchValue({
            descripcion: promocion.descripcion,
            descuento: promocion.descuento,
            fechaInicio: new Date(promocion.fechainicio),
            fechaFin: new Date(promocion.fechafin)
          });
        } else {
          this.snackBar.open('❌ Promoción no encontrada', 'Cerrar', { duration: 3000 });
          this.router.navigate(['/mi-restaurante']);
        }
      },
      error: (error) => {
        console.error('Error al cargar promoción:', error);
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

    if (!this.restauranteId) {
      this.snackBar.open('❌ No se pudo identificar el restaurante', 'Cerrar', { duration: 3000 });
      return;
    }

    this.isSubmitting = true;
    
    const promocionData: PromocionRequest = {
      descripcion: this.promocionForm.value.descripcion,
      descuento: parseFloat(this.promocionForm.value.descuento),
      fechainicio: this.formatDate(this.promocionForm.value.fechaInicio),
      fechafin: this.formatDate(this.promocionForm.value.fechaFin),
      restauranteId: this.restauranteId
    };

    if (this.isEditMode && this.promocionId) {
      // Actualizar promoción existente
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

  hasError(field: string, error: string): boolean {
    const control = this.promocionForm.get(field);
    return !!(control?.hasError(error) && (control?.dirty || control?.touched));
  }

  getErrorMessage(field: string): string {
    const control = this.promocionForm.get(field);
    
    if (control?.hasError('required')) {
      return 'Este campo es obligatorio';
    }
    if (control?.hasError('maxlength')) {
      return `Máximo ${control.errors?.['maxlength'].requiredLength} caracteres`;
    }
    if (control?.hasError('min')) {
      return 'El descuento debe ser mayor a 0';
    }
    if (control?.hasError('max')) {
      return 'El descuento no puede ser mayor a 100';
    }
    
    return '';
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