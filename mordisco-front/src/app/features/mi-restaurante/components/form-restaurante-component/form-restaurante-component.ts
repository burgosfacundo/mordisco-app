import { Component, inject, OnInit } from "@angular/core";
import { MatDialog } from '@angular/material/dialog';
import { RestauranteService } from "../../../../shared/services/restaurante/restaurante-service";
import { AuthService } from "../../../../shared/services/auth-service";
import { ActivatedRoute, Router } from "@angular/router";
import RestauranteUpdate from "../../../../shared/models/restaurante/restaurante-update";
import RestauranteRequest from "../../../../shared/models/restaurante/restaurante-request";
import { FormValidationService } from "../../../../shared/services/form-validation-service";
import { CommonModule } from "@angular/common";
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatInputModule } from "@angular/material/input";
import { MatFormFieldModule } from "@angular/material/form-field";
import { NotificationService } from "../../../../core/services/notification-service";
import { ConfirmDialogComponent } from '../../../../shared/store/confirm-dialog-component';

@Component({
  selector: 'app-restaurante-form-component',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatInputModule,
    MatFormFieldModule
  ],
  templateUrl: './form-restaurante-component.html'
})
export class RestauranteFormComponent implements OnInit {
  private restauranteService = inject(RestauranteService);
  private formValidationService = inject(FormValidationService);
  private authService = inject(AuthService);
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private notificationService = inject(NotificationService);
  private dialog = inject(MatDialog);

  restauranteForm!: FormGroup;
  isEditMode = false;
  restauranteId?: number;
  imagenId? : number
  isSubmitting = false;
  userId = this.authService.currentUser()?.userId;

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.restauranteId = +params['id'];
      }
      
      this.initForm();
      
      if (this.isEditMode && this.restauranteId) {
        this.cargarRestaurante(this.restauranteId);
      }
    });
  }

  initForm(): void {
    const baseFields = {
      razonSocial: ['', [Validators.required, Validators.maxLength(50)]],
      activo: [true, [Validators.required]],
      logoUrl: ['', [Validators.required, Validators.pattern(/^https?:\/\/.+/)]]
    };
    const addressFields = this.isEditMode ? {} : {
      calle: ['', [Validators.required, Validators.maxLength(50)]],
      numero: ['', [Validators.required, Validators.maxLength(50)]],
      piso: ['', [Validators.maxLength(15)]],
      depto: ['', [Validators.maxLength(15)]],
      codigoPostal: ['', [Validators.required, Validators.maxLength(15)]],
      ciudad: ['', [Validators.required, Validators.maxLength(50), Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/)]],
      referencias: ['', [Validators.maxLength(250)]]
    };

    this.restauranteForm = this.fb.group({
      ...baseFields,
      ...addressFields
    });
  }

  cargarRestaurante(id: number): void {
    this.restauranteService.findById(id).subscribe({
      next: (r) => {
          this.restauranteForm.patchValue({
            razonSocial: r.razonSocial,
            activo: r.activo,
            logoUrl: r.logo.url,
          });
          this.imagenId = r.logo.id
      },
      error: () => {
        this.router.navigate(['/restaurante']);
      }
    });
  }

  onSubmit(): void {
    if (this.restauranteForm.invalid) {
      this.markFormGroupTouched(this.restauranteForm);
      this.notificationService.warning('⚠️ Por favor completa todos los campos correctamente');
      return;
    }

    if (!this.userId) {
      return;
    }

    this.isSubmitting = true;
    const formValue = this.restauranteForm.value;

    if (this.isEditMode && this.restauranteId && this.imagenId) {
      const restauranteData: RestauranteUpdate = {
        id : this.restauranteId,
        razonSocial: formValue.razonSocial,
        activo: formValue.activo,
        logo: {
          id: this.imagenId,
          url: formValue.logoUrl,
          nombre: `${formValue.razonSocial} Logo`
        },
      };

      this.restauranteService.put(restauranteData).subscribe({
        next: () => {
          this.notificationService.success('✅ Restaurante actualizado correctamente');
          this.router.navigate(['/']);
        },
        complete: () => {
          this.isSubmitting = false;
        }
      });
    } else {
      const restauranteData: RestauranteRequest = {
        razonSocial: formValue.razonSocial,
        activo: formValue.activo,
        logo: {
          url: formValue.logoUrl,
          nombre: `${formValue.razonSocial} Logo`
        },
        idUsuario: this.userId,
        direccion: {
          calle: formValue.calle,
          numero: formValue.numero,
          piso: formValue.piso || undefined,
          depto: formValue.depto || undefined,
          codigoPostal: formValue.codigoPostal,
          referencias: formValue.referencias || undefined,
          ciudad: formValue.ciudad
        }
      };

      this.restauranteService.save(restauranteData).subscribe({
        next: () => {
          this.notificationService.success('✅ Restaurante creado correctamente');
          this.router.navigate(['/']);
        },
        complete: () => {
          this.isSubmitting = false;
        }
      });
    }
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
      this.restauranteForm.get(fieldName),
      fieldName
    );
  }

  onCancel(): void {
    if (this.restauranteForm.dirty) {
      const dialogRef = this.dialog.open(ConfirmDialogComponent, {
        width: '400px',
        data: { mensaje: '¿Descartar los cambios realizados?' }
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