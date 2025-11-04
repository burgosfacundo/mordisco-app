import { Component, inject, Input, OnInit } from "@angular/core";
import { RestauranteService } from "../../../../shared/services/restaurante/restaurante-service";
import { AuthService } from "../../../../shared/services/auth-service";
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { MatSnackBar } from "@angular/material/snack-bar";
import { CommonModule } from "@angular/common";
import { MatInputModule } from "@angular/material/input";
import { MatFormFieldModule } from "@angular/material/form-field";
import RestauranteUpdate from "../../../../shared/models/restaurante/restaurante-update";
import RestauranteRequest from "../../../../shared/models/restaurante/restaurante-request";
import DireccionRequest from "../../../../shared/models/direccion/direccion-request";
import { FormValidationService } from "../../../../shared/services/form-validation-service";
import { DireccionFormComponent } from "../../../direccion/components/direccion-form-component/direccion-form-component";

@Component({
  selector: 'app-restaurante-form-component',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatInputModule,
    MatFormFieldModule,
    DireccionFormComponent
],
  templateUrl: './form-restaurante-component.html'
})
export class RestauranteFormComponent implements OnInit {
  private restauranteService = inject(RestauranteService);
  private formValidationService = inject(FormValidationService)
  private authService = inject(AuthService);
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private snackBar = inject(MatSnackBar);

  restauranteForm!: FormGroup;
  isEditMode = false;
  restauranteId?: number;
  isSubmitting = false;
  userId = this.authService.currentUser()?.userId

  direccion? : DireccionRequest 

  ngOnInit(): void {
    this.initForm();
    
    // Detectar si es modo edición
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.restauranteId = +params['id'];
        this.cargarRestaurante(this.restauranteId);
      }
    });
  }

  initForm(): void {
    this.restauranteForm = this.fb.group({
      razonSocial: ['', [Validators.required, Validators.maxLength(50)]],
      activo: ['', [Validators.required]],
      logoUrl: ['', [Validators.required,Validators.maxLength(255)]],
      logoNombre: ['', [Validators.required,Validators.maxLength(50)]]
    });
  }

  cargarRestaurante(id: number): void {
    if (!this.restauranteId) {
      this.snackBar.open('⚠️ Esperando datos del restaurante...', 'Cerrar', { duration: 2000 });
      setTimeout(() => this.cargarRestaurante(id), 1000);
      return;
    }

    this.restauranteService.findById(this.restauranteId).subscribe({
      next: (r) => {
        if (r) {
          this.restauranteForm.patchValue({
            razonSocial: r.razonSocial,
            activo: r.activo,
            logoUrl: r.logo.url,
            logoNombre: r.logo.nombre
          });
        } else {
          this.snackBar.open('❌ Restaurante no encontrado', 'Cerrar', { duration: 3000 });
          this.router.navigate(['/']);
        }
      }
    });
  }

  onSubmit(): void {
    if (!this.restauranteForm.valid && !this.direccion) {
      this.markFormGroupTouched(this.restauranteForm);
      this.snackBar.open('⚠️ Por favor completa todos los campos correctamente', 'Cerrar', { duration: 3000 });
      return;
    }

    if (!this.restauranteId) {
      this.snackBar.open('❌ No se pudo identificar el restaurante', 'Cerrar', { duration: 3000 });
      return;
    }

    this.isSubmitting = true;

    if (this.isEditMode && this.restauranteId && this.userId) {
        const restauranteData: RestauranteUpdate = {
            razonSocial: this.restauranteForm.value.razonSocial,
            activo : this.restauranteForm.value.activo,
            logo: {
                id: this.restauranteForm.value.logo.id,
                url: this.restauranteForm.value.logo.url,
                nombre: this.restauranteForm.value.logo.nombre
            },
            idUsuario: this.userId
        };

      this.restauranteService.put(restauranteData).subscribe({
        next: () => {
          this.snackBar.open('✅ Restaurante actualizado correctamente', 'Cerrar', { duration: 3000 });
          this.router.navigate(['/mi-restaurante']);
        },
        error: (error) => {
          console.error('Error al actualizar restaurante:', error);
          this.snackBar.open('❌ Error al actualizar el restaurante', 'Cerrar', { duration: 4000 });
          this.isSubmitting = false;
        }
      });
    } else {
      if(this.userId && this.direccion){

        const restauranteData: RestauranteRequest = {
          razonSocial: this.restauranteForm.value.razonSocial,
          activo : this.restauranteForm.value.activo,
          logo: {
            url: this.restauranteForm.value.logo.url,
            nombre: this.restauranteForm.value.logo.nombre
          },
          idUsuario: this.userId,
          direccion: {
            calle : this.direccion.calle,
            numero : this.direccion.numero,
            piso : this.direccion.piso,
            depto : this.direccion.depto,
            codigoPostal : this.direccion.codigoPostal,
            referencias : this.direccion.referencias,
            ciudad : this.direccion.ciudad
          }
        };

      
        this.restauranteService.save(restauranteData).subscribe({
          next: () => {
            this.snackBar.open('✅ Restaurante creado correctamente', 'Cerrar', { duration: 3000 });
            this.router.navigate(['/mi-restaurante']);
          },
          error: (error) => {
            console.error('Error al crear restaurante:', error);
            this.snackBar.open('❌ Error al crear el restaurante', 'Cerrar', { duration: 4000 });
            this.isSubmitting = false;
          }
        });
      }
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
      if (confirm('¿Descartar los cambios realizados?')) {
        this.router.navigate(['/mi-restaurante']);
      }
    } else {
      this.router.navigate(['/mi-restaurante']);
    }
  }

  recibirDireccion(e :DireccionRequest){
    this.direccion = e
  }
}